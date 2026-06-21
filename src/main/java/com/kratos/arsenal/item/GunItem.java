package com.kratos.arsenal.item;

import com.kratos.arsenal.gun.AttachmentType;
import com.kratos.arsenal.gun.FireMode;
import com.kratos.arsenal.gun.GunData;
import com.kratos.arsenal.gun.GunEngine;
import com.kratos.arsenal.gun.GunType;
import com.kratos.arsenal.registry.ModSounds;
import com.kratos.arsenal.util.InventoryHelper;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

import java.util.List;

/**
 * Arma de fuego genérica. Toda la configuración (daño, cadencia, cargador,
 * modos de disparo, etc.) procede de su {@link GunType}, de modo que esta
 * misma clase implementa las 10 armas del mod.
 *
 * <p>Flujo de disparo:
 * <ol>
 *     <li>{@link #use} comienza el "uso" del objeto (mantener clic derecho).</li>
 *     <li>{@link #usageTick} dispara según el modo (semi / ráfaga / automático),
 *         respetando la cadencia y el cargador.</li>
 *     <li>{@link #onStoppedUsing} reinicia el estado al soltar el botón.</li>
 * </ol>
 */
public class GunItem extends Item {

    private final GunType type;

    public GunItem(GunType type, Settings settings) {
        super(settings.maxCount(1).maxDamage(0));
        this.type = type;
    }

    public GunType getGunType() {
        return type;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        // Inicializa el modo de disparo la primera vez.
        if (stack.getNbt() == null || !stack.getNbt().contains("FireMode")) {
            GunData.setFireMode(stack, type.getDefaultMode());
        }

        if (GunData.isReloading(stack)) {
            return TypedActionResult.fail(stack);
        }

        if (GunData.getAmmo(stack) <= 0) {
            // Disparo en seco: intenta recargar automáticamente en el servidor.
            if (!world.isClient && user instanceof ServerPlayerEntity serverPlayer) {
                if (!startReload(serverPlayer, stack)) {
                    world.playSound(null, user.getX(), user.getY(), user.getZ(),
                            ModSounds.DRY_FIRE, SoundCategory.PLAYERS, 0.8f, 1.0f);
                }
            }
            return TypedActionResult.fail(stack);
        }

        // Mantener el objeto "en uso" permite el disparo automático.
        user.setCurrentHand(hand);
        return TypedActionResult.consume(stack);
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (world.isClient || !(user instanceof ServerPlayerEntity player)) {
            return;
        }
        if (GunData.isReloading(stack)) {
            return;
        }
        if (GunData.getAmmo(stack) <= 0) {
            player.stopUsingItem();
            startReload(player, stack);
            return;
        }

        long now = player.getWorld().getTime();
        if (now - GunData.getLastShot(stack) < type.getFireCooldownTicks()) {
            return; // Respeta la cadencia de disparo.
        }

        FireMode mode = GunData.getFireMode(stack, type);
        switch (mode) {
            case SEMI -> {
                if (GunData.hasFiredThisHold(stack)) {
                    return; // Un disparo por pulsación.
                }
                doFire(player, stack, now);
                GunData.setFiredThisHold(stack, true);
            }
            case BURST -> {
                if (!GunData.hasFiredThisHold(stack)) {
                    GunData.setBurstLeft(stack, type.getBurstCount());
                    GunData.setFiredThisHold(stack, true);
                }
                if (GunData.getBurstLeft(stack) > 0) {
                    doFire(player, stack, now);
                    GunData.setBurstLeft(stack, GunData.getBurstLeft(stack) - 1);
                }
            }
            case AUTO -> doFire(player, stack, now);
        }
    }

    /** Ejecuta un disparo: descuenta munición y delega los efectos en {@link GunEngine}. */
    private void doFire(ServerPlayerEntity player, ItemStack stack, long now) {
        GunData.setLastShot(stack, now);
        GunData.setAmmo(stack, GunData.getAmmo(stack) - 1);
        GunEngine.fire(player, stack, type);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        // Al soltar el botón se reinicia el estado de la pulsación.
        GunData.setFiredThisHold(stack, false);
        GunData.setBurstLeft(stack, 0);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        // Completa la recarga cuando ha transcurrido el tiempo necesario.
        if (!world.isClient && entity instanceof ServerPlayerEntity player && GunData.isReloading(stack)) {
            if (world.getTime() >= GunData.getReloadEnd(stack)) {
                finishReload(player, stack);
            }
        }
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.NONE;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 72000;
    }

    @Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return false;
    }

    // ================= Acciones invocadas desde la red =================

    /**
     * Inicia la recarga si procede.
     *
     * @return {@code true} si la recarga ha comenzado.
     */
    public static boolean startReload(ServerPlayerEntity player, ItemStack stack) {
        if (!(stack.getItem() instanceof GunItem gun)) {
            return false;
        }
        GunType type = gun.type;
        if (GunData.isReloading(stack) || GunData.getAmmo(stack) >= type.getMagSize()) {
            return false;
        }
        if (InventoryHelper.countAmmo(player, type.getAmmoType()) <= 0) {
            // Sin munición: clic en seco.
            player.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(),
                    ModSounds.DRY_FIRE, SoundCategory.PLAYERS, 0.8f, 1.0f);
            return false;
        }
        GunData.setReloading(stack, true);
        GunData.setReloadEnd(stack, player.getWorld().getTime() + type.getReloadTicks());
        player.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(),
                ModSounds.RELOAD, SoundCategory.PLAYERS, 1.0f, 1.0f);
        return true;
    }

    /** Finaliza la recarga: consume munición del inventario y rellena el cargador. */
    private static void finishReload(ServerPlayerEntity player, ItemStack stack) {
        if (!(stack.getItem() instanceof GunItem gun)) {
            return;
        }
        GunType type = gun.type;
        int needed = type.getMagSize() - GunData.getAmmo(stack);
        int taken = InventoryHelper.removeAmmo(player, type.getAmmoType(), needed);
        GunData.setAmmo(stack, GunData.getAmmo(stack) + taken);
        GunData.setReloading(stack, false);
    }

    /** Cambia al siguiente modo de disparo disponible para esta arma. */
    public static void cycleFireMode(ServerPlayerEntity player, ItemStack stack) {
        if (!(stack.getItem() instanceof GunItem gun)) {
            return;
        }
        FireMode current = GunData.getFireMode(stack, gun.type);
        FireMode next = FireMode.next(current, gun.type.getModes());
        GunData.setFireMode(stack, next);
        player.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(),
                ModSounds.FIRE_MODE_SWITCH, SoundCategory.PLAYERS, 0.6f, 1.0f);
    }

    /** Actualiza el estado de apuntado (ADS) del arma. */
    public static void setAiming(ItemStack stack, boolean aiming) {
        if (stack.getItem() instanceof GunItem) {
            GunData.setAiming(stack, aiming);
        }
    }

    // ================= Tooltip =================

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("tooltip.kratos_arsenal.ammo",
                GunData.getAmmo(stack), type.getMagSize()).formatted(Formatting.GOLD));
        tooltip.add(Text.translatable("tooltip.kratos_arsenal.caliber",
                Text.translatable("ammo.kratos_arsenal." + type.getAmmoType().getItemName())).formatted(Formatting.GRAY));
        tooltip.add(Text.translatable("tooltip.kratos_arsenal.damage",
                String.format("%.1f", type.getDamage())).formatted(Formatting.RED));
        tooltip.add(Text.translatable("tooltip.kratos_arsenal.firemode",
                Text.translatable("firemode.kratos_arsenal." + GunData.getFireMode(stack, type).getId()))
                .formatted(Formatting.AQUA));

        // Accesorios montados.
        for (AttachmentType.Slot slot : AttachmentType.Slot.values()) {
            AttachmentType attachment = GunData.getAttachment(stack, slot);
            if (attachment != null) {
                tooltip.add(Text.translatable("tooltip.kratos_arsenal.attachment",
                        Text.translatable("item.kratos_arsenal." + attachment.getId()))
                        .formatted(Formatting.DARK_AQUA));
            }
        }
        tooltip.add(Text.translatable("tooltip.kratos_arsenal.help").formatted(Formatting.DARK_GRAY));
    }
}

package com.kratos.arsenal.client;

import com.kratos.arsenal.gun.GunData;
import com.kratos.arsenal.gun.GunType;
import com.kratos.arsenal.item.GunItem;
import com.kratos.arsenal.util.InventoryHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

/**
 * HUD del arma: munición en cargador / reserva, indicador de recarga y modo
 * de disparo. Sólo se dibuja cuando el jugador empuña un arma del mod.
 */
@Environment(EnvType.CLIENT)
public final class GunHudRenderer {

    private GunHudRenderer() {}

    public static void register() {
        HudRenderCallback.EVENT.register(GunHudRenderer::render);
    }

    private static void render(DrawContext context, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        if (player == null || client.options.hudHidden) {
            return;
        }

        ItemStack stack = player.getMainHandStack();
        if (!(stack.getItem() instanceof GunItem gun)) {
            return;
        }
        GunType type = gun.getGunType();

        int screenW = client.getWindow().getScaledWidth();
        int screenH = client.getWindow().getScaledHeight();

        int ammo = GunData.getAmmo(stack);
        int reserve = InventoryHelper.countAmmo(player, type.getAmmoType());

        // --- Texto de munición (abajo a la derecha) ---
        String ammoText = ammo + " / " + reserve;
        int color = ammo <= 0 ? 0xFFFF5555 : (ammo <= type.getMagSize() * 0.25 ? 0xFFFFAA00 : 0xFFFFFFFF);
        int textX = screenW - client.textRenderer.getWidth(ammoText) - 10;
        int textY = screenH - 30;
        context.drawTextWithShadow(client.textRenderer, Text.literal(ammoText), textX, textY, color);

        // --- Modo de disparo ---
        Text modeText = Text.translatable("firemode.kratos_arsenal." + GunData.getFireMode(stack, type).getId());
        int modeX = screenW - client.textRenderer.getWidth(modeText) - 10;
        context.drawTextWithShadow(client.textRenderer, modeText, modeX, screenH - 42, 0xFF55FFFF);

        // --- Nombre del arma ---
        Text gunName = Text.translatable("item.kratos_arsenal." + type.getId());
        int nameX = screenW - client.textRenderer.getWidth(gunName) - 10;
        context.drawTextWithShadow(client.textRenderer, gunName, nameX, screenH - 54, 0xFFAAAAAA);

        // --- Indicador de recarga ---
        if (GunData.isReloading(stack)) {
            long now = player.getWorld().getTime();
            long end = GunData.getReloadEnd(stack);
            float progress = MathHelper.clamp(
                    1.0f - (float) (end - now) / Math.max(1, type.getReloadTicks()), 0f, 1f);

            int barW = 80;
            int barH = 6;
            int barX = screenW / 2 - barW / 2;
            int barY = screenH / 2 + 16;
            context.fill(barX - 1, barY - 1, barX + barW + 1, barY + barH + 1, 0xAA000000);
            context.fill(barX, barY, barX + (int) (barW * progress), barY + barH, 0xFF33CC33);
            context.drawTextWithShadow(client.textRenderer,
                    Text.translatable("hud.kratos_arsenal.reloading"),
                    barX, barY - 12, 0xFFFFFFFF);
        }
    }
}

package com.kratos.arsenal.gun;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

/**
 * Utilidades para leer y escribir el estado de un arma en el NBT de su
 * {@link ItemStack}. Centralizar el acceso al NBT evita errores con las
 * claves y mantiene el resto del código limpio.
 */
public final class GunData {

    // Claves NBT.
    private static final String AMMO = "Ammo";
    private static final String FIRE_MODE = "FireMode";
    private static final String RELOADING = "Reloading";
    private static final String RELOAD_END = "ReloadEnd";
    private static final String LAST_SHOT = "LastShot";
    private static final String AIMING = "Aiming";
    private static final String BURST_LEFT = "BurstLeft";
    private static final String FIRED_THIS_HOLD = "FiredThisHold";
    private static final String ATTACHMENTS = "Attachments";

    private GunData() {}

    // ---- Munición en el cargador ----

    public static int getAmmo(ItemStack stack) {
        return stack.getOrCreateNbt().getInt(AMMO);
    }

    public static void setAmmo(ItemStack stack, int ammo) {
        stack.getOrCreateNbt().putInt(AMMO, Math.max(0, ammo));
    }

    // ---- Modo de disparo ----

    public static FireMode getFireMode(ItemStack stack, GunType type) {
        NbtCompound nbt = stack.getNbt();
        if (nbt == null || !nbt.contains(FIRE_MODE)) {
            return type.getDefaultMode();
        }
        return FireMode.byId(nbt.getString(FIRE_MODE));
    }

    public static void setFireMode(ItemStack stack, FireMode mode) {
        stack.getOrCreateNbt().putString(FIRE_MODE, mode.getId());
    }

    // ---- Recarga ----

    public static boolean isReloading(ItemStack stack) {
        return stack.getOrCreateNbt().getBoolean(RELOADING);
    }

    public static void setReloading(ItemStack stack, boolean reloading) {
        stack.getOrCreateNbt().putBoolean(RELOADING, reloading);
    }

    public static long getReloadEnd(ItemStack stack) {
        return stack.getOrCreateNbt().getLong(RELOAD_END);
    }

    public static void setReloadEnd(ItemStack stack, long time) {
        stack.getOrCreateNbt().putLong(RELOAD_END, time);
    }

    // ---- Control de cadencia ----

    public static long getLastShot(ItemStack stack) {
        return stack.getOrCreateNbt().getLong(LAST_SHOT);
    }

    public static void setLastShot(ItemStack stack, long time) {
        stack.getOrCreateNbt().putLong(LAST_SHOT, time);
    }

    // ---- Apuntado (ADS) ----

    public static boolean isAiming(ItemStack stack) {
        return stack.getOrCreateNbt().getBoolean(AIMING);
    }

    public static void setAiming(ItemStack stack, boolean aiming) {
        stack.getOrCreateNbt().putBoolean(AIMING, aiming);
    }

    // ---- Ráfaga (estado transitorio) ----

    public static int getBurstLeft(ItemStack stack) {
        return stack.getOrCreateNbt().getInt(BURST_LEFT);
    }

    public static void setBurstLeft(ItemStack stack, int value) {
        stack.getOrCreateNbt().putInt(BURST_LEFT, value);
    }

    // ---- Disparo semiautomático: evita el "auto-fire" manteniendo pulsado ----

    public static boolean hasFiredThisHold(ItemStack stack) {
        return stack.getOrCreateNbt().getBoolean(FIRED_THIS_HOLD);
    }

    public static void setFiredThisHold(ItemStack stack, boolean fired) {
        stack.getOrCreateNbt().putBoolean(FIRED_THIS_HOLD, fired);
    }

    // ---- Accesorios ----

    /** Monta un accesorio en su ranura (sustituye al que hubiera). */
    public static void setAttachment(ItemStack stack, AttachmentType attachment) {
        NbtCompound attachments = stack.getOrCreateNbt().getCompound(ATTACHMENTS);
        attachments.putString(attachment.getSlot().name(), attachment.getId());
        stack.getOrCreateNbt().put(ATTACHMENTS, attachments);
    }

    /** Devuelve el accesorio montado en la ranura indicada, o {@code null}. */
    public static AttachmentType getAttachment(ItemStack stack, AttachmentType.Slot slot) {
        NbtCompound nbt = stack.getNbt();
        if (nbt == null || !nbt.contains(ATTACHMENTS)) {
            return null;
        }
        NbtCompound attachments = nbt.getCompound(ATTACHMENTS);
        if (!attachments.contains(slot.name())) {
            return null;
        }
        return AttachmentType.byId(attachments.getString(slot.name()));
    }

    public static boolean hasAttachment(ItemStack stack, AttachmentType.Slot slot) {
        return getAttachment(stack, slot) != null;
    }

    /** Factor de zoom de la mira montada (1.0 si no hay mira). */
    public static float getZoomFactor(ItemStack stack) {
        AttachmentType sight = getAttachment(stack, AttachmentType.Slot.SIGHT);
        return sight != null ? sight.getZoomFactor() : 1.0f;
    }

    /** {@code true} si el arma lleva silenciador montado. */
    public static boolean isSilenced(ItemStack stack) {
        AttachmentType barrel = getAttachment(stack, AttachmentType.Slot.BARREL);
        return barrel != null && barrel.isSilencer();
    }
}

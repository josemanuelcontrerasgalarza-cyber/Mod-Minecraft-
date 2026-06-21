package com.kratos.arsenal.registry;

import com.kratos.arsenal.KratosArsenal;
import com.kratos.arsenal.gun.GunType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

import java.util.EnumMap;
import java.util.Map;

/**
 * Registro de todos los eventos de sonido del mod.
 *
 * <p>Cada arma tiene su propio sonido de disparo ({@code <arma>_shoot}). Además
 * existen sonidos compartidos para recarga, disparo en seco, cambio de modo,
 * disparo silenciado y headshot.</p>
 */
public final class ModSounds {

    private static final Map<GunType, SoundEvent> GUN_SHOOT = new EnumMap<>(GunType.class);

    public static SoundEvent SHOOT_SILENCED;
    public static SoundEvent RELOAD;
    public static SoundEvent DRY_FIRE;
    public static SoundEvent FIRE_MODE_SWITCH;
    public static SoundEvent HEADSHOT;

    private ModSounds() {}

    public static void register() {
        for (GunType type : GunType.values()) {
            GUN_SHOOT.put(type, create(type.getId() + "_shoot"));
        }
        SHOOT_SILENCED = create("shoot_silenced");
        RELOAD = create("reload");
        DRY_FIRE = create("dry_fire");
        FIRE_MODE_SWITCH = create("fire_mode_switch");
        HEADSHOT = create("headshot");
    }

    /** Devuelve el sonido de disparo del arma indicada. */
    public static SoundEvent forGun(GunType type) {
        return GUN_SHOOT.getOrDefault(type, RELOAD);
    }

    private static SoundEvent create(String name) {
        Identifier id = KratosArsenal.id(name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }
}

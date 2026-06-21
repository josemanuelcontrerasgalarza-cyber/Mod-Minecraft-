package com.kratos.arsenal.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Estado de cliente compartido entre el tick, el HUD y el mixin de zoom.
 */
@Environment(EnvType.CLIENT)
public final class ClientGunState {

    /** Factor de zoom aplicado al FOV (1.0 = sin zoom; <1.0 = acercar). */
    public static volatile double zoom = 1.0;

    /** {@code true} mientras el jugador mantiene la tecla de apuntar. */
    public static volatile boolean aiming = false;

    private ClientGunState() {}
}

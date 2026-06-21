package com.kratos.arsenal.client;

import com.kratos.arsenal.gun.GunData;
import com.kratos.arsenal.item.GunItem;
import com.kratos.arsenal.network.ModNetworking;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;

/**
 * Punto de entrada de cliente: teclas, HUD, zoom (ADS) y recepción del
 * retroceso enviado por el servidor.
 */
@Environment(EnvType.CLIENT)
public class KratosArsenalClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ModKeyBindings.register();
        GunHudRenderer.register();
        registerRecoilReceiver();
        registerTick();
    }

    /** Aplica el retroceso recibido del servidor a la cámara del jugador. */
    private void registerRecoilReceiver() {
        ClientPlayNetworking.registerGlobalReceiver(ModNetworking.RECOIL, (client, handler, buf, sender) -> {
            float pitch = buf.readFloat();
            float yaw = buf.readFloat();
            client.execute(() -> {
                if (client.player != null) {
                    // Restar el pitch sube la mira (retroceso vertical).
                    client.player.setPitch(client.player.getPitch() - pitch);
                    client.player.setYaw(client.player.getYaw() + yaw);
                }
            });
        });
    }

    private void registerTick() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ClientPlayerEntity player = client.player;
            if (player == null) {
                ClientGunState.aiming = false;
                ClientGunState.zoom = 1.0;
                return;
            }

            ItemStack stack = player.getMainHandStack();
            boolean holdingGun = stack.getItem() instanceof GunItem;

            // Tecla de recarga.
            while (ModKeyBindings.reload.wasPressed()) {
                if (holdingGun) {
                    ClientPlayNetworking.send(ModNetworking.RELOAD, PacketByteBufs.empty());
                }
            }

            // Tecla de cambio de modo de disparo.
            while (ModKeyBindings.fireMode.wasPressed()) {
                if (holdingGun) {
                    ClientPlayNetworking.send(ModNetworking.TOGGLE_FIRE_MODE, PacketByteBufs.empty());
                }
            }

            // Apuntar (mantener pulsado).
            boolean wantAim = holdingGun && ModKeyBindings.aim.isPressed();
            if (wantAim != ClientGunState.aiming) {
                ClientGunState.aiming = wantAim;
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeBoolean(wantAim);
                ClientPlayNetworking.send(ModNetworking.SET_AIM, buf);
            }

            // Factor de zoom según la mira montada.
            if (wantAim) {
                float factor = GunData.getZoomFactor(stack); // >= 1.0
                ClientGunState.zoom = 1.0 / Math.max(1.0f, factor);
            } else {
                ClientGunState.zoom = 1.0;
            }
        });
    }
}

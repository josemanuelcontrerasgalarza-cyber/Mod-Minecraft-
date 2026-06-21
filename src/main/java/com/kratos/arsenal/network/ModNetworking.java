package com.kratos.arsenal.network;

import com.kratos.arsenal.KratosArsenal;
import com.kratos.arsenal.item.GunItem;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

/**
 * Canales de red del mod.
 *
 * <p>Cliente → Servidor: recarga, cambio de modo de disparo y estado de
 * apuntado. Servidor → Cliente: retroceso aplicado tras cada disparo.</p>
 */
public final class ModNetworking {

    // C2S
    public static final Identifier RELOAD = KratosArsenal.id("reload");
    public static final Identifier TOGGLE_FIRE_MODE = KratosArsenal.id("toggle_fire_mode");
    public static final Identifier SET_AIM = KratosArsenal.id("set_aim");

    // S2C
    public static final Identifier RECOIL = KratosArsenal.id("recoil");

    private ModNetworking() {}

    /** Registra los receptores del lado servidor. */
    public static void registerServerReceivers() {
        ServerPlayNetworking.registerGlobalReceiver(RELOAD, (server, player, handler, buf, sender) ->
                server.execute(() -> {
                    ItemStack stack = player.getMainHandStack();
                    if (stack.getItem() instanceof GunItem) {
                        GunItem.startReload(player, stack);
                    }
                }));

        ServerPlayNetworking.registerGlobalReceiver(TOGGLE_FIRE_MODE, (server, player, handler, buf, sender) ->
                server.execute(() -> {
                    ItemStack stack = player.getMainHandStack();
                    if (stack.getItem() instanceof GunItem) {
                        GunItem.cycleFireMode(player, stack);
                    }
                }));

        ServerPlayNetworking.registerGlobalReceiver(SET_AIM, (server, player, handler, buf, sender) -> {
            boolean aiming = buf.readBoolean();
            server.execute(() -> {
                ItemStack stack = player.getMainHandStack();
                if (stack.getItem() instanceof GunItem) {
                    GunItem.setAiming(stack, aiming);
                }
            });
        });
    }

    /** Envía el retroceso al cliente que ha disparado. */
    public static void sendRecoil(ServerPlayerEntity player, float pitch, float yaw) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeFloat(pitch);
        buf.writeFloat(yaw);
        ServerPlayNetworking.send(player, RECOIL, buf);
    }
}

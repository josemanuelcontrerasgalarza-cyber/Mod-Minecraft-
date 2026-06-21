package com.kratos.arsenal.util;

import com.kratos.arsenal.gun.AmmoType;
import com.kratos.arsenal.registry.ModItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Utilidades para contar y consumir munición del inventario del jugador.
 */
public final class InventoryHelper {

    private InventoryHelper() {}

    /** Cuenta cuántas balas del tipo indicado tiene el jugador. */
    public static int countAmmo(PlayerEntity player, AmmoType type) {
        Item ammoItem = ModItems.getAmmoItem(type);
        if (ammoItem == null) {
            return 0;
        }
        int total = 0;
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.isOf(ammoItem)) {
                total += stack.getCount();
            }
        }
        return total;
    }

    /**
     * Retira hasta {@code max} balas del inventario.
     *
     * @return cantidad realmente retirada.
     */
    public static int removeAmmo(PlayerEntity player, AmmoType type, int max) {
        if (player.getAbilities().creativeMode) {
            return max; // En creativo la munición es ilimitada.
        }
        Item ammoItem = ModItems.getAmmoItem(type);
        if (ammoItem == null || max <= 0) {
            return 0;
        }
        int remaining = max;
        for (int i = 0; i < player.getInventory().size() && remaining > 0; i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.isOf(ammoItem)) {
                int take = Math.min(remaining, stack.getCount());
                stack.decrement(take);
                remaining -= take;
            }
        }
        return max - remaining;
    }
}

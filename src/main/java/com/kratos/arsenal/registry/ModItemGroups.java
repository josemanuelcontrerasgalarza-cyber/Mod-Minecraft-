package com.kratos.arsenal.registry;

import com.kratos.arsenal.KratosArsenal;
import com.kratos.arsenal.gun.GunType;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;

/**
 * Grupo creativo (pestaña) que agrupa todo el contenido del mod.
 */
public final class ModItemGroups {

    public static ItemGroup KRATOS_GROUP;

    private ModItemGroups() {}

    public static void register() {
        KRATOS_GROUP = Registry.register(Registries.ITEM_GROUP, KratosArsenal.id("general"),
                FabricItemGroup.builder()
                        .icon(() -> new ItemStack(ModItems.getGunItem(GunType.AK47)))
                        .displayName(Text.translatable("itemgroup.kratos_arsenal.general"))
                        .entries((displayContext, entries) -> {
                            // Armas
                            ModItems.GUNS.forEach(entries::add);
                            // Munición
                            ModItems.AMMO.forEach(entries::add);
                            // Accesorios
                            ModItems.ATTACHMENTS.forEach(entries::add);
                            // Materiales
                            ModItems.MATERIALS.forEach(entries::add);
                            // Bloques
                            entries.add(ModBlocks.TITANIUM_ORE);
                            entries.add(ModBlocks.DEEPSLATE_TITANIUM_ORE);
                            entries.add(ModBlocks.TITANIUM_BLOCK);
                            entries.add(ModBlocks.STEEL_BLOCK);
                        })
                        .build());
    }
}

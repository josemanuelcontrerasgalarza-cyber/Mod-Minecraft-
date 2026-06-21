package com.kratos.arsenal.registry;

import com.kratos.arsenal.KratosArsenal;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

/**
 * Registro de bloques del mod: mineral de titanio (normal y de pizarra) y
 * bloques de almacenamiento de acero y titanio.
 */
public final class ModBlocks {

    public static Block TITANIUM_ORE;
    public static Block DEEPSLATE_TITANIUM_ORE;
    public static Block TITANIUM_BLOCK;
    public static Block STEEL_BLOCK;

    private ModBlocks() {}

    public static void register() {
        TITANIUM_ORE = register("titanium_ore", new Block(
                FabricBlockSettings.copyOf(Blocks.IRON_ORE).strength(3.5f, 3.0f)));
        DEEPSLATE_TITANIUM_ORE = register("deepslate_titanium_ore", new Block(
                FabricBlockSettings.copyOf(Blocks.DEEPSLATE_IRON_ORE).strength(4.5f, 3.0f)
                        .sounds(BlockSoundGroup.DEEPSLATE)));
        TITANIUM_BLOCK = register("titanium_block", new Block(
                FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).strength(6.0f, 7.0f)));
        STEEL_BLOCK = register("steel_block", new Block(
                FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).strength(5.0f, 6.0f)));
    }

    private static Block register(String name, Block block) {
        Identifier id = KratosArsenal.id(name);
        Block registered = Registry.register(Registries.BLOCK, id, block);
        // Registra automáticamente el BlockItem asociado.
        Registry.register(Registries.ITEM, id, new BlockItem(registered, new Item.Settings()));
        return registered;
    }
}

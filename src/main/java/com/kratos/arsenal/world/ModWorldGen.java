package com.kratos.arsenal.world;

import com.kratos.arsenal.KratosArsenal;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.PlacedFeature;

/**
 * Añade la generación de mineral de titanio al mundo.
 *
 * <p>La definición de la veta (tamaño, frecuencia, altura) está en los JSON de
 * datos {@code worldgen/configured_feature} y {@code worldgen/placed_feature};
 * aquí sólo se inyecta la característica colocada en los biomas del Overworld.</p>
 */
public final class ModWorldGen {

    private ModWorldGen() {}

    public static void register() {
        RegistryKey<PlacedFeature> titaniumOre = RegistryKey.of(RegistryKeys.PLACED_FEATURE,
                KratosArsenal.id("titanium_ore"));

        BiomeModifications.addFeature(
                BiomeSelectors.foundInOverworld(),
                GenerationStep.Feature.UNDERGROUND_ORES,
                titaniumOre);
    }
}

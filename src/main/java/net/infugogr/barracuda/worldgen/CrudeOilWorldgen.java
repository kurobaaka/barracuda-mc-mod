package net.infugogr.barracuda.worldgen;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.infugogr.barracuda.Barracuda;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.PlacedFeature;

public final class CrudeOilWorldgen {
    private static final Feature<DefaultFeatureConfig> CRUDE_OIL_LAKE = Registry.register(
            Registries.FEATURE, Barracuda.id("crude_oil_lake"),
            new CrudeOilLakeFeature(DefaultFeatureConfig.CODEC)
    );
    private static final RegistryKey<PlacedFeature> CRUDE_OIL_LAKE_PLACED = RegistryKey.of(
            RegistryKeys.PLACED_FEATURE, Barracuda.id("crude_oil_lake")
    );

    private CrudeOilWorldgen() {
    }

    public static void register() {
        BiomeModifications.addFeature(
                BiomeSelectors.all(),
                GenerationStep.Feature.LAKES,
                CRUDE_OIL_LAKE_PLACED
        );
    }
}

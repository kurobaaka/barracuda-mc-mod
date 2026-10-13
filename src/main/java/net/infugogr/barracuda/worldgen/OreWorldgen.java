package net.infugogr.barracuda.worldgen;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.infugogr.barracuda.Barracuda;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.PlacedFeature;

public final class OreWorldgen {
    private static final Beta92OreFeature AQUATIC_SAND_FEATURE = Registry.register(
            Registries.FEATURE,
            Barracuda.id("beta92_aquatic_sand_ore"),
            new Beta92OreFeature(OreFeatureConfig.CODEC)
    );

    private static final String[] ORE_FEATURES = {
            "ruby_ore", "ruby_ore_medium", "ruby_ore_large", "ruby_ore_buried",
            "sapphire_ore", "sapphire_ore_medium", "sapphire_ore_large", "sapphire_ore_buried",
            "uranium_ore", "uranium_ore_medium", "uranium_ore_large", "uranium_ore_buried",
            "silver_ore_extra", "silver_ore", "silver_ore_lower",
            "beta92_aquatic_sand_ore", "beta92_aquatic_sand_ore_lower"
    };

    private OreWorldgen() {
    }

    public static void register() {
        for (String feature : ORE_FEATURES) {
            BiomeModifications.addFeature(
                    BiomeSelectors.all(),
                    GenerationStep.Feature.UNDERGROUND_ORES,
                    RegistryKey.of(RegistryKeys.PLACED_FEATURE, Barracuda.id(feature))
            );
        }
    }
}

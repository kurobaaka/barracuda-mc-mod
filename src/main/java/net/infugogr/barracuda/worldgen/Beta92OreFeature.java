package net.infugogr.barracuda.worldgen;

import net.infugogr.barracuda.world.dimension.ModDimensions;
import net.minecraft.world.gen.feature.OreFeature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class Beta92OreFeature extends OreFeature {
    public Beta92OreFeature(com.mojang.serialization.Codec<OreFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<OreFeatureConfig> context) {
        if (!context.getWorld().toServerWorld().getRegistryKey().equals(ModDimensions.BETA_LEVEL_KEY)) {
            return false;
        }
        return super.generate(context);
    }
}

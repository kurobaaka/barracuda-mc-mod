package net.infugogr.barracuda.world.dimension;

import net.infugogr.barracuda.Barracuda;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;

public class ModDimensions {
    public static final RegistryKey<DimensionOptions> BETA_KEY = RegistryKey.of(RegistryKeys.DIMENSION,
            Barracuda.id("beta92"));
    public static final RegistryKey<World> BETA_LEVEL_KEY = RegistryKey.of(RegistryKeys.WORLD,
            Barracuda.id("beta92"));
    public static final RegistryKey<DimensionType> BETA_DIM_TYPE = RegistryKey.of(RegistryKeys.DIMENSION_TYPE,
            Barracuda.id("beta92_type"));
}

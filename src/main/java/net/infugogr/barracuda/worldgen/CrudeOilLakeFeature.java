package net.infugogr.barracuda.worldgen;

import com.mojang.serialization.Codec;
import net.infugogr.barracuda.fluid.ModFluids;
import net.infugogr.barracuda.world.dimension.ModDimensions;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class CrudeOilLakeFeature extends Feature<DefaultFeatureConfig> {
    public CrudeOilLakeFeature(Codec<DefaultFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        boolean beta92 = world.toServerWorld().getRegistryKey().equals(ModDimensions.BETA_LEVEL_KEY);
        if (!beta92 && !world.toServerWorld().getRegistryKey().equals(World.OVERWORLD)) {
            return false;
        }

        int chanceDenominator = beta92 ? 8 : 16;
        if (context.getRandom().nextInt(chanceDenominator) != 0) {
            return false;
        }

        ChunkPos chunkPos = new ChunkPos(context.getOrigin());
        int x = chunkPos.getStartX() + context.getRandom().nextBetween(4, 11);
        int z = chunkPos.getStartZ() + context.getRandom().nextBetween(4, 11);
        int minY = world.getBottomY() + 8;
        int maxY;
        if (beta92) {
            maxY = world.getTopY(Heightmap.Type.WORLD_SURFACE_WG, x, z) - 4;
        } else {
            maxY = -2;
        }
        if (maxY <= minY) {
            return false;
        }

        int surfaceY = context.getRandom().nextBetween(minY, maxY);
        int radiusX = context.getRandom().nextBetween(2, 3);
        int radiusZ = context.getRandom().nextBetween(2, 3);
        int depth = context.getRandom().nextBetween(2, 3);
        BlockState oil = ModFluids.CRUDE_OIL.block().getDefaultState();
        BlockPos.Mutable pos = new BlockPos.Mutable();
        boolean placedOil = false;

        for (int dx = -radiusX; dx <= radiusX; dx++) {
            for (int dz = -radiusZ; dz <= radiusZ; dz++) {
                double distance = (double) dx * dx / (radiusX * radiusX)
                        + (double) dz * dz / (radiusZ * radiusZ);
                if (distance > 1.0D) {
                    continue;
                }

                int columnDepth = Math.max(1, (int) Math.ceil(depth * (1.0D - distance)));
                for (int dy = 0; dy < columnDepth; dy++) {
                    pos.set(x + dx, surfaceY - dy, z + dz);
                    if (!isNaturalStone(world.getBlockState(pos))) {
                        continue;
                    }
                    world.setBlockState(pos, oil, 2);
                    placedOil = true;
                }

                for (int dy = 1; dy <= 2; dy++) {
                    pos.set(x + dx, surfaceY + dy, z + dz);
                    BlockState state = world.getBlockState(pos);
                    if (isNaturalStone(state)) {
                        world.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);
                    }
                }
            }
        }
        return placedOil;
    }

    private static boolean isNaturalStone(BlockState state) {
        return state.isIn(BlockTags.BASE_STONE_OVERWORLD)
                || state.isOf(Blocks.SANDSTONE)
                || state.isOf(Blocks.RED_SANDSTONE)
                || state.isOf(Blocks.DIRT)
                || state.isOf(Blocks.GRASS_BLOCK);
    }
}

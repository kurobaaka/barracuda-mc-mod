package net.infugogr.barracuda.util;

import net.infugogr.barracuda.block.ModBlocks;
import net.infugogr.barracuda.block.MultiBlock;
import net.infugogr.barracuda.block.entity.MultiBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class UniversalStructurePlacer {

    // ---------------- OIL REFINERY 3×2×1 ----------------

    public static void placeOilRefinery(World world, BlockPos pos, Direction facing) {
        Direction perp = facing.rotateYClockwise();

        // нижний слой shape = 0,1,2
        for (int x = -1; x <= 1; x++) {
            BlockPos p = pos.add(perp.getOffsetX() * x, -1, perp.getOffsetZ() * x);
            world.setBlockState(p,
                    ModBlocks.MULTIBLOCK.getDefaultState()
                            .with(MultiBlock.SHAPE, x + 1)
                            .with(MultiBlock.FACING, facing));
            BlockEntity be = world.getBlockEntity(p);
            if (be instanceof MultiBlockEntity multiBlockEntity) {
                multiBlockEntity.setEntityPos(pos);
            }
        }

        // верхний слой shape = 3,4,5
        for (int x = -1; x <= 1; x+=2) {
            BlockPos p = pos.add(perp.getOffsetX() * x, 0, perp.getOffsetZ() * x);
            world.setBlockState(p,
                    ModBlocks.MULTIBLOCK.getDefaultState()
                            .with(MultiBlock.SHAPE, 3 + (x + 1))
                            .with(MultiBlock.FACING, facing));
            BlockEntity be = world.getBlockEntity(p);
            if (be instanceof MultiBlockEntity multiBlockEntity) {
                multiBlockEntity.setEntityPos(pos);
            }
        }
    }

    // ---------------- CLOSET 1×2×1 ----------------

    public static void placeCloset(World world, BlockPos pos, Direction facing) {

        // нижний блок
        world.setBlockState(pos,
                ModBlocks.MULTIBLOCK.getDefaultState()
                        .with(MultiBlock.SHAPE, 10)
                        .with(MultiBlock.FACING, facing));

        // верхний блок
        world.setBlockState(pos.up(),
                ModBlocks.MULTIBLOCK.getDefaultState()
                        .with(MultiBlock.SHAPE, 11)
                        .with(MultiBlock.FACING, facing));
    }

    // ------------ CIRCUIT IMPRINTER 1×1×2 ------------

    public static void placeCircuitImprinter(World world, BlockPos pos, Direction facing) {

        BlockPos front = pos;
        BlockPos back = pos.offset(facing);

        // передняя половина (shape 20)
        world.setBlockState(front,
                ModBlocks.MULTIBLOCK.getDefaultState()
                        .with(MultiBlock.SHAPE, 20)
                        .with(MultiBlock.FACING, facing));

        // задняя половина (shape 21)
        world.setBlockState(back,
                ModBlocks.MULTIBLOCK.getDefaultState()
                        .with(MultiBlock.SHAPE, 21)
                        .with(MultiBlock.FACING, facing));
    }
}

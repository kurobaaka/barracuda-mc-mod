package net.infugogr.barracuda.block;

import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class RodsBlock  extends Block implements Waterloggable {
    private static final VoxelShape SHAPE_1;
    private static final VoxelShape SHAPE_2;
    private static final VoxelShape SHAPE_3;
    private static final VoxelShape SHAPE_4;
    private static final VoxelShape SHAPE_5;
    private static final VoxelShape SHAPE_6;
    private static final VoxelShape SHAPE_7;
    private static final VoxelShape SHAPE_8;
    private static final VoxelShape SHAPE_9;
    private static final VoxelShape SHAPE_10;
    private static final VoxelShape SHAPE_11;
    private static final VoxelShape SHAPE_12;
    private static final VoxelShape SHAPES;

    public RodsBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPES;
    }

    static {
        SHAPE_1 = VoxelShapes.cuboid(0.25, 0.1875, 0.0, 0.3125, 0.25, 1.0);
        SHAPE_2 = VoxelShapes.cuboid(0.6875, 0.6875, 0.0, 0.75, 0.75, 1.0);
        SHAPE_3 = VoxelShapes.cuboid(0.0, 0.625, 0.6875, 1.0, 0.6875, 0.75);
        SHAPE_4 = VoxelShapes.cuboid(0.0, 0.625, 0.25, 1.0, 0.6875, 0.3125);
        SHAPE_5 = VoxelShapes.cuboid(0.25, 0.6875, 0.0, 0.3125, 0.75, 1.0);
        SHAPE_6 = VoxelShapes.cuboid(0.0, 0.25, 0.25, 1.0, 0.3125, 0.3125);
        SHAPE_7 = VoxelShapes.cuboid(0.0, 0.25, 0.6875, 1.0, 0.3125, 0.75);
        SHAPE_8 = VoxelShapes.cuboid(0.6875, 0.1875, 0.0, 0.75, 0.25, 1.0);
        SHAPE_9 = VoxelShapes.cuboid(0.3125, 0.0, 0.3125, 0.375, 1.0, 0.375);
        SHAPE_10 = VoxelShapes.cuboid(0.625, 0.0, 0.625, 0.6875, 1.0, 0.6875);
        SHAPE_11 = VoxelShapes.cuboid(0.3125, 0.0, 0.625, 0.375, 1.0, 0.6875);
        SHAPE_12 = VoxelShapes.cuboid(0.6875, 0.1875, 0.0, 0.75, 0.25, 1.0);
        SHAPES = VoxelShapes.union(SHAPE_1, SHAPE_2, SHAPE_3,
                SHAPE_4, SHAPE_5, SHAPE_6, SHAPE_7, SHAPE_8, SHAPE_9, SHAPE_10, SHAPE_11, SHAPE_12).simplify();
    }

}
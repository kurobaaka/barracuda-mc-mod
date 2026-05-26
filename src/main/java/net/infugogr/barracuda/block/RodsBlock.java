package net.infugogr.barracuda.block;

import net.infugogr.barracuda.Barracuda;
import net.infugogr.barracuda.block.entity.LVcableBlockEntity;
import net.infugogr.barracuda.item.ModItems;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import static net.infugogr.barracuda.block.LVcableBlock.STATE;

public class RodsBlock  extends Block implements Waterloggable {

    public static final VoxelShape SHAPES = VoxelShapes.union(
            VoxelShapes.cuboid(2/16.0, 2/16.0, 0/16.0, 4/16.0, 4/16.0, 16/16.0),
            VoxelShapes.cuboid(12/16.0,12/16.0,0/16.0,14/16.0,14/16.0,16/16.0),
            VoxelShapes.cuboid(0/16.0,12/16.0,12/16.0,16/16.0,14/16.0,14/16.0),
            VoxelShapes.cuboid(0/16.0,12/16.0,2/16.0,16/16.0,14/16.0,4/16.0),
            VoxelShapes.cuboid(2/16.0,12/16.0,0/16.0,4/16.0,14/16.0,16/16.0),
            VoxelShapes.cuboid(0/16.0,2/16.0,2/16.0,16/16.0,4/16.0,4/16.0),
            VoxelShapes.cuboid(0/16.0,2/16.0,12/16.0,16/16.0,4/16.0,14/16.0),
            VoxelShapes.cuboid(12/16.0,2/16.0,0/16.0,14/16.0,4/16.0,16/16.0),
            VoxelShapes.cuboid(12/16.0,0/16.0,2/16.0,14/16.0,16/16.0,4/16.0),
            VoxelShapes.cuboid(2/16.0,0/16.0,2/16.0,4/16.0,16/16.0,4/16.0),
            VoxelShapes.cuboid(12/16.0,0/16.0,12/16.0,14/16.0,16/16.0,14/16.0),
            VoxelShapes.cuboid(2/16.0,0/16.0,12/16.0,4/16.0,16/16.0,14/16.0),
            VoxelShapes.cuboid(2/16.0,2/16.0,2/16.0,14/16.0,14/16.0,14/16.0)
    ).simplify();

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

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack stack = player.getStackInHand(hand);
        if (!world.isClient) {
            if (stack.getItem() == ModBlocks.LVCABLE.asItem()) {
                world.setBlockState(pos, ModBlocks.LVCABLE.getDefaultState().with(STATE, LVcableBlock.StateType.RODS));
                if (!player.isCreative()) stack.decrement(1);
                return ActionResult.SUCCESS;
            } else if (stack.getItem() == ModItems.STEEL_PLATE) {
                world.setBlockState(pos, ModBlocks.SHUTTLE_WALL.getDefaultState());
                if (!player.isCreative()) stack.decrement(1);
                return ActionResult.SUCCESS;
            } else if (stack.getItem() == ModItems.SCREWDRIVER) {
                world.setBlockState(pos, Blocks.AIR.getDefaultState());
                if (!player.isCreative()) dropStack(world, pos, ModBlocks.RODS_BLOCK.asItem().getDefaultStack());
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }
}
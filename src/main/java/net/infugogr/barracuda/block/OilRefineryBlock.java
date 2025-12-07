package net.infugogr.barracuda.block;

import com.mojang.serialization.MapCodec;
import net.infugogr.barracuda.block.entity.ModBlockEntityType;
import net.infugogr.barracuda.block.entity.MultiBlockEntity;
import net.infugogr.barracuda.block.entity.OilRefineryBlockEntity;
import net.infugogr.barracuda.util.ModTags;
import net.infugogr.barracuda.util.TickableBlockEntity;
import net.infugogr.barracuda.util.UniversalStructurePlacer;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class OilRefineryBlock extends HorizontalFacingBlock implements BlockEntityProvider {
    private static final MapCodec<OilRefineryBlock> CODEC = createCodec(OilRefineryBlock::new);
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    private static final VoxelShape OIL_REFINERY_TOP =
            VoxelShapes.union(
                    Block.createCuboidShape(1, 0, 1, 15, 2, 15),
                    Block.createCuboidShape(0, 2, 0, 16, 10, 16)
            );

    protected OilRefineryBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends HorizontalFacingBlock> getCodec() {
        return CODEC;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return ModBlockEntityType.OIL_REFINERY.instantiate(pos, state);
    }

    /**
     * При клике: сначала попытка залить/взять жидкость, иначе открыть GUI контроллера.
     */
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        BlockEntity ent = world.getBlockEntity(pos);
        ItemStack stack = player.getStackInHand(hand);

        if (!world.isClient && ent instanceof OilRefineryBlockEntity blockEntity && stack.isIn(ModTags.FLUIDS)) {
            return blockEntity.setFluid(stack, player);
        }

        if (!world.isClient) {
            NamedScreenHandlerFactory screenHandlerFactory = (NamedScreenHandlerFactory) ent;
            if (screenHandlerFactory != null) {
                player.openHandledScreen(screenHandlerFactory);
                return ActionResult.SUCCESS;
            }
        }

        return ActionResult.CONSUME;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return TickableBlockEntity.createTicker(world);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing());
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (world.isClient) {
            return super.onBreak(world, pos, state, player);
        }
        BlockState result = super.onBreak(world, pos, state, player);
        Direction facing = state.get(FACING);
        Direction perp = facing.rotateYClockwise();

        for (int w = -1; w <= 1; w++) {
            for (int h = -1; h <= 0; h++) {
                BlockPos target = pos.add(perp.getOffsetX() * w, h, perp.getOffsetZ() * w);
                if (target.equals(pos)) continue;
                BlockState partState = world.getBlockState(target);
                if (partState.getBlock() instanceof MultiBlock) {
                    world.breakBlock(target, false);
                }
            }
        }

        return result;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (world.isClient) return;
        Direction facing = state.get(FACING);
        UniversalStructurePlacer.placeOilRefinery(world, pos, facing);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        Direction facing = state.get(FACING);
        Direction perp = facing.rotateYClockwise();
        for (int w = -1; w <= 1; w++) {
            for (int h = -1; h <= 0; h++) {
                BlockPos checkPos = pos.add(perp.getOffsetX() * w, h, perp.getOffsetZ() * w);
                if (!world.isAir(checkPos)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return OIL_REFINERY_TOP;
    }
}
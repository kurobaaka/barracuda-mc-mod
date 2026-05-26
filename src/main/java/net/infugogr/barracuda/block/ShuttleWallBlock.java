package net.infugogr.barracuda.block;

import net.infugogr.barracuda.block.entity.LVcableBlockEntity;
import net.infugogr.barracuda.item.ModItems;
import net.infugogr.barracuda.util.ModTags;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.infugogr.barracuda.block.LVcableBlock.STATE;

public class ShuttleWallBlock extends Block{
    public ShuttleWallBlock(Settings settings) {
        super(settings);
    }

    public static final BooleanProperty NORTH = BooleanProperty.of("north");
    public static final BooleanProperty SOUTH = BooleanProperty.of("south");
    public static final BooleanProperty WEST = BooleanProperty.of("west");
    public static final BooleanProperty EAST = BooleanProperty.of("east");
    public static final BooleanProperty UP = BooleanProperty.of("up");
    public static final BooleanProperty DOWN = BooleanProperty.of("down");

    public static @NotNull BlockState calculateState(World world, BlockPos pos, BlockState state) {
        Boolean north = getSide(world, pos, Direction.NORTH);
        Boolean south = getSide(world, pos, Direction.SOUTH);
        Boolean west = getSide(world, pos, Direction.WEST);
        Boolean east = getSide(world, pos, Direction.EAST);
        Boolean up = getSide(world, pos, Direction.UP);
        Boolean down = getSide(world, pos, Direction.DOWN);

        return state
                .with(NORTH, north)
                .with(SOUTH, south)
                .with(WEST, west)
                .with(EAST, east)
                .with(UP, up)
                .with(DOWN, down);
    }

    private static boolean getSide(World world, BlockPos pos, Direction dir) {
        BlockPos neighborPos = pos.offset(dir);
        BlockState neighbor = world.getBlockState(neighborPos);
        if (neighbor.isIn(ModTags.SHUTTLE_BLOCKS)){
            return false;
        } else if (neighbor.getBlock() instanceof LVcableBlock) {
            return neighbor != neighbor.with(STATE, LVcableBlock.StateType.WALL);
        }
        return true;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(NORTH, SOUTH, WEST, EAST, UP, DOWN);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        World world = ctx.getWorld();
        BlockPos pos = ctx.getBlockPos();
        BlockState state = getDefaultState();
        return calculateState(world, pos, state);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        return calculateState((World) world, pos, state);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
        BlockState blockState = calculateState(world, pos, state);
        if (blockState != state) {
            world.setBlockState(pos, blockState);
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack stack = player.getStackInHand(hand);
        if (!world.isClient && stack.getItem() == ModItems.SCREWDRIVER) {
            world.setBlockState(pos, ModBlocks.RODS_BLOCK.getDefaultState());
            if (!player.isCreative()) dropStack(world, pos, ModItems.STEEL_PLATE.getDefaultStack());
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }
}


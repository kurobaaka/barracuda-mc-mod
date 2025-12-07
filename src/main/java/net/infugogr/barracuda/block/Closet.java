package net.infugogr.barracuda.block;

import net.infugogr.barracuda.block.entity.ClosetBlockEntity;
import net.infugogr.barracuda.block.entity.FuelGeneratorBlockEntity;
import net.infugogr.barracuda.block.entity.MultiBlockEntity;
import net.infugogr.barracuda.util.UniversalStructurePlacer;
import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.*;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class Closet extends Block implements BlockEntityProvider {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final BooleanProperty OPEN = Properties.OPEN;

    public Closet(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(OPEN, false));
    }

    // Создаём блок-сущность (ниже будет класс)
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ClosetBlockEntity(pos, state);
    }

    // Визуально нельзя соединить с другими — просто не реализуем никакой логики соединения (в отличие от ChestBlock).
    // Позиция при размещении — ставим нижнюю половину и автоматически создаём верхнюю.
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        World world = ctx.getWorld();
        BlockPos pos = ctx.getBlockPos();
        BlockPos posUp = pos.up();

        // если сверху занят — отменить установку
        if (!world.getBlockState(posUp).canReplace(ctx)) {
            return null;
        }

        Direction dir = ctx.getHorizontalPlayerFacing().getOpposite(); // типично для сундуков
        return this.getDefaultState().with(FACING, dir).with(OPEN, false);
    }

    // Открытие GUI
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            NamedScreenHandlerFactory screenHandlerFactory = ((ClosetBlockEntity) world.getBlockEntity(pos));

            if (screenHandlerFactory != null) {
                player.openHandledScreen(screenHandlerFactory);
            }
        }

        return ActionResult.SUCCESS;
    }

    // При удалении блока очищаем сущность
    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof ClosetBlockEntity) {
                // при желании: выбросить содержимое — но world.breakBlock(other, true) уже сделает drop
                world.removeBlockEntity(pos);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        } else {
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    // Состояния блока
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, OPEN);
    }


    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Direction facing = state.get(FACING);
        double y1 = 0.0;
        double y2 = 1; // 875
        double x1 = 0.125;
        double x2 = 0.875;
        double z1 = 0.125;
        double z2 = 0.75;
        switch (facing) {
            case SOUTH:
                return VoxelShapes.cuboid(x1, y1, 0.0, x2, y2, z2);
            case NORTH:
                return VoxelShapes.cuboid(x1, y1, 1.0 - z2, x2, y2, 1.0);
            case EAST:
                return VoxelShapes.cuboid(0.0, y1, z1, z2, y2, x2);
            case WEST:
                return VoxelShapes.cuboid(1.0 - z2, y1, z1, 1.0, y2, x2);
            default:
                return VoxelShapes.empty();
        }
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (world.isClient) {
            return super.onBreak(world, pos, state, player);
        }
        BlockState result = super.onBreak(world, pos, state, player);
                BlockPos target = pos.up();
                BlockState partState = world.getBlockState(target);
                if (partState.getBlock() instanceof MultiBlock) {
                    world.breakBlock(target, false);
        }
        return result;
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockPos checkPos = pos.add(0, 1, 0);
        return world.isAir(checkPos);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (world.isClient) return;
        Direction facing = state.get(FACING);
        UniversalStructurePlacer.placeCloset(world, pos, facing);
    }
}

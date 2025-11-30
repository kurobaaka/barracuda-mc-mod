package net.infugogr.barracuda.block;

import com.mojang.serialization.MapCodec;
import net.infugogr.barracuda.block.entity.ModBlockEntityType;
import net.infugogr.barracuda.block.entity.TeleporterBlockEntity;
import net.infugogr.barracuda.util.TickableBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
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
import net.minecraft.world.*;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import net.minecraft.entity.LivingEntity;

public class TeleporterBlock extends HorizontalFacingBlock implements BlockEntityProvider {
    public static final MapCodec<TeleporterBlock> CODEC = createCodec(TeleporterBlock::new);
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    protected TeleporterBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    public MapCodec<TeleporterBlock> getCodec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return ModBlockEntityType.TELEPORTER.instantiate(pos, state);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof TeleporterBlockEntity entity) {
                player.openHandledScreen(entity);
            }
        }
        return ActionResult.SUCCESS;
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
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (!world.isClient && entity instanceof LivingEntity) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof TeleporterBlockEntity teleporter) {
                world.scheduleBlockTick(pos, this, 95);
            }
        }
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, net.minecraft.util.math.random.Random random) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof TeleporterBlockEntity teleporter) {
            Entity entity = world.getClosestPlayer(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 1.0, false);
            if (entity != null) {
                tryTeleport(teleporter, (ServerPlayerEntity) entity);
            }
        }
    }

    private void tryTeleport(TeleporterBlockEntity teleporter, ServerPlayerEntity player) {
        // Проверяем наличие чипа позиции и энергии
        if (!teleporter.hasPosChip() || teleporter.getEnergyStorage().amount == 10000000) {
            return;
        }

        // Получаем целевую позицию
        BlockPos targetPos = teleporter.getTargetPosition();
        if (targetPos == null) return;

        // Определяем целевое измерение (если есть чип измерения) или текущее
        RegistryKey<World> targetDim = teleporter.getTargetDimension();
        if (targetDim == null) {
            targetDim = player.getWorld().getRegistryKey();
        }

        ServerWorld targetWorld = Objects.requireNonNull(player.getServer()).getWorld(targetDim);
        if (targetWorld != null) {
            // Находим безопасную позицию для телепортации
            BlockPos safePos = findHighestSafePosition(targetWorld, targetPos);
            if (safePos != null) {
                teleporter.getEnergyStorage().amount -= 10000000;
                teleporter.markDirty(); // Сохраняем изменения энергии
                player.teleport(
                        targetWorld,
                        safePos.getX() + 0.5,
                        safePos.getY(),
                        safePos.getZ() + 0.5,
                        player.getYaw(),
                        player.getPitch()
                );
                teleporter.syncWithClient(); // Синхронизируем с клиентом
            }
        }
    }

    private BlockPos findHighestSafePosition(ServerWorld world, BlockPos baseXZ) {
        int topY = world.getTopY();
        int bottomY = world.getBottomY();

        for (int y = topY - 2; y > bottomY; y--) {
            BlockPos feet = new BlockPos(baseXZ.getX(), y, baseXZ.getZ());
            BlockPos head = feet.up();
            BlockPos ground = feet.down();

            if (world.isAir(feet) && world.isAir(head) && !world.isAir(ground)) {
                return feet;
            }
        }
        return baseXZ;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.cuboid(0, 0, 0, 1, 0.6875, 1);
    }
}

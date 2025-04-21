package net.infugogr.barracuda.block;

import com.mojang.serialization.MapCodec;
import net.infugogr.barracuda.block.entity.ModBlockEntityType;
import net.infugogr.barracuda.util.TickableBlockEntity;
import net.infugogr.barracuda.world.dimension.ModDimensions;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.*;
import org.jetbrains.annotations.Nullable;

public class TeleporterBlock extends HorizontalFacingBlock implements BlockEntityProvider{
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

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return TickableBlockEntity.createTicker(world);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }


    @Override
    public void onEntityLand(BlockView world1, Entity entity) {
        World world = entity.getWorld();
        if (!world.isClient && entity instanceof ServerPlayerEntity serverPlayer) {
            Vec3d currentPos = entity.getPos();
            BlockPos baseXZ = new BlockPos((int) currentPos.x, 0, (int) currentPos.z);
            RegistryKey<World> currentDim = world.getRegistryKey();
            ServerWorld targetWorld = null;

            if (currentDim == World.OVERWORLD) {
                targetWorld = serverPlayer.getServer().getWorld(ModDimensions.BETA_LEVEL_KEY);
            } else if (currentDim == ModDimensions.BETA_LEVEL_KEY) {
                targetWorld = serverPlayer.getServer().getWorld(World.OVERWORLD);
            }

            if (targetWorld != null) {
                BlockPos targetPos = findHighestSafePosition(targetWorld, baseXZ);
                if (targetPos != null) {
                    serverPlayer.teleport(
                            targetWorld,
                            targetPos.getX() + 0.5,
                            targetPos.getY(),
                            targetPos.getZ() + 0.5,
                            entity.getYaw(),
                            entity.getPitch()
                    );
                }
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

        // Если не нашли безопасное место — fallback на world spawn
        return world.getSpawnPos();
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.cuboid(0, 0, 0, 1, 0.6875, 1);
    }
}

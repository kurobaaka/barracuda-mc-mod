package net.infugogr.barracuda.block;

import com.mojang.serialization.MapCodec;
import net.infugogr.barracuda.Barracuda;
import net.infugogr.barracuda.block.entity.ModBlockEntityType;
import net.infugogr.barracuda.util.TickableBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.*;
import org.jetbrains.annotations.Nullable;

public class TeleporterBlock extends HorizontalFacingBlock implements BlockEntityProvider{
    private static final MapCodec<TeleporterBlock> CODEC = createCodec(TeleporterBlock::new);
    protected TeleporterBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends HorizontalFacingBlock> getCodec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return ModBlockEntityType.TELEPORTER.instantiate(pos, state);
    }

    @Override
    public void onEntityLand(BlockView world, Entity entity) {
        super.onEntityLand(world, entity);
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
        super.appendProperties(builder);
        builder.add(FACING);
    }

}

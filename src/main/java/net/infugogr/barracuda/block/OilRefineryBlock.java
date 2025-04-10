package net.infugogr.barracuda.block;

import com.mojang.serialization.MapCodec;
import net.infugogr.barracuda.block.entity.MachineFrameBlockEntity;
import net.infugogr.barracuda.block.entity.ModBlockEntityType;
import net.infugogr.barracuda.block.entity.OilRefineryBlockEntity;
import net.infugogr.barracuda.fluid.ModFluids;
import net.infugogr.barracuda.item.ModItems;
import net.infugogr.barracuda.util.ModTags;
import net.infugogr.barracuda.util.TickableBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
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
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class OilRefineryBlock extends BlockWithEntity implements BlockEntityProvider{
    private static final MapCodec<OilRefineryBlock> CODEC = createCodec(OilRefineryBlock::new);
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    protected OilRefineryBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(FACING, Direction.NORTH));
    }



    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
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

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        NamedScreenHandlerFactory screenHandlerFactory = ((OilRefineryBlockEntity) world.getBlockEntity(pos));
        BlockEntity ent = world.getBlockEntity(pos);
        ItemStack stack = player.getStackInHand(hand);
        if (!world.isClient && ent instanceof OilRefineryBlockEntity blockEntity && stack.isIn(ModTags.FLUIDS)) {
            return blockEntity.setFluid(stack, player);
        } else if (!world.isClient && screenHandlerFactory != null) {
            player.openHandledScreen(screenHandlerFactory);
            return ActionResult.SUCCESS;
        } else {return ActionResult.CONSUME;}
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return TickableBlockEntity.createTicker(world);
    }
}

package net.infugogr.barracuda.block;

import com.mojang.serialization.MapCodec;
import net.infugogr.barracuda.block.entity.ModBlockEntityType;
import net.infugogr.barracuda.block.entity.PounderBlockEntity;
import net.infugogr.barracuda.util.ModTags;
import net.infugogr.barracuda.util.TickableBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class PounderBlock extends BlockWithEntity  implements BlockEntityProvider{
    private static final MapCodec<PounderBlock> CODEC = createCodec(PounderBlock::new);
    public static final IntProperty POUNDS = IntProperty.of("pounds", 0, 3); // Счетчик нажатий (0-3)

    public PounderBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(POUNDS, 0));
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
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(POUNDS);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.cuboid(0.125, 0, 0.125, 0.875, 0.375, 0.875);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            if (world.getBlockEntity(pos) instanceof PounderBlockEntity pounder) {
                ItemStack heldStack = player.getStackInHand(hand);

                if (pounder.getInputStack().isEmpty() && heldStack.isIn(ModTags.CRUSHER_RECIPE_ITEMS)) {
                    // Помещаем предмет в ступку
                    pounder.setInputStack(heldStack.split(1));
                    world.setBlockState(pos, state.with(POUNDS, 1));
                    return ActionResult.SUCCESS;
                } else if (!pounder.getInputStack().isEmpty() && heldStack.isEmpty() && player.isCrawling()) {
                    // Забираем предмет обратно
                    player.setStackInHand(hand, pounder.getInputStack());
                    pounder.setInputStack(ItemStack.EMPTY);
                    world.setBlockState(pos, state.with(POUNDS, 0));
                    return ActionResult.SUCCESS;
                } else if (!pounder.getInputStack().isEmpty()) {
                    // Увеличиваем счетчик ударов
                    int currentPounds = state.get(POUNDS);
                    if (currentPounds < 3) {
                        world.setBlockState(pos, state.with(POUNDS, currentPounds + 1));

                        // Если это третий удар, запускаем анимацию
                        if (currentPounds == 2) {
                            pounder.startPounding();
                            return ActionResult.SUCCESS;
                        } else return ActionResult.PASS;
                    } else return ActionResult.PASS;
                } else return ActionResult.PASS;
            } else return ActionResult.PASS;
        } else return ActionResult.PASS;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            if (world.getBlockEntity(pos) instanceof PounderBlockEntity pounder) {
                if (!pounder.getInputStack().isEmpty()) {
                    ItemScatterer.spawn(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, pounder.getInputStack());
                }
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PounderBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient ? null : validateTicker(type, ModBlockEntityType.POUNDER, PounderBlockEntity::tick);
    }
}

package net.infugogr.barracuda.block;

import net.infugogr.barracuda.block.entity.MultiBlockEntity;
import net.infugogr.barracuda.block.entity.OilRefineryBlockEntity;
import net.infugogr.barracuda.block.entity.SMESblockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
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
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class MultiBlock extends Block implements BlockEntityProvider {

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    // Простое свойство индекса формы: SHAPE = 0..99 (запас)
    public static final IntProperty SHAPE = IntProperty.of("shape", 0, 99);

    public MultiBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager
                .getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(SHAPE, 0)
        );
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof MultiBlockEntity multiBlockEntity) {
                BlockEntity blockEntity2 = world.getBlockEntity(multiBlockEntity.getEntityPos());
                if (blockEntity2 instanceof OilRefineryBlockEntity oilRefineryBlockEntity) {
                    player.openHandledScreen(oilRefineryBlockEntity);
                }
            }
        }

        return ActionResult.SUCCESS;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, SHAPE);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new MultiBlockEntity(pos, state);
    }

    // ---------------- SHAPES TABLE -----------------

    private static final VoxelShape OIL_REFINERY_BOTTOM =
            Block.createCuboidShape(1, 0, 1, 15, 16, 15);

    private static final VoxelShape OIL_REFINERY_TOP =
            VoxelShapes.union(
                    Block.createCuboidShape(1, 0, 1, 15, 2, 15),
                    Block.createCuboidShape(0, 2, 0, 16, 10, 16)
            );

    // CLOSET SHAPE (1×2×1)
    private static VoxelShape closetShape(Direction facing) {
        double y1 = 0.0;
        double y2 = 1.875;
        double x1 = 0.125;
        double x2 = 0.875;
        double z1 = 0.125;
        double z2 = 0.75;

        return switch (facing) {
            case SOUTH -> VoxelShapes.cuboid(x1, y1, 0.0, x2, y2, z2);
            case NORTH -> VoxelShapes.cuboid(x1, y1, 1 - z2, x2, y2, 1);
            case EAST  -> VoxelShapes.cuboid(0, y1, z1, z2, y2, x2);
            case WEST  -> VoxelShapes.cuboid(1 - z2, y1, z1, 1, y2, x2);
            default -> VoxelShapes.fullCube();
        };
    }

    // CIRCUIT PRINTER shape (1×1×2)
    private static final VoxelShape PRINTER_FULL =
            Block.createCuboidShape(2, 0, 1, 14, 11, 25);  // конвертировано 0.125..1.5625 *16

    // Передняя половина
    private static final VoxelShape PRINTER_FRONT =
            Block.createCuboidShape(2, 0, 1, 14, 11, 13);

    // Задняя половина
    private static final VoxelShape PRINTER_BACK =
            Block.createCuboidShape(2, 0, 13, 14, 11, 25);

    // ---------------------------------------------------

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        int shape = state.get(SHAPE);
        Direction facing = state.get(FACING);

        return switch (shape) {

            // 0..5 — Oil Refinery
            case 0 -> OIL_REFINERY_BOTTOM;
            case 1 -> OIL_REFINERY_BOTTOM;
            case 2 -> OIL_REFINERY_BOTTOM;
            case 3 -> OIL_REFINERY_TOP;
            case 4 -> OIL_REFINERY_TOP;
            case 5 -> OIL_REFINERY_TOP;

            // 10..11 — Closet (верх/низ)
            case 10 -> closetShape(facing); // нижний
            case 11 -> closetShape(facing); // верхний

            // 20..21 — Circuit Imprinter
            case 20 -> PRINTER_FRONT;
            case 21 -> PRINTER_BACK;

            default -> VoxelShapes.fullCube();
        };
    }

    // ПЕРЕДАЧА В entity — тут пусто
    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state,
                         @Nullable LivingEntity placer, ItemStack itemStack) {}

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient) {

            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof MultiBlockEntity part) {

                BlockPos controllerPos = part.getEntityPos();

                if (controllerPos != null && world.isChunkLoaded(controllerPos)) {
                    BlockState controllerState = world.getBlockState(controllerPos);

                    if (controllerState.getBlock() instanceof OilRefineryBlock) {

                        Direction facing = controllerState.get(OilRefineryBlock.FACING);
                        Direction perp = facing.rotateYClockwise();

                        // Ломаем ВСЮ структуру (6 блоков)
                        for (int w = -1; w <= 1; w++) {
                            for (int h = 0; h >= -1; h--) {
                                BlockPos target = controllerPos.add(
                                        perp.getOffsetX() * w,
                                        h,
                                        perp.getOffsetZ() * w
                                );

                                // Если это MultiBlock или сам контроллер — ломаем
                                BlockState st = world.getBlockState(target);
                                if (st.getBlock() instanceof MultiBlock || st.getBlock() instanceof OilRefineryBlock) {
                                    world.breakBlock(target, st.getBlock() instanceof OilRefineryBlock);
                                    // true — только контроллер дропает предмет
                                }
                            }
                        }
                    }
                }
            }
        }

        return super.onBreak(world, pos, state, player);
    }
}

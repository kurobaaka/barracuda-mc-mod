package net.infugogr.barracuda.fluid;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

import java.util.function.Supplier;

public abstract class BarracudaFluid extends FlowableFluid {
    private final Supplier<Fluid> stillSupplier, flowingSupplier;
    private final Supplier<Item> bucketSupplier;
    private final Supplier<Block> blockSupplier;
    private final int levelDecreasePerBlock;
    protected final int flowSpeed;
    private final int tickRate;

    public BarracudaFluid(Supplier<Fluid> stillSupplier, Supplier<Fluid> flowingSupplier, Supplier<Item> bucketSupplier,
                          Supplier<Block> blockSupplier, int levelDecreasePerBlock, int flowSpeed, int tickRate) {
        this.stillSupplier = stillSupplier;
        this.flowingSupplier = flowingSupplier;
        this.bucketSupplier = bucketSupplier;
        this.blockSupplier = blockSupplier;
        this.levelDecreasePerBlock = levelDecreasePerBlock;
        this.flowSpeed = flowSpeed;
        this.tickRate = tickRate;
    }

    @Override
    public Fluid getFlowing() {
        return this.flowingSupplier.get();
    }

    @Override
    public Fluid getStill() {
        return this.stillSupplier.get();
    }


    @Override
    public boolean matchesType(Fluid fluid) {
        return fluid == getFlowing() || fluid == getStill();
    }

    @Override
    protected void beforeBreakingBlock(WorldAccess world, BlockPos pos, BlockState state) {
        BlockEntity blockEntity = state.hasBlockEntity() ? world.getBlockEntity(pos) : null;
        Block.dropStacks(state, world, pos, blockEntity);
    }


    @Override
    protected int getLevelDecreasePerBlock(WorldView world) {
        return this.levelDecreasePerBlock;
    }

    @Override
    public Item getBucketItem() {
        return this.bucketSupplier.get();
    }

    @Override
    protected boolean canBeReplacedWith(FluidState state, BlockView world, BlockPos pos, Fluid fluid, Direction direction) {
        return false;
    }

    @Override
    public int getTickRate(WorldView world) {
        return this.tickRate;
    }

    @Override
    protected float getBlastResistance() {
        return 120f;
    }

    @Override
    protected BlockState toBlockState(FluidState state) {
        return this.blockSupplier.get().getDefaultState().with(Properties.LEVEL_15, getBlockStateLevel(state));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Fluid, FluidState> builder) {
        super.appendProperties(builder);
        builder.add(LEVEL);
    }

    public static class Flowing extends BarracudaFluid {
        public Flowing(Supplier<Fluid> stillSupplier, Supplier<Fluid> flowingSupplier, Supplier<Item> bucketSupplier,
                       Supplier<Block> blockSupplier, int levelDecreasePerBlock, int flowSpeed, int tickRate) {
            super(stillSupplier, flowingSupplier, bucketSupplier, blockSupplier, levelDecreasePerBlock, flowSpeed, tickRate);
        }

        @Override
        public boolean isStill(FluidState state) {
            return false;
        }

        @Override
        protected boolean isInfinite(World world) {
            return false;
        }

        @Override
        protected int getFlowSpeed(WorldView world) {
            return this.flowSpeed;
        }

        @Override
        public int getLevel(FluidState state) {
            return state.get(LEVEL);
        }
    }

    public static class Still extends BarracudaFluid {
        public Still(Supplier<Fluid> stillSupplier, Supplier<Fluid> flowingSupplier, Supplier<Item> bucketSupplier,
                     Supplier<Block> blockSupplier, int levelDecreasePerBlock, int flowSpeed, int tickRate) {
            super(stillSupplier, flowingSupplier, bucketSupplier, blockSupplier, levelDecreasePerBlock, flowSpeed, tickRate);
        }

        @Override
        public boolean isStill(FluidState state) {
            return true;
        }

        @Override
        protected boolean isInfinite(World world) {
            return false;
        }

        @Override
        protected int getFlowSpeed(WorldView world) {
            return this.flowSpeed;
        }

        @Override
        public int getLevel(FluidState state) {
            return 8;
        }
    }
}

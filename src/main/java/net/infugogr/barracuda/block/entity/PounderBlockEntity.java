package net.infugogr.barracuda.block.entity;

import net.infugogr.barracuda.block.PounderBlock;
import net.infugogr.barracuda.item.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PounderBlockEntity extends BlockEntity{
    private ItemStack inputStack = ItemStack.EMPTY;
    private int poundingProgress = 0;
    private boolean isPounding = false;

    public PounderBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityType.POUNDER, pos, state);
    }

    public void setInputStack(ItemStack stack) {
        this.inputStack = stack;
        markDirty();
    }

    public ItemStack getInputStack() {
        return inputStack;
    }

    public int getPoundingProgress() {
        return poundingProgress;
    }

    public boolean isPounding() {
        return isPounding;
    }

    public void startPounding() {
        this.isPounding = true;
        this.poundingProgress = 0;
        markDirty();
    }


    public static void tick(World world, BlockPos pos, BlockState state, PounderBlockEntity pounder) {
        if (pounder.isPounding) {
            pounder.poundingProgress++;

            // Анимация завершена (20 тиков = 1 секунда)
            if (pounder.poundingProgress >= 20) {
                pounder.isPounding = false;
                pounder.processInput();
                world.setBlockState(pos, state.with(PounderBlock.POUNDS, 0));
            }
        }
    }

    private void processInput() {
        if (world != null && !inputStack.isEmpty()) {
            ItemStack result = transformItem(inputStack);
            if (!result.isEmpty()) {
                world.spawnEntity(new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, result));
            }
            inputStack = ItemStack.EMPTY;
            markDirty();
        }
    }

    private ItemStack transformItem(ItemStack input) {
        if (input.isOf(ModItems.URANIUM_INGOT)) {
            return new ItemStack(ModItems.URANIUM_DUST, 3);
        }
        // Добавьте другие рецепты здесь
        return ItemStack.EMPTY;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        inputStack = ItemStack.fromNbt(nbt.getCompound("InputStack"));
        isPounding = nbt.getBoolean("IsPounding");
        poundingProgress = nbt.getInt("PoundingProgress");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.put("InputStack", inputStack.writeNbt(new NbtCompound()));
        nbt.putBoolean("IsPounding", isPounding);
        nbt.putInt("PoundingProgress", poundingProgress);
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }
}

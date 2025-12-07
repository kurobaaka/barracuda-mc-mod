package net.infugogr.barracuda.block.entity;

import net.infugogr.barracuda.util.UpdatableBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class MultiBlockEntity extends UpdatableBlockEntity {
    private BlockPos entityPos;

    public MultiBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityType.MULTIBLOCK, pos, state);
    }

    public void setEntityPos(BlockPos entityPos) {
        this.entityPos = entityPos;
        markDirty(); // пометить для сохранения/обновления
    }

    public BlockPos getEntityPos() {
        return entityPos;
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if (entityPos != null) nbt.putLong("EntityPos", entityPos.asLong());
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains("EntityPos")) this.entityPos = BlockPos.fromLong(nbt.getLong("EntityPos"));
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }
}
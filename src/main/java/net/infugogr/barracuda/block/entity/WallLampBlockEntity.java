package net.infugogr.barracuda.block.entity;

import net.infugogr.barracuda.util.SyncableStorage;
import net.infugogr.barracuda.util.SyncableTickableBlockEntity;
import net.infugogr.barracuda.util.UpdatableBlockEntity;
import net.infugogr.barracuda.util.energy.EnergySpreader;
import net.infugogr.barracuda.util.energy.SyncingEnergyStorage;
import net.infugogr.barracuda.util.energy.WrappedEnergyStorage;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.base.SimpleEnergyStorage;

import java.util.List;

import static net.infugogr.barracuda.block.WallLampBlock.LIT;
import static net.minecraft.block.HorizontalFacingBlock.FACING;


public class WallLampBlockEntity extends UpdatableBlockEntity implements SyncableTickableBlockEntity, EnergySpreader{
    private final WrappedEnergyStorage energyStorage = new WrappedEnergyStorage();
    private final BlockState state;

    public WallLampBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityType.WALL_LAMP, pos, state);
        this.state = state;
        this.energyStorage.addStorage(new SyncingEnergyStorage(this, 10, 5, 0));
    }
    @Override
    public List<SyncableStorage> getSyncableStorages() {
        var energy = (SyncingEnergyStorage) this.energyStorage.getStorage(null);
        return List.of(energy);
    }
    @Override
    public void onTick() {
        if (this.world == null || this.world.isClient)
            return;

        SimpleEnergyStorage energyStorage = this.energyStorage.getStorage(null);

        spread(this.world, this.pos, energyStorage);

        if (energyStorage.getAmount() > 1) {
            changeState(state, true);
            energyStorage.amount -= 1;
        } else {changeState(state, false);}

    }

    public void changeState (BlockState state, Boolean bool){
        Direction direction = state.get(FACING);
        assert world != null;
        if(bool){
            world.setBlockState(pos, state.getBlock().getDefaultState().with(FACING, direction).with(LIT, true), 3);
        }
        if(!bool){
            world.setBlockState(pos, state.getBlock().getDefaultState().with(FACING, direction).with(LIT, false), 3);
        }
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf packetByteBuf) {
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        nbt.put("EnergyStorage", this.energyStorage.writeNbt());
    }
    @Override
    public void readNbt(NbtCompound nbt) {
        this.energyStorage.readNbt(nbt.getList("EnergyStorage", NbtElement.COMPOUND_TYPE));
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
    public EnergyStorage getEnergyProvider(Direction direction) {
        return this.energyStorage.getStorage(direction);
    }
}

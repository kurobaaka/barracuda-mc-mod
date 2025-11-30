package net.infugogr.barracuda.block.entity;

import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.infugogr.barracuda.util.SyncableStorage;
import net.infugogr.barracuda.util.SyncableTickableBlockEntity;
import net.infugogr.barracuda.util.UpdatableBlockEntity;
import net.infugogr.barracuda.util.energy.SyncingEnergyStorage;
import net.infugogr.barracuda.util.energy.WrappedEnergyStorage;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.base.SimpleEnergyStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class ShuttleWallBlockEntity extends UpdatableBlockEntity implements SyncableTickableBlockEntity {
    private final WrappedEnergyStorage wrappedEnergyStorage = new WrappedEnergyStorage();
    private Set<BlockPos> connectedBlocks = null;

    protected ShuttleWallBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    public ShuttleWallBlockEntity(BlockPos blockPos, BlockState blockState) {
        this(ModBlockEntityType.SHUTTLE_WALL, blockPos, blockState);

        this.wrappedEnergyStorage.addStorage(new SyncingEnergyStorage(this, 1000, 100, 0));
    }

    private void checkOutputs() {
        if (this.connectedBlocks == null && this.world != null) {
            this.connectedBlocks = new HashSet<>();
            traverse(this.pos, cable -> {
                // Check for all energy receivers around this position (ignore cables)
                for (Direction direction : Direction.values()) {
                    BlockPos pos = cable.getPos().offset(direction);
                    var storage = EnergyStorage.SIDED.find(this.world, pos, direction.getOpposite());
                    if (storage != null && storage.supportsInsertion() && !(this.world.getBlockEntity(pos) instanceof ShuttleWallBlockEntity)) {
                        this.connectedBlocks.add(pos);
                    }
                }
            });
        }
    }

    // This is a generic function that will traverse all cables connected to this cable1 and call the given consumer for each cable1.
    private void traverse(BlockPos pos, Consumer<ShuttleWallBlockEntity> consumer) {
        Set<BlockPos> traversed = new HashSet<>();
        traversed.add(pos);
        consumer.accept(this);
        traverse(pos, traversed, consumer);
    }

    private void traverse(BlockPos pos, Set<BlockPos> traversed, Consumer<ShuttleWallBlockEntity> consumer) {
        if (this.world == null)
            return;

        for (Direction direction : Direction.values()) {
            BlockPos offset = pos.offset(direction);
            if (!traversed.contains(offset)) {
                traversed.add(offset);
                if (this.world.getBlockEntity(offset) instanceof ShuttleWallBlockEntity cable) {
                    consumer.accept(cable);
                    cable.traverse(offset, traversed, consumer);
                }
            }
        }
    }

    @Override
    public List<SyncableStorage> getSyncableStorages() {
        return List.of((SyncableStorage) this.wrappedEnergyStorage.getStorage(null));
    }

    @Override
    public void onTick() {
        if(this.world == null || this.world.isClient)
            return;

        SimpleEnergyStorage energy = getEnergy();
        if(energy.amount > 0) {
            checkOutputs();
            if (this.connectedBlocks.isEmpty())
                return;

            long amount = energy.getAmount() / this.connectedBlocks.size();
            try (Transaction transaction = Transaction.openOuter()) {
                for (BlockPos pos : this.connectedBlocks) {
                    var direction = Direction.fromVector(this.pos.getX() - pos.getX(), this.pos.getY() - pos.getY(), this.pos.getZ() - pos.getZ());
                    if(direction == null)
                        continue;

                    var storage = EnergyStorage.SIDED.find(this.world, pos, direction);
                    if (storage != null && storage.supportsInsertion()) {
                        energy.amount -= storage.insert(amount, transaction);
                    }
                }

                transaction.commit();
            }
        }
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf packetByteBuf) {

    }

    public void markDirty() {
        traverse(this.pos, cable -> cable.connectedBlocks = null);
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.put("Energy", this.wrappedEnergyStorage.writeNbt());
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        if(nbt.contains("Energy", NbtElement.LIST_TYPE))
            this.wrappedEnergyStorage.readNbt(nbt.getList("Energy", NbtElement.COMPOUND_TYPE));
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    public SimpleEnergyStorage getEnergy() {
        return this.wrappedEnergyStorage.getStorage(null);
    }

    public SimpleEnergyStorage getEnergyProvider(Direction direction) {
        return this.wrappedEnergyStorage.getStorage(direction);
    }
}
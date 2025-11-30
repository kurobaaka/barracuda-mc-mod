package net.infugogr.barracuda.block.entity;

import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.infugogr.barracuda.util.SyncableStorage;
import net.infugogr.barracuda.util.SyncableTickableBlockEntity;
import net.infugogr.barracuda.util.UpdatableBlockEntity;
import net.infugogr.barracuda.util.energy.SyncingEnergyStorage;
import net.infugogr.barracuda.util.energy.WrappedEnergyStorage;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.base.SimpleEnergyStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class LVcableBlockEntity extends UpdatableBlockEntity implements SyncableTickableBlockEntity {

    private final WrappedEnergyStorage wrappedEnergyStorage = new WrappedEnergyStorage();

    // Храним позиции выхода + направление, в которое НАДО ИНСЕРТИТЬ энергию
    public record OutputTarget(BlockPos pos, Direction insertDirection) {}

    private Set<OutputTarget> connectedBlocks = null;

    public LVcableBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntityType.LVCABLE, blockPos, blockState);
        this.wrappedEnergyStorage.addStorage(new SyncingEnergyStorage(this, 1000, 100, 0));
    }

    // --------------------
    // Traverse – полный обход всей сети проводов
    // --------------------

    private void traverse(BlockPos start, Consumer<BlockPos> consumer) {
        Set<BlockPos> visited = new HashSet<>();
        visited.add(start);
        consumer.accept(start);
        traverse(start, visited, consumer);
    }

    private void traverse(BlockPos pos, Set<BlockPos> visited, Consumer<BlockPos> consumer) {
        if (world == null) return;

        for (Direction dir : Direction.values()) {
            BlockPos next = pos.offset(dir);

            if (!visited.contains(next)) {
                BlockEntity be = world.getBlockEntity(next);

                if (be instanceof LVcableBlockEntity) {
                    visited.add(next);
                    consumer.accept(next);
                    traverse(next, visited, consumer);
                }
            }
        }
    }

    // --------------------
    // Найти ВСЕ выходы энергии на сети кабелей
    // --------------------

    private void checkOutputs() {
        if (this.connectedBlocks != null || world == null)
            return;

        this.connectedBlocks = new HashSet<>();

        traverse(this.pos, cablePos -> {

            for (Direction dir : Direction.values()) {
                BlockPos targetPos = cablePos.offset(dir);

                BlockEntity be = world.getBlockEntity(targetPos);

                // Это провод? Тогда пропускаем
                if (be instanceof LVcableBlockEntity)
                    continue;

                // Найти энергохранилище
                var storage = EnergyStorage.SIDED.find(world, targetPos, dir.getOpposite());
                if (storage == null || !storage.supportsInsertion())
                    continue;

                // Если блок имеет facing — проверяем
                BlockState state = world.getBlockState(targetPos);
                if (state.contains(Properties.HORIZONTAL_FACING)) {
                    Direction facing = state.get(Properties.HORIZONTAL_FACING);
                    if (facing != dir.getOpposite())
                        continue;
                }

                // Сохраняем ПОЗИЦИЮ + в КАКОМ НАПРАВЛЕНИИ ВСТАВЛЯТЬ
                this.connectedBlocks.add(new OutputTarget(targetPos, dir.getOpposite()));
            }
        });
    }

    // --------------------
    // Tick — распределяем энергию
    // --------------------

    @Override
    public void onTick() {
        if (world == null || world.isClient)
            return;

        SimpleEnergyStorage energy = getEnergy();
        if (energy.amount <= 0)
            return;

        checkOutputs();
        if (connectedBlocks == null || connectedBlocks.isEmpty())
            return;

        long amountPerOutput = energy.getAmount() / connectedBlocks.size();
        if (amountPerOutput <= 0)
            return;

        try (Transaction transaction = Transaction.openOuter()) {

            for (OutputTarget target : connectedBlocks) {

                var storage = EnergyStorage.SIDED.find(world, target.pos(), target.insertDirection());
                if (storage != null && storage.supportsInsertion()) {

                    long inserted = storage.insert(amountPerOutput, transaction);
                    energy.amount -= inserted;
                }
            }

            transaction.commit();
        }
    }

    // --------------------
    // Sync / Saving
    // --------------------

    @Override
    public List<SyncableStorage> getSyncableStorages() {
        return List.of((SyncableStorage) this.wrappedEnergyStorage.getStorage(null));
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) { }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.put("Energy", this.wrappedEnergyStorage.writeNbt());
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains("Energy", NbtElement.LIST_TYPE))
            this.wrappedEnergyStorage.readNbt(nbt.getList("Energy", NbtElement.COMPOUND_TYPE));
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    // --------------------
    // Сброс сети при изменении
    // --------------------

    public void markDirty() {
        // Сбрасываем connectedBlocks у всех проводов в сети
        traverse(this.pos, pos -> {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof LVcableBlockEntity cable)
                cable.connectedBlocks = null;
        });
    }

    // --------------------
    // Energy getters
    // --------------------

    public SimpleEnergyStorage getEnergy() {
        return this.wrappedEnergyStorage.getStorage(null);
    }

    public SimpleEnergyStorage getEnergyProvider(Direction direction) {
        return this.wrappedEnergyStorage.getStorage(direction);
    }
}
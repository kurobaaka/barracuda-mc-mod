package net.infugogr.barracuda.util.energy;

import net.infugogr.barracuda.util.SyncableStorage;
import net.infugogr.barracuda.util.UpdatableBlockEntity;
import team.reborn.energy.api.base.SimpleEnergyStorage;

public class SyncingEnergyStorage extends SimpleEnergyStorage implements SyncableStorage {
    private final UpdatableBlockEntity blockEntity;
    private boolean isDirty = false;

    public SyncingEnergyStorage(UpdatableBlockEntity blockEntity, long capacity, long maxInput, long maxOutput) {
        super(capacity, maxInput, maxOutput);
        this.blockEntity = blockEntity;
    }

    @Override
    protected void onFinalCommit() {
        super.onFinalCommit();
        this.isDirty = true;
    }

    @Override
    public void sync() {
        if (this.isDirty && this.blockEntity != null && this.blockEntity.hasWorld()) {
            assert this.blockEntity.getWorld() != null;
            if (!this.blockEntity.getWorld().isClient) {
                this.isDirty = false;
            }
        }
    }

    public UpdatableBlockEntity getBlockEntity() {
        return this.blockEntity;
    }
}

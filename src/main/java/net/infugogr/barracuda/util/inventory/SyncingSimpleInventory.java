package net.infugogr.barracuda.util.inventory;

import net.infugogr.barracuda.util.SyncableStorage;
import net.infugogr.barracuda.util.UpdatableBlockEntity;

public class SyncingSimpleInventory extends RecipeSimpleInventory implements SyncableStorage {

    private final UpdatableBlockEntity blockEntity;

    public SyncingSimpleInventory(UpdatableBlockEntity blockEntity, int size) {
        super(size);
        this.blockEntity = blockEntity;
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (blockEntity != null) {
            blockEntity.markForSync();
        }
    }

    @Override
    public void sync() {
        // больше не используется — синхронизация через endTick()
    }
}
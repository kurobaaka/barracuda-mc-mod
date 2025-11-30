package net.infugogr.barracuda.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

public class UpdatableBlockEntity extends BlockEntity {

    private boolean needsSync = false;

    public UpdatableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    /**
     * Вызывается инвентарями/механикой когда требуется синхронизация.
     * Не делает markDirty() немедленно — только помечает флаг.
     */
    public void markForSync() {
        this.needsSync = true;
    }

    /**
     * Вызывается в конце каждого тика (в block entity ticker).
     */
    public void endTick() {
        if (!needsSync) return;
        needsSync = false;

        markDirty();

        if (world != null && !world.isClient) {
            world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_LISTENERS);
        }
    }
}

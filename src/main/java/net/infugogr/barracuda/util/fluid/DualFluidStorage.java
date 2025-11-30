package net.infugogr.barracuda.util.fluid;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DualFluidStorage implements Storage<FluidVariant> {

    private final Storage<FluidVariant> input;
    private final Storage<FluidVariant> output;

    public DualFluidStorage(Storage<FluidVariant> input, Storage<FluidVariant> output) {
        this.input = input;
        this.output = output;
    }

    @Override
    public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction) {
        return input.insert(resource, maxAmount, transaction);
    }

    @Override
    public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
        return output.extract(resource, maxAmount, transaction);
    }

    // ВАЖНО: Storage<T> наследует Iterable<StorageView<T>>
    @Override
    public @NotNull Iterator<StorageView<FluidVariant>> iterator() {
        List<StorageView<FluidVariant>> views = new ArrayList<>();

        for (StorageView<FluidVariant> v : input) {
            views.add(v);
        }

        for (StorageView<FluidVariant> v : output) {
            views.add(v);
        }

        return views.iterator();
    }
}

package net.infugogr.barracuda.util.inventory;

import net.infugogr.barracuda.util.UpdatableBlockEntity;
import net.minecraft.item.ItemStack;

import java.util.function.BiPredicate;

public class PredicateSimpleInventory extends SyncingSimpleInventory {
    private final BiPredicate<ItemStack, Integer> predicate;

    public PredicateSimpleInventory(UpdatableBlockEntity blockEntity, int size, BiPredicate<ItemStack, Integer> predicate) {
        super(blockEntity, size);
        this.predicate = predicate;
    }

    public PredicateSimpleInventory(UpdatableBlockEntity blockEntity, BiPredicate<ItemStack, Integer> predicate, ItemStack... stacks) {
        super(blockEntity, stacks.length);
        this.predicate = predicate;
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        return this.predicate.test(stack, slot);
    }

    public BiPredicate<ItemStack, Integer> getPredicate() {
        return this.predicate;
    }
}
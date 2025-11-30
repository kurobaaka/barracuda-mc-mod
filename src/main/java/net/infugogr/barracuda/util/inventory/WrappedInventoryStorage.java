package net.infugogr.barracuda.util.inventory;

import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.infugogr.barracuda.util.NBTSerializable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WrappedInventoryStorage<T extends SimpleInventory> implements NBTSerializable<NbtList> {

    private final List<T> inventories = new ArrayList<>();
    private final List<InventoryStorage> storages = new ArrayList<>();
    private final Map<Direction, InventoryStorage> sidedStorageMap = new HashMap<>();
    private final CombinedStorage<ItemVariant, InventoryStorage> combinedStorage =
            new CombinedStorage<>(storages);

    // ---------------------------
    //  ИНИЦИАЛИЗАЦИЯ
    // ---------------------------

    public void addInventory(T inventory) {
        addInventory(inventory, null);
    }

    public void addInventory(T inventory, Direction side) {
        inventories.add(inventory);

        InventoryStorage storage = InventoryStorage.of(inventory, side);

        storages.add(storage);
        sidedStorageMap.put(side, storage);
    }

    // ---------------------------
    //  ПОЛУЧЕНИЕ ДАННЫХ
    // ---------------------------

    public List<T> getInventories() {
        return inventories;
    }

    public List<InventoryStorage> getStorages() {
        return storages;
    }

    public CombinedStorage<ItemVariant, InventoryStorage> getCombinedStorage() {
        return combinedStorage;
    }

    public @Nullable InventoryStorage getStorage(Direction side) {
        return sidedStorageMap.get(side);
    }

    public @Nullable T getInventory(int index) {
        if (index < 0 || index >= inventories.size()) return null;
        return inventories.get(index);
    }

    /**
     * Возвращает список всех ItemStack из всех инвентарей (живых, не копий).
     */
    public @NotNull List<ItemStack> getStacks() {
        List<ItemStack> list = new ArrayList<>();
        for (T inv : inventories) {
            for (int i = 0; i < inv.size(); i++) {
                list.add(inv.getStack(i));
            }
        }
        return list;
    }

    /**
     * Проверка, что общая сумма всех слотов равна ожидаемой.
     */
    public void checkSize(int size) {
        int sum = inventories.stream()
                .mapToInt(Inventory::size)
                .sum();

        if (sum != size)
            throw new IllegalArgumentException(
                    "Size mismatch: inventories=" + sum + " expected=" + size
            );
    }

    // ---------------------------
    //  ХУКИ
    // ---------------------------

    public void onOpen(PlayerEntity player) {
        inventories.forEach(inv -> inv.onOpen(player));
    }

    public void onClose(PlayerEntity player) {
        inventories.forEach(inv -> inv.onClose(player));
    }

    public void dropContents(World world, BlockPos pos) {
        inventories.forEach(inv -> ItemScatterer.spawn(world, pos, inv));
    }

    public RecipeSimpleInventory getRecipeInventory() {
        return new RecipeSimpleInventory(getStacks().toArray(new ItemStack[0]));
    }

    // ---------------------------
    //  NBT
    // ---------------------------

    @Override
    public NbtList writeNbt() {
        NbtList list = new NbtList();

        for (SimpleInventory inv : inventories) {
            NbtCompound tag = new NbtCompound();
            Inventories.writeNbt(tag, inv.getHeldStacks()); // безопасно
            list.add(tag);
        }
        return list;
    }

    @Override
    public void readNbt(NbtList list) {
        for (int i = 0; i < list.size(); i++) {
            NbtCompound tag = list.getCompound(i);
            SimpleInventory inv = inventories.get(i);
            Inventories.readNbt(tag, inv.getHeldStacks());
        }
    }
}

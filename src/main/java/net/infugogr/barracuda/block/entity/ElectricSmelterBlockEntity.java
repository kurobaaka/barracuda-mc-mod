package net.infugogr.barracuda.block.entity;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.infugogr.barracuda.Barracuda;
import net.infugogr.barracuda.screenhandler.ElectricSmelterScreenHandler;
import net.infugogr.barracuda.util.SyncableStorage;
import net.infugogr.barracuda.util.SyncableTickableBlockEntity;
import net.infugogr.barracuda.util.UpdatableBlockEntity;
import net.infugogr.barracuda.util.energy.EnergySpreader;
import net.infugogr.barracuda.util.energy.SyncingEnergyStorage;
import net.infugogr.barracuda.util.energy.WrappedEnergyStorage;
import net.infugogr.barracuda.util.inventory.OutputSimpleInventory;
import net.infugogr.barracuda.util.inventory.SyncingSimpleInventory;
import net.infugogr.barracuda.util.inventory.WrappedInventoryStorage;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

import java.util.List;
import java.util.Optional;

import static net.minecraft.block.entity.AbstractFurnaceBlockEntity.createFuelTimeMap;

public class ElectricSmelterBlockEntity extends UpdatableBlockEntity implements SyncableTickableBlockEntity, EnergySpreader, ExtendedScreenHandlerFactory {
    public static final Text TITLE = Barracuda.containerTitle("electric_smelter");
    private final WrappedEnergyStorage energyStorage = new WrappedEnergyStorage();
    private final WrappedInventoryStorage<SimpleInventory> inventoryStorage = new WrappedInventoryStorage<>();
    protected final PropertyDelegate propertyDelegate;
    private int progress = 0;
    private int maxProgress = 0;

    public ElectricSmelterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityType.ELECTRIC_SMELTER, pos, state);
        this.energyStorage.addStorage(new SyncingEnergyStorage(this, 5000, 100, 0));
        this.inventoryStorage.addInventory(new SyncingSimpleInventory(this, 1), Direction.UP);
        this.inventoryStorage.addInventory(new OutputSimpleInventory(this, 1), Direction.DOWN);
        this.inventoryStorage.addInventory(new SyncingSimpleInventory(this, 3));
        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> ElectricSmelterBlockEntity.this.progress;
                    case 1 -> ElectricSmelterBlockEntity.this.maxProgress;
                    case 2 -> (int) ElectricSmelterBlockEntity.this.energyStorage.getStorage(null).getAmount();
                    case 3 -> (int) ElectricSmelterBlockEntity.this.energyStorage.getStorage(null).getCapacity();
                    default -> 0;
                };
            }
            @Override
            public void set(int index, int value) {
            }
            @Override
            public int size() {
                return 4;
            }
        };
    }
    @Override
    public List<SyncableStorage> getSyncableStorages() {
        var energy = (SyncingEnergyStorage) this.energyStorage.getStorage(null);
        var input = (SyncingSimpleInventory) this.inventoryStorage.getInventory(0);
        var output = (SyncingSimpleInventory) this.inventoryStorage.getInventory(1);
        var upgrade = (SyncingSimpleInventory) this.inventoryStorage.getInventory(2);
        assert input != null;
        assert output != null;
        assert upgrade != null;
        return List.of(energy, input, output, upgrade);
    }

    @Override
    public void onTick() {
        if (this.world == null || this.world.isClient) return;

        var energy = (SyncingEnergyStorage) this.energyStorage.getStorage(null);
        var inputInv = (SyncingSimpleInventory) this.inventoryStorage.getInventory(0);
        var outputInv = (SyncingSimpleInventory) this.inventoryStorage.getInventory(1);

        assert inputInv != null;
        assert outputInv != null;

        ItemStack inputStack = inputInv.getStack(0);
        ItemStack outputStack = outputInv.getStack(0);

        // если нет входного предмета — сброс прогресса
        if (inputStack.isEmpty()) {
            if (this.progress > 0) {
                this.progress = 0;
                markDirty();
            }
            return;
        }

        // ищем рецепт плавки
        Optional<RecipeEntry<SmeltingRecipe>> match = world.getRecipeManager()
                .getFirstMatch(RecipeType.SMELTING, inputInv, world);

        if (match.isEmpty()) {
            // нет рецепта — сброс прогресса
            if (this.progress > 0) {
                this.progress = 0;
                markDirty();
            }
            return;
        }

        SmeltingRecipe recipe = match.get().value();
        ItemStack result = recipe.getResult(world.getRegistryManager()).copy();

        // задаем время плавки — в 2 раза быстрее обычной печки
        this.maxProgress = recipe.getCookingTime() / 2;

        // энергопотребление (можешь подстроить)
        int energyPerTick = 20; // RF/t (пример)

        // проверяем энергию
        if (energy.getAmount() < energyPerTick) {
            // не хватает энергии — пауза
            return;
        }

        // проверяем, можно ли вставить результат в выходной слот
        if (!canInsertIntoOutput(result, outputStack)) {
            // выход забит
            return;
        }

        // тратим энергию и продвигаем прогресс
        energy.amount -= energyPerTick;
        this.progress++;

        // если плавка завершена
        if (this.progress >= this.maxProgress) {
            // потребляем вход
            inputStack.decrement(1);

            // кладем результат
            if (outputStack.isEmpty()) {
                outputInv.setStack(0, result);
            } else {
                outputStack.increment(result.getCount());
            }

            // сбрасываем прогресс
            this.progress = 0;

            // помечаем блок как изменённый
            markDirty();
        }
    }

    private boolean canInsertIntoOutput(ItemStack result, ItemStack outputStack) {
        if (outputStack.isEmpty()) return true;
        if (!outputStack.isOf(result.getItem())) return false;
        return outputStack.getCount() + result.getCount() <= outputStack.getMaxCount();
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf packetByteBuf) {
        packetByteBuf.writeBlockPos(this.pos);
    }
    @Override
    public void writeNbt(NbtCompound nbt) {
        nbt.put("EnergyStorage", this.energyStorage.writeNbt());
        nbt.put("Inventory", this.inventoryStorage.writeNbt());
        nbt.putInt("BurnTime", this.progress);
        nbt.putInt("FuelTime", this.maxProgress);
    }
    @Override
    public void readNbt(NbtCompound nbt) {
        this.energyStorage.readNbt(nbt.getList("EnergyStorage", NbtElement.COMPOUND_TYPE));
        this.inventoryStorage.readNbt(nbt.getList("Inventory", NbtElement.COMPOUND_TYPE));
        this.progress = nbt.getInt("BurnTime");
        this.maxProgress = nbt.getInt("FuelTime");
    }
    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }
    @Override
    public Text getDisplayName() {
        return TITLE;
    }
    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new ElectricSmelterScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }
    public WrappedInventoryStorage<SimpleInventory> getWrappedInventoryStorage() {
        return this.inventoryStorage;
    }
    public EnergyStorage getEnergyProvider(Direction direction) {
        return this.energyStorage.getStorage(direction);
    }
    public InventoryStorage getInventoryProvider(Direction direction) {
        return this.inventoryStorage.getStorage(direction);
    }
    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }
}

package net.infugogr.barracuda.block.entity;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.SingleFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.infugogr.barracuda.Barracuda;
import net.infugogr.barracuda.block.recipes.ChemicalPlantRecipe;
import net.infugogr.barracuda.block.recipes.ChemicalPlantRecipeManager;
import net.infugogr.barracuda.item.CapsuleItem;
import net.infugogr.barracuda.mixin.BucketItemMixin;
import net.infugogr.barracuda.screenhandler.ChemicalPlantScreenHandler;
import net.infugogr.barracuda.util.ModTags;
import net.infugogr.barracuda.util.SyncableStorage;
import net.infugogr.barracuda.util.SyncableTickableBlockEntity;
import net.infugogr.barracuda.util.UpdatableBlockEntity;
import net.infugogr.barracuda.util.energy.EnergySpreader;
import net.infugogr.barracuda.util.energy.SyncingEnergyStorage;
import net.infugogr.barracuda.util.energy.WrappedEnergyStorage;
import net.infugogr.barracuda.util.fluid.SyncingFluidStorage;
import net.infugogr.barracuda.util.fluid.WrappedFluidStorage;
import net.infugogr.barracuda.util.inventory.OutputSimpleInventory;
import net.infugogr.barracuda.util.inventory.SyncingSimpleInventory;
import net.infugogr.barracuda.util.inventory.WrappedInventoryStorage;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ChemicalPlantBlockEntity extends UpdatableBlockEntity implements SyncableTickableBlockEntity, EnergySpreader, ExtendedScreenHandlerFactory {
    public static final Text TITLE = Barracuda.containerTitle("chemical_plant");
    private final WrappedEnergyStorage energyStorage = new WrappedEnergyStorage();
    private final WrappedFluidStorage<SingleFluidStorage> fluidStorage = new WrappedFluidStorage<>();
    private final WrappedInventoryStorage<SimpleInventory> inventoryStorage = new WrappedInventoryStorage<>();
    protected final PropertyDelegate propertyDelegate;
    private int progress = 0;
    private int maxProgress = 0;
    private final int maxCapacity = (int) (FluidConstants.BUCKET * 10);

    public ChemicalPlantBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityType.CHEMICAL_PLANT, pos, state);
        this.energyStorage.addStorage(new SyncingEnergyStorage(this, 5000, 100, 0));
        this.inventoryStorage.addInventory(new SyncingSimpleInventory(this, 3));
        this.inventoryStorage.addInventory(new SyncingSimpleInventory(this, 2));
        this.inventoryStorage.addInventory(new OutputSimpleInventory(this, 1));
        this.fluidStorage.addStorage(new SyncingFluidStorage(this, maxCapacity), Direction.UP);
        this.fluidStorage.addStorage(new SyncingFluidStorage(this, maxCapacity), Direction.DOWN);
        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> ChemicalPlantBlockEntity.this.progress;
                    case 1 -> ChemicalPlantBlockEntity.this.maxProgress;
                    case 2 -> (int) ChemicalPlantBlockEntity.this.energyStorage.getStorage(null).getAmount();
                    case 3 -> (int) ChemicalPlantBlockEntity.this.energyStorage.getStorage(null).getCapacity();
                    case 4 -> maxCapacity;
                    case 5 -> (int) ChemicalPlantBlockEntity.this.fluidStorage.getStorage(Direction.UP).getAmount();
                    case 6 -> (int) ChemicalPlantBlockEntity.this.fluidStorage.getStorage(Direction.DOWN).getAmount();
                    case 7 -> inputFluidId();
                    case 8 -> outputFluidId();
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
            }

            @Override
            public int size() {
                return 9;
            }
        };
    }

    @Override
    public List<SyncableStorage> getSyncableStorages() {
        var energy = (SyncingEnergyStorage) this.energyStorage.getStorage(null);
        var slot_1 = (SyncingSimpleInventory) this.inventoryStorage.getInventory(0);
        var slot_2 = (SyncingSimpleInventory) this.inventoryStorage.getInventory(1);
        var slot_3 = (SyncingSimpleInventory) this.inventoryStorage.getInventory(2);
        var input_fluid = (SyncingFluidStorage) this.fluidStorage.getStorage(0);
        var output_fluid = (SyncingFluidStorage) this.fluidStorage.getStorage(1);
        assert slot_1 != null;
        assert slot_2 != null;
        assert slot_3 != null;
        return List.of(energy, slot_1, slot_2, slot_3, input_fluid, output_fluid);
    }

    @Override
    public void onTick() {
        if (world == null || world.isClient) return;

        ItemStack slotA = Objects.requireNonNull(this.inventoryStorage.getInventory(1)).getStack(0);
        ItemStack slotB = Objects.requireNonNull(this.inventoryStorage.getInventory(1)).getStack(1);
        var inputTank = this.fluidStorage.getStorage(Direction.UP);
        FluidVariant inputVariant = inputTank.variant;
        Fluid inputFluid = inputVariant.getFluid();
        long inputAmount = inputTank.amount;
        SingleFluidStorage outputTank = fluidStorage.getStorage(Direction.DOWN);

        Optional<ChemicalPlantRecipe> optional =
                ChemicalPlantRecipeManager.getInstance().getRecipeFor(slotA, slotB, inputFluid);

        if (optional.isEmpty()) {
            this.progress = 0;
            return;
        }

        ChemicalPlantRecipe recipe = optional.get();
        maxProgress = recipe.craftTime;

        // ENERGY CHECK
        long energy = this.energyStorage.getStorage(null).amount;

        if (energy < 20) {
            this.progress = 0;
            return;
        }


        // FLUID CHECK
        if (recipe.inputFluid != null) {
            long required = recipe.inputFluidAmount;
            if (inputAmount < required) {
                this.progress = 0;
                return;
            }
        }


        // OUTPUT ITEM CHECK
        ItemStack outputSlot = Objects.requireNonNull(this.inventoryStorage.getInventory(2)).getStack(0);
        ItemStack recipeOut = recipe.outputItem;

        if (!outputSlot.isEmpty()) {

            if (!ItemStack.canCombine(recipeOut, outputSlot)) {
                this.progress = 0;
                return;
            }
            if (outputSlot.getCount() + recipeOut.getCount() > outputSlot.getMaxCount()) {
                this.progress = 0;
                return;
            }
        }

        // PROCESS
        this.progress++;
        this.energyStorage.getStorage(null).amount -= 20;

        if (this.progress >= recipe.craftTime) {

            // Consume items
            if (!slotA.isEmpty())
                slotA.decrement(recipe.inputs.get(0).getCount());

            if (recipe.inputs.size() > 1 && !slotB.isEmpty())
                slotB.decrement(recipe.inputs.get(1).getCount());


            // Consume fluid
            if (recipe.inputFluid != null) {

                try (var tx = Transaction.openOuter()) {
                    inputTank.extract(FluidVariant.of(recipe.inputFluid), recipe.inputFluidAmount, tx);
                    tx.commit();
                }
            }
            if (!recipeOut.isEmpty()) {

                if (outputSlot.isEmpty()) {
                    Objects.requireNonNull(this.inventoryStorage.getInventory(2)).setStack(0, recipeOut.copy());
                } else {
                    outputSlot.increment(recipeOut.getCount());
                }
            }
            if (recipe.outputFluid != null) {
                try (var tx = Transaction.openOuter()) {
                    outputTank.insert(FluidVariant.of(recipe.outputFluid), recipe.outputFluidAmount, tx);
                    tx.commit();
                }
            }

            this.progress = 0;
        }
    }


    @Override
    public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf packetByteBuf) {
        packetByteBuf.writeBlockPos(this.pos);
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
        return new ChemicalPlantScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
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

    public Storage<FluidVariant> getFluidProvider(Direction dir) {
        return this.fluidStorage.getStorage(dir);
    }
    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    public ActionResult setFluid(ItemStack stack, PlayerEntity player) {
        SingleFluidStorage input = this.fluidStorage.getStorage(Direction.UP);
        SingleFluidStorage output = this.fluidStorage.getStorage(Direction.DOWN);

        // Проверка, что предмет подходит
        if (!stack.isIn(ModTags.FLUIDS)) {
            return ActionResult.PASS;
        }

        Item item = stack.getItem();
        Fluid fluid;
        boolean isEmptyContainer;
        ItemStack filledResult = ItemStack.EMPTY;
        ItemStack emptyResult = stack.getRecipeRemainder();

        // Определяем, какой это контейнер и какую жидкость он содержит
        if (item instanceof CapsuleItem capsule) {
            fluid = capsule.getFluid();
            isEmptyContainer = (fluid == Fluids.EMPTY);
        } else if (item instanceof BucketItem bucket) {
            fluid = ((BucketItemMixin) item).getFluid();
            isEmptyContainer = (fluid == Fluids.EMPTY);
        } else {
            return ActionResult.PASS;
        }

        // Логика для заполненных контейнеров (вставляем жидкость в input)
        if (!isEmptyContainer) {
            FluidVariant variant = FluidVariant.of(fluid);

            try (Transaction t = Transaction.openOuter()) {
                long inserted = input.insert(variant, FluidConstants.BUCKET, t);

                if (inserted == FluidConstants.BUCKET) {
                    t.commit();

                    if (!player.getWorld().isClient()) {
                        ItemUsage.exchangeStack(stack, player, emptyResult);
                    }
                    return ActionResult.SUCCESS;
                }
            }
            return ActionResult.PASS;
        }

        // Логика для пустых контейнеров (берём жидкость)
        // Если output пусто, берём из input
        SingleFluidStorage sourceTank = output.getResource().getFluid() == Fluids.EMPTY ? input : output;

        FluidVariant tankVariant = sourceTank.getResource();
        Fluid sourceFluid = tankVariant.getFluid();

        if (sourceFluid == Fluids.EMPTY) {
            return ActionResult.PASS;
        }

        // Определяем, чем заполняем контейнер
        if (item instanceof CapsuleItem capsuleItem) {
            filledResult = capsuleItem.getFilledVariant(sourceFluid);
        } else if (item instanceof BucketItem) {
            filledResult = new ItemStack(sourceFluid.getBucketItem());
        }

        try (Transaction t = Transaction.openOuter()) {
            long extracted = sourceTank.extract(tankVariant, FluidConstants.BUCKET, t);

            if (extracted == FluidConstants.BUCKET) {
                t.commit();

                if (!player.getWorld().isClient()) {
                    ItemUsage.exchangeStack(stack, player, filledResult);
                }
                return ActionResult.SUCCESS;
            }
        }

        return ActionResult.PASS;
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        // Сохраняем стандартные вещи (энергия, инвентарь)
        nbt.put("EnergyStorage", this.energyStorage.writeNbt());
        nbt.put("Inventory", this.inventoryStorage.writeNbt());

        // Сохраняем флюиды в простом и предсказуемом формате
        NbtCompound fluids = new NbtCompound();

        // У нас два направления: UP (input) и DOWN (output)
        Direction[] dirs = new Direction[] { Direction.UP, Direction.DOWN };
        for (int i = 0; i < dirs.length; i++) {
            Direction dir = dirs[i];
            SingleFluidStorage tank = this.fluidStorage.getStorage(dir);
            if (tank == null) continue;

            FluidVariant variant = tank.getResource();
            Fluid fluid = variant.getFluid();
            long amount = tank.getAmount();

            // Сохраняем id жидкости (registry raw id) и количество
            fluids.putInt("Fluid_" + i + "_Id", Registries.FLUID.getRawId(fluid));
            fluids.putLong("Fluid_" + i + "_Amount", amount);

            // Если у варианта есть дополнительные NBT данные — сохраняем их
            NbtCompound variantTag = variant.getNbt(); // может вернуть null
            if (variantTag != null && !variantTag.isEmpty()) {
                fluids.put("Fluid_" + i + "_Variant", variantTag);
            }
        }

        nbt.put("FluidStorageSimple", fluids);

        // Сохраняем прогресс
        nbt.putInt("BurnTime", this.progress);
        nbt.putInt("FuelTime", this.maxProgress);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        // Считываем энергию и инвентарь
        if (nbt.contains("EnergyStorage", NbtElement.LIST_TYPE)) {
            this.energyStorage.readNbt(nbt.getList("EnergyStorage", NbtElement.COMPOUND_TYPE));
        }
        if (nbt.contains("Inventory", NbtElement.LIST_TYPE)) {
            this.inventoryStorage.readNbt(nbt.getList("Inventory", NbtElement.COMPOUND_TYPE));
        }

        // Восстанавливаем флюиды
        if (nbt.contains("FluidStorageSimple", NbtElement.COMPOUND_TYPE)) {
            NbtCompound fluids = nbt.getCompound("FluidStorageSimple");

            Direction[] dirs = new Direction[] { Direction.UP, Direction.DOWN };
            for (int i = 0; i < dirs.length; i++) {
                Direction dir = dirs[i];
                SingleFluidStorage tank = this.fluidStorage.getStorage(dir);
                if (tank == null) continue;

                // Читаем id и количество
                int fluidId = fluids.contains("Fluid_" + i + "_Id") ? fluids.getInt("Fluid_" + i + "_Id") : Registries.FLUID.getRawId(Fluids.EMPTY);
                long amount = fluids.contains("Fluid_" + i + "_Amount") ? fluids.getLong("Fluid_" + i + "_Amount") : 0L;

                Fluid fluid = Registries.FLUID.get(fluidId);
                NbtCompound variantTag = fluids.contains("Fluid_" + i + "_Variant", NbtElement.COMPOUND_TYPE) ?
                        fluids.getCompound("Fluid_" + i + "_Variant") : null;

                FluidVariant variant = variantTag != null ? FluidVariant.of(fluid, variantTag) : FluidVariant.of(fluid);

                // Очищаем текущий бак и вставляем сохранённый ресурс через транзакцию
                try (Transaction tx = Transaction.openOuter()) {
                    // сначала пытаемся полностью извлечь существующее
                    long cur = tank.getAmount();
                    if (cur > 0) {
                        tank.extract(tank.getResource(), cur, tx);
                    }
                    if (amount > 0 && !variant.isBlank()) {
                        tank.insert(variant, amount, tx);
                    }
                    tx.commit();
                } catch (Exception e) {
                    // Логируем
                    e.printStackTrace();
                }
            }
        }

        // Прогресс
        if (nbt.contains("BurnTime")) this.progress = nbt.getInt("BurnTime");
        if (nbt.contains("FuelTime")) this.maxProgress = nbt.getInt("FuelTime");
    }

    int inputFluidId() {
        return Registries.FLUID.getRawId(
                fluidStorage.getStorage(Direction.UP).getResource().getFluid());
    }

    int outputFluidId() {
        return Registries.FLUID.getRawId(
                fluidStorage.getStorage(Direction.DOWN).getResource().getFluid());
    }

    public void sync() {
        if (world != null && !world.isClient) {
            world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
            markDirty();
        }
    }
}

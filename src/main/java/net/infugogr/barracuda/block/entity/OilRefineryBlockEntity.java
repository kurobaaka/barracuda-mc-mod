package net.infugogr.barracuda.block.entity;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.SingleFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.infugogr.barracuda.Barracuda;
import net.infugogr.barracuda.item.ModItems;
import net.infugogr.barracuda.screenhandler.OilRefineryScreenHandler;
import net.infugogr.barracuda.util.SyncableStorage;
import net.infugogr.barracuda.util.SyncableTickableBlockEntity;
import net.infugogr.barracuda.util.UpdatableBlockEntity;
import net.infugogr.barracuda.util.energy.EnergySpreader;
import net.infugogr.barracuda.util.energy.SyncingEnergyStorage;
import net.infugogr.barracuda.util.energy.WrappedEnergyStorage;
import net.infugogr.barracuda.util.fluid.OutputFluidStorage;
import net.infugogr.barracuda.util.fluid.SyncingFluidStorage;
import net.infugogr.barracuda.util.fluid.WrappedFluidStorage;
import net.infugogr.barracuda.util.inventory.SyncingSimpleInventory;
import net.infugogr.barracuda.util.inventory.WrappedInventoryStorage;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.base.SimpleEnergyStorage;

import java.util.List;

public class OilRefineryBlockEntity extends UpdatableBlockEntity implements SyncableTickableBlockEntity, EnergySpreader, ExtendedScreenHandlerFactory {
    public static final Text TITLE = Barracuda.containerTitle("oil_refinery");
    private final WrappedEnergyStorage energyStorage = new WrappedEnergyStorage();
    private final WrappedFluidStorage<SingleFluidStorage> fluidStorage = new WrappedFluidStorage<>();
    private final WrappedInventoryStorage<SimpleInventory> inventoryStorage = new WrappedInventoryStorage<>();
    protected final PropertyDelegate propertyDelegate;
    private int progress = 0;
    private int maxProgress = 900;
    private final int maxCapacity = (int) (FluidConstants.BUCKET * 10);

    public OilRefineryBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityType.OIL_REFINERY, pos, state);
        this.energyStorage.addStorage(new SyncingEnergyStorage(this, 5000, 100, 0));
        this.inventoryStorage.addInventory(new SyncingSimpleInventory(this, 1));
        this.inventoryStorage.addInventory(new SyncingSimpleInventory(this, 1));
        this.inventoryStorage.addInventory(new SyncingSimpleInventory(this, 1));
        this.fluidStorage.addStorage(new SyncingFluidStorage(this, maxCapacity), Direction.UP);
        this.fluidStorage.addStorage(new OutputFluidStorage(this, maxCapacity), Direction.SOUTH);
        this.fluidStorage.addStorage(new OutputFluidStorage(this, maxCapacity), Direction.EAST);
        this.fluidStorage.addStorage(new OutputFluidStorage(this, maxCapacity), Direction.WEST);
        this.fluidStorage.addStorage(new SyncingFluidStorage(this, maxCapacity), Direction.DOWN);
        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> OilRefineryBlockEntity.this.progress;
                    case 1 -> OilRefineryBlockEntity.this.maxProgress;
                    case 2 -> (int) OilRefineryBlockEntity.this.energyStorage.getStorage(null).getAmount();
                    case 3 -> (int) OilRefineryBlockEntity.this.energyStorage.getStorage(null).getCapacity();
                    case 4 -> maxCapacity;
                    case 5 -> (int) OilRefineryBlockEntity.this.fluidStorage.getStorage(Direction.UP).getAmount();
                    case 6 -> (int) OilRefineryBlockEntity.this.fluidStorage.getStorage(Direction.SOUTH).getAmount();
                    case 7 -> (int) OilRefineryBlockEntity.this.fluidStorage.getStorage(Direction.EAST).getAmount();
                    case 8 -> (int) OilRefineryBlockEntity.this.fluidStorage.getStorage(Direction.WEST).getAmount();
                    case 9 -> (int) OilRefineryBlockEntity.this.fluidStorage.getStorage(Direction.DOWN).getAmount();
                    default -> 0;
                };
            }
            @Override
            public void set(int index, int value) {
            }
            @Override
            public int size() {
                return 10;
            }
        };
    }
    @Override
    public List<SyncableStorage> getSyncableStorages() {
        var energy = (SyncingEnergyStorage) this.energyStorage.getStorage(null);
        var slot_1 = (SyncingSimpleInventory) this.inventoryStorage.getInventory(0);
        var slot_2 = (SyncingSimpleInventory) this.inventoryStorage.getInventory(1);
        var slot_3 = (SyncingSimpleInventory) this.inventoryStorage.getInventory(2);
        var oil = (SyncingFluidStorage) this.fluidStorage.getStorage(0);
        var heavy_oil = (SyncingFluidStorage) this.fluidStorage.getStorage(1);
        var diesel = (SyncingFluidStorage) this.fluidStorage.getStorage(2);
        var gas = (SyncingFluidStorage) this.fluidStorage.getStorage(3);
        var water = (SyncingFluidStorage) this.fluidStorage.getStorage(4);
        assert slot_1 != null;
        assert slot_2 != null;
        assert slot_3 != null;
        return List.of(energy, slot_1, slot_2, slot_3, oil, heavy_oil, diesel, gas, water);
    }
    @Override
    public void onTick() {
        if (this.world == null || this.world.isClient)
            return;
        SimpleEnergyStorage energyStorage = this.energyStorage.getStorage(null);
        SingleFluidStorage oilStorage =  this.fluidStorage.getStorage(Direction.UP);
        SingleFluidStorage hewOilStorage =  this.fluidStorage.getStorage(Direction.SOUTH);
        SingleFluidStorage disStorage =  this.fluidStorage.getStorage(Direction.EAST);
        SingleFluidStorage gasStorage =  this.fluidStorage.getStorage(Direction.WEST);
        SingleFluidStorage watStorage =  this.fluidStorage.getStorage(Direction.DOWN);
        if (energyStorage.amount < 20)
            return;
        if ((oilStorage.amount<FluidConstants.BUCKET) || (watStorage.amount<FluidConstants.BUCKET * 0.5)) {
            int currentProgress = this.progress;
            this.progress = 0;

            if (currentProgress > 0)
                return;
        }
        if (this.progress >= this.maxProgress) {
            this.progress = 0;

            hewOilStorage.amount += (long) (FluidConstants.BUCKET * 0.25);
            disStorage.amount += (long) (FluidConstants.BUCKET * 0.45);
            gasStorage.amount += (long) (FluidConstants.BUCKET * 0.55);
            oilStorage.amount -= FluidConstants.BUCKET;
            watStorage.amount -= (long) (FluidConstants.BUCKET * 0.5);

            return;
        }

        this.progress++;
        energyStorage.amount -= 20;
    }
    @Override
    public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf packetByteBuf) {
        packetByteBuf.writeBlockPos(this.pos);
    }
    @Override
    public void writeNbt(NbtCompound nbt) {
        nbt.put("EnergyStorage", this.energyStorage.writeNbt());
        nbt.put("Inventory", this.inventoryStorage.writeNbt());
        nbt.put("FluidStorage", this.fluidStorage.writeNbt());
        nbt.putInt("BurnTime", this.progress);
        nbt.putInt("FuelTime", this.maxProgress);
    }
    @Override
    public void readNbt(NbtCompound nbt) {
        this.energyStorage.readNbt(nbt.getList("EnergyStorage", NbtElement.COMPOUND_TYPE));
        this.inventoryStorage.readNbt(nbt.getList("Inventory", NbtElement.COMPOUND_TYPE));
        this.fluidStorage.readNbt(nbt.getList("FluidStorage", NbtElement.COMPOUND_TYPE));
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
        return new OilRefineryScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
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
    public SingleFluidStorage getFluidProvider(Direction direction) {
        return this.fluidStorage.getStorage(direction);
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    public ActionResult setFluid(ItemStack stack, PlayerEntity player) {
        SingleFluidStorage oil = this.fluidStorage.getStorage(Direction.UP);
        SingleFluidStorage water = this.fluidStorage.getStorage(Direction.DOWN);
        SingleFluidStorage heavy_oil =  this.fluidStorage.getStorage(Direction.SOUTH);
        SingleFluidStorage diesel =  this.fluidStorage.getStorage(Direction.EAST);
        SingleFluidStorage gas =  this.fluidStorage.getStorage(Direction.WEST);

        if (oil.getAmount() <= oil.getCapacity() - FluidConstants.BUCKET & (stack.getItem() == ModItems.CRUDE_OIL_BUCKET | stack.getItem() == ModItems.CRUDE_OIL_CAPSULE)) {
            ItemUsage.exchangeStack(stack, player, stack.getRecipeRemainder());
            oil.amount += FluidConstants.BUCKET;
            return  ActionResult.SUCCESS;
        }
        else if (water.getAmount() <= water.getCapacity() - FluidConstants.BUCKET & (stack.getItem() == Items.WATER_BUCKET | stack.getItem() == ModItems.WATER_CAPSULE)) {
            ItemUsage.exchangeStack(stack, player, stack.getRecipeRemainder());
            water.amount += FluidConstants.BUCKET;
            return  ActionResult.SUCCESS;
        }
        else if (heavy_oil.getAmount() >= FluidConstants.BUCKET & (stack.getItem() == Items.BUCKET | stack.getItem() == ModItems.EMPTY_CAPSULE)) {
            if (stack.getItem() == Items.BUCKET){
                ItemUsage.exchangeStack(stack, player, ModItems.HEAVY_OIL_BUCKET.getDefaultStack());
            } else {
                ItemUsage.exchangeStack(stack, player, ModItems.HEAVY_OIL_CAPSULE.getDefaultStack());
            }
            heavy_oil.amount -= FluidConstants.BUCKET;
            return ActionResult.SUCCESS;
        }
        else if (diesel.getAmount() >= FluidConstants.BUCKET & (stack.getItem() == Items.BUCKET | stack.getItem() == ModItems.EMPTY_CAPSULE)) {
            if (stack.getItem() == Items.BUCKET){
                ItemUsage.exchangeStack(stack, player, ModItems.DIESEL_BUCKET.getDefaultStack());
            } else {
                ItemUsage.exchangeStack(stack, player, ModItems.DIESEL_CAPSULE.getDefaultStack());
            }
            diesel.amount -= FluidConstants.BUCKET;
            return ActionResult.SUCCESS;
        }
        else if (gas.getAmount() >= FluidConstants.BUCKET & (stack.getItem() == Items.BUCKET | stack.getItem() == ModItems.EMPTY_CAPSULE)) {
            if (stack.getItem() == Items.BUCKET){
                ItemUsage.exchangeStack(stack, player, ModItems.GAS_BUCKET.getDefaultStack());
            } else {
                ItemUsage.exchangeStack(stack, player, ModItems.GAS_CAPSULE.getDefaultStack());
            }
            gas.amount -= FluidConstants.BUCKET;
            return ActionResult.SUCCESS;
        }
        else {return  ActionResult.PASS;}
    }
}

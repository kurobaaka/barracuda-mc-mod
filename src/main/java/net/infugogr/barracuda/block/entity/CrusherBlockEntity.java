package net.infugogr.barracuda.block.entity;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.infugogr.barracuda.Barracuda;
import net.infugogr.barracuda.block.ModBlocks;
import net.infugogr.barracuda.block.recipes.CrusherRecipes;
import net.infugogr.barracuda.item.ModItems;
import net.infugogr.barracuda.screenhandler.CrusherScreenHandler;
import net.infugogr.barracuda.util.ModTags;
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
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.base.SimpleEnergyStorage;

import java.util.List;

import static net.minecraft.block.entity.AbstractFurnaceBlockEntity.createFuelTimeMap;

public class CrusherBlockEntity extends UpdatableBlockEntity implements SyncableTickableBlockEntity, EnergySpreader, ExtendedScreenHandlerFactory {
    public static final Text TITLE = Barracuda.containerTitle("crusher");
    private final WrappedEnergyStorage energyStorage = new WrappedEnergyStorage();
    private final WrappedInventoryStorage<SimpleInventory> inventoryStorage = new WrappedInventoryStorage<>();
    protected final PropertyDelegate propertyDelegate;
    private int progress = 0;
    private int maxProgress = 600;

    public CrusherBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityType.CRUSHER, pos, state);
        this.energyStorage.addStorage(new SyncingEnergyStorage(this, 5000, 100, 0));
        this.inventoryStorage.addInventory(new SyncingSimpleInventory(this, 1));
        this.inventoryStorage.addInventory(new SyncingSimpleInventory(this, 1));
        this.inventoryStorage.addInventory(new SyncingSimpleInventory(this, 1));
        this.inventoryStorage.addInventory(new SyncingSimpleInventory(this, 1), Direction.UP);
        this.inventoryStorage.addInventory(new OutputSimpleInventory(this, 1), Direction.DOWN);
        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> CrusherBlockEntity.this.progress;
                    case 1 -> CrusherBlockEntity.this.maxProgress;
                    case 2 -> (int) CrusherBlockEntity.this.energyStorage.getStorage(null).getAmount();
                    case 3 -> (int) CrusherBlockEntity.this.energyStorage.getStorage(null).getCapacity();
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
        var slot_1 = (SyncingSimpleInventory) this.inventoryStorage.getInventory(0);
        var slot_2 = (SyncingSimpleInventory) this.inventoryStorage.getInventory(1);
        var slot_3 = (SyncingSimpleInventory) this.inventoryStorage.getInventory(2);
        var slot_4 = (SyncingSimpleInventory) this.inventoryStorage.getInventory(3);
        var slot_5 = (SyncingSimpleInventory) this.inventoryStorage.getInventory(4);
        assert slot_1 != null;
        assert slot_2 != null;
        assert slot_3 != null;
        assert slot_4 != null;
        assert slot_5 != null;
        return List.of(energy, slot_1, slot_2, slot_3, slot_4, slot_5);
    }
    @Override
    public void onTick() {
        if (this.world == null || this.world.isClient)
            return;
        SimpleEnergyStorage energyStorage = this.energyStorage.getStorage(null);
        SimpleInventory inventory =  this.inventoryStorage.getInventory(3);
        assert inventory != null;
        ItemStack input = inventory.getStack(0);
        SimpleInventory inventory2 =  this.inventoryStorage.getInventory(4);
        assert inventory2 != null;
        ItemStack output = inventory2.getStack(0);

        if (energyStorage.amount < 20)
            return;
        if (!input.isIn(ModTags.CRUSHER_RECIPE_ITEMS)) {
            int currentProgress = this.progress;
            this.progress = 0;

            if (currentProgress > 0)
                update();

            return;
        }
        this.maxProgress = 600;
        if(output.getCount() >= 64) {
            return;
        }
        if (this.progress >= this.maxProgress) {
            this.progress = 0;
            input.decrement(1);
            if (output == ItemStack.EMPTY) {
                inventory2.setStack(0, CrusherRecipes.getCrusherOutput(input.getItem()).getDefaultStack());
                update();
                output.increment(1);
            } else {
                output.increment(2);
            }
            update();
            return;
        }

        this.progress++;
        energyStorage.amount -= 20;
        update();
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
        return new CrusherScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }
    public boolean isFuel(ItemStack stack) {
        return createFuelTimeMap().containsKey(stack.getItem());
    }
    public int getFuelTime(ItemStack stack) {
        return createFuelTimeMap().getOrDefault(stack.getItem(), 0);
    }
    public WrappedInventoryStorage<SimpleInventory> getWrappedInventoryStorage() {
        return this.inventoryStorage;
    }
    public EnergyStorage getEnergyProvider(Direction direction) {
        return this.energyStorage.getStorage(direction);
    }
    public boolean isValid(ItemStack itemStack, int slot) {
        return slot == 0 && isFuel(itemStack);
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

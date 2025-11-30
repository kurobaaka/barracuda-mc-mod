package net.infugogr.barracuda.block.entity;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.infugogr.barracuda.Barracuda;
import net.infugogr.barracuda.item.DimensionChip;
import net.infugogr.barracuda.item.PosChip;
import net.infugogr.barracuda.screenhandler.TeleporterScreenHandler;
import net.infugogr.barracuda.util.SyncableStorage;
import net.infugogr.barracuda.util.SyncableTickableBlockEntity;
import net.infugogr.barracuda.util.UpdatableBlockEntity;
import net.infugogr.barracuda.util.energy.EnergySpreader;
import net.infugogr.barracuda.util.energy.SyncingEnergyStorage;
import net.infugogr.barracuda.util.energy.WrappedEnergyStorage;
import net.infugogr.barracuda.util.inventory.SyncingSimpleInventory;
import net.infugogr.barracuda.util.inventory.WrappedInventoryStorage;
import net.minecraft.block.Block;
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
import net.minecraft.registry.RegistryKey;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.base.SimpleEnergyStorage;

import java.util.List;
import java.util.Optional;

public class TeleporterBlockEntity extends UpdatableBlockEntity implements SyncableTickableBlockEntity, EnergySpreader, ExtendedScreenHandlerFactory{
    public static final Text TITLE = Barracuda.containerTitle("teleporter");
    private final WrappedEnergyStorage energyStorage = new WrappedEnergyStorage();
    private final WrappedInventoryStorage<SimpleInventory> inventoryStorage = new WrappedInventoryStorage<>();
    protected final PropertyDelegate propertyDelegate;

    public TeleporterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityType.TELEPORTER, pos, state);

        this.energyStorage.addStorage(new SyncingEnergyStorage(this, 10000000, 10000, 0));
        this.inventoryStorage.addInventory(new SyncingSimpleInventory(this, 6));
        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> (int) TeleporterBlockEntity.this.energyStorage.getStorage(null).getAmount();
                    case 1 -> (int) TeleporterBlockEntity.this.energyStorage.getStorage(null).getCapacity();
                    default -> 0;
                };
            }
            @Override
            public void set(int index, int value) {
            }
            @Override
            public int size() {
                return 2;
            }
        };
    }

    public RegistryKey<World> getTargetDimension() {
        SimpleInventory inventory = this.inventoryStorage.getInventory(0);
        assert inventory != null;
        Optional<ItemStack> dimensionChipOpt = findFirstChip(inventory, DimensionChip.class);
        return dimensionChipOpt.map(stack -> ((DimensionChip) stack.getItem()).getDimension()).orElse(null);
    }

    public BlockPos getTargetPosition() {
        SimpleInventory inventory = this.inventoryStorage.getInventory(0);
        assert inventory != null;
        Optional<ItemStack> posChipOpt = findFirstChip(inventory, PosChip.class);
        return posChipOpt.map(stack -> ((PosChip) stack.getItem()).getPos(stack)).orElse(null);
    }

    private Optional<ItemStack> findFirstChip(SimpleInventory inventory, Class<?> chipClass) {
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (chipClass.isInstance(stack.getItem())) {
                return Optional.of(stack);
            }
        }
        return Optional.empty();
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf packetByteBuf) {
        packetByteBuf.writeBlockPos(this.pos);
    }

    @Override
    public Text getDisplayName() {
        return TITLE;
    }

    @Override
    public List<SyncableStorage> getSyncableStorages() {
        var energy = (SyncingEnergyStorage) this.energyStorage.getStorage(null);
        var inventory = (SyncingSimpleInventory) this.inventoryStorage.getInventory(0);
        assert inventory != null;
        return List.of(energy, inventory);
    }

    @Override
    public void onTick() {
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new TeleporterScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.put("EnergyStorage", this.energyStorage.writeNbt());
        nbt.put("Inventory", this.inventoryStorage.writeNbt());
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.energyStorage.readNbt(nbt.getList("EnergyStorage", NbtElement.COMPOUND_TYPE));
        this.inventoryStorage.readNbt(nbt.getList("Inventory", NbtElement.COMPOUND_TYPE));
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    public boolean isValid(ItemStack itemStack, int slot) {
        return itemStack.getItem() instanceof DimensionChip || itemStack.getItem() instanceof PosChip;
    }

    public EnergyStorage getEnergyProvider(Direction direction) {
        return this.energyStorage.getStorage(direction);
    }

    public InventoryStorage getInventoryProvider(Direction direction) {
        return this.inventoryStorage.getStorage(direction);
    }

    public SimpleEnergyStorage getEnergyStorage() {
        return this.energyStorage.getStorage(null);
    }

    public WrappedInventoryStorage<SimpleInventory> getWrappedInventoryStorage() {
        return this.inventoryStorage;
    }

    public boolean hasPosChip() {
        SimpleInventory inventory = this.inventoryStorage.getInventory(0);
        if (inventory == null) return false;
        return findFirstChip(inventory, PosChip.class).isPresent();
    }

    public void syncWithClient() {
        if (this.world != null && !this.world.isClient) {
            BlockState state = this.world.getBlockState(this.pos);
            this.world.updateListeners(this.pos, state, state, Block.NOTIFY_ALL);
        }
    }
}

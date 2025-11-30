package net.infugogr.barracuda.block.entity;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.infugogr.barracuda.Barracuda;
import net.infugogr.barracuda.screenhandler.SMESScreenHandler;
import net.infugogr.barracuda.util.SyncableStorage;
import net.infugogr.barracuda.util.SyncableTickableBlockEntity;
import net.infugogr.barracuda.util.UpdatableBlockEntity;
import net.infugogr.barracuda.util.energy.EnergySpreader;
import net.infugogr.barracuda.util.energy.SyncingEnergyStorage;
import net.infugogr.barracuda.util.energy.WrappedEnergyStorage;
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
import java.util.Objects;

public class SMESblockEntity extends UpdatableBlockEntity implements SyncableTickableBlockEntity, EnergySpreader, ExtendedScreenHandlerFactory {
    public static final Text TITLE = Barracuda.containerTitle("battery");
    public static final Text CHARGE_MODE_BUTTON_TOOLTIP_TEXT = Text.translatable("gui." + Barracuda.MOD_ID + ".battery.charge_mode_button.tooltip");

    private final WrappedInventoryStorage<SimpleInventory> wrappedInventoryStorage = new WrappedInventoryStorage<>();
    private final WrappedEnergyStorage wrappedEnergyStorage = new WrappedEnergyStorage();
    protected final PropertyDelegate propertyDelegate;
    private ChargeMode chargeMode = ChargeMode.DISCHARGE;

    public SMESblockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityType.SMES, pos, state);

        this.wrappedInventoryStorage.addInventory(new SyncingSimpleInventory(this, 1));
        this.wrappedEnergyStorage.addStorage(new SyncingEnergyStorage(this,5000000, 100, 100));
        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> (int)SMESblockEntity.this.wrappedEnergyStorage.getStorage(null).getAmount();
                    case 1 -> (int)SMESblockEntity.this.wrappedEnergyStorage.getStorage(null).getCapacity();
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

    @Override
    public List<SyncableStorage> getSyncableStorages() {
        var input = (SyncingSimpleInventory) this.wrappedInventoryStorage.getInventory(0);
        var energy = (SyncingEnergyStorage) this.wrappedEnergyStorage.getStorage(null);
        assert input != null;
        return List.of(input, energy);
    }

    @Override
    public void onTick() {
        if (this.world == null || this.world.isClient)
            return;

        SimpleEnergyStorage energyStorage = this.wrappedEnergyStorage.getStorage(null);
        if (energyStorage == null)
            return;

        ItemStack stack = getInventory().getStack(0);
        if (!stack.isEmpty()) {
            var itemEnergyStorage = ContainerItemContext.withConstant(stack).find(EnergyStorage.ITEM);
            if (itemEnergyStorage != null) {
                try (Transaction transaction = Transaction.openOuter()) {
                    if (this.chargeMode == ChargeMode.CHARGE && itemEnergyStorage.supportsInsertion() && itemEnergyStorage.getAmount() < itemEnergyStorage.getCapacity()) {
                        long attemptToInsert = Math.min(Math.min(energyStorage.getAmount(), itemEnergyStorage.getCapacity() - itemEnergyStorage.getAmount()), energyStorage.maxExtract);
                        if (attemptToInsert <= 0)
                            return;

                        long inserted = itemEnergyStorage.insert(attemptToInsert, transaction);
                        if (inserted <= 0)
                            return;

                        energyStorage.amount -= inserted;
                        transaction.commit();

                    } else if (this.chargeMode == ChargeMode.DISCHARGE && itemEnergyStorage.supportsExtraction() && energyStorage.getAmount() < energyStorage.getCapacity()) {
                        long attemptToExtract = Math.min(Math.min(itemEnergyStorage.getAmount(), energyStorage.getCapacity() - energyStorage.getAmount()), energyStorage.maxInsert);
                        if (attemptToExtract <= 0)
                            return;

                        long extracted = itemEnergyStorage.extract(attemptToExtract, transaction);
                        if (extracted <= 0)
                            return;

                        energyStorage.amount += extracted;
                        transaction.commit();

                    }
                }
            }
        }

        spread(this.world, this.pos, energyStorage);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf packetByteBuf) {
        packetByteBuf.writeBlockPos(this.pos);
    }

    @Override
    public Text getDisplayName() {
        return TITLE;
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new SMESScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.chargeMode = ChargeMode.valueOf(nbt.getString("ChargeMode"));
        this.wrappedInventoryStorage.readNbt(nbt.getList("Inventory", NbtElement.COMPOUND_TYPE));
        this.wrappedEnergyStorage.readNbt(nbt.getList("Energy", NbtElement.COMPOUND_TYPE));
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putString("ChargeMode", this.chargeMode.name());
        nbt.put("Inventory", this.wrappedInventoryStorage.writeNbt());
        nbt.put("Energy", this.wrappedEnergyStorage.writeNbt());
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

    public ItemStack getRenderStack() {
        return Objects.requireNonNull(this.getWrappedInventory().getInventory(0)).getStack(0);
    }

    public SimpleEnergyStorage getEnergyProvider(Direction direction) {
        return this.wrappedEnergyStorage.getStorage(direction);
    }

    public InventoryStorage getInventoryProvider(Direction direction) {
        return this.wrappedInventoryStorage.getStorage(direction);
    }

    public WrappedInventoryStorage<SimpleInventory> getWrappedInventory() {
        return this.wrappedInventoryStorage;
    }

    public SimpleEnergyStorage getEnergy() {
        return this.wrappedEnergyStorage.getStorage(null);
    }

    public SimpleInventory getInventory() {
        return this.wrappedInventoryStorage.getInventory(0);
    }

    public boolean isValid(ItemStack stack, int slot) {
        var itemEnergyStorage = ContainerItemContext.withConstant(stack).find(EnergyStorage.ITEM);
        return itemEnergyStorage != null;
    }

    public ChargeMode getChargeMode() {
        return this.chargeMode;
    }

    public void setChargeMode(ChargeMode mode) {
        this.chargeMode = mode;
        System.out.println("Charge mode set to: " + mode);
    }

    public enum ChargeMode {
        DISCHARGE,
        CHARGE;

        public static final Codec<ChargeMode> CODEC = Codec.STRING.xmap(ChargeMode::valueOf, ChargeMode::name);
        public ChargeMode next() {
            return switch (this) {
                case DISCHARGE -> CHARGE;
                case CHARGE -> DISCHARGE;
            };
        }
    }
}
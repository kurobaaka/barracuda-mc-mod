package net.infugogr.barracuda.block.entity;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.infugogr.barracuda.Barracuda;
import net.infugogr.barracuda.recipes.PressRecipe;
import net.infugogr.barracuda.recipes.PressRecipeManager;
import net.infugogr.barracuda.screenhandler.PrimitivePressScreenHandler;
import net.infugogr.barracuda.util.SyncableStorage;
import net.infugogr.barracuda.util.SyncableTickableBlockEntity;
import net.infugogr.barracuda.util.UpdatableBlockEntity;
import net.infugogr.barracuda.util.energy.EnergySpreader;
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

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class PrimitivePressBlockEntity extends UpdatableBlockEntity implements SyncableTickableBlockEntity, EnergySpreader, ExtendedScreenHandlerFactory {
    public static final Text TITLE = Barracuda.containerTitle("primitive_press");
    private final WrappedInventoryStorage<SimpleInventory> inventoryStorage = new WrappedInventoryStorage<>();
    protected final PropertyDelegate propertyDelegate;
    private int progress = 0;
    private boolean pressing = false;

    public PrimitivePressBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityType.PRIMITIVE_PRESS, pos, state);
        this.inventoryStorage.addInventory(new SyncingSimpleInventory(this, 1), Direction.NORTH);
        this.inventoryStorage.addInventory(new OutputSimpleInventory(this, 1), Direction.DOWN);
        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> PrimitivePressBlockEntity.this.progress;
                    default -> 0;
                };
            }
            @Override
            public void set(int index, int value) {
            }
            @Override
            public int size() {
                return 1;
            }
        };
    }
    @Override
    public List<SyncableStorage> getSyncableStorages() {
        var input = (SyncingSimpleInventory) this.inventoryStorage.getInventory(0);
        var output = (SyncingSimpleInventory) this.inventoryStorage.getInventory(1);
        assert input != null;
        assert output != null;
        return List.of(input, output);
    }

    @Override
    public void onTick() {
        if (this.world == null || this.world.isClient) return;
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
            pressing = false;
            return;
        }
        Optional<PressRecipe> recipeOpt = PressRecipeManager.getInstance().getRecipeFor(inputStack);
        if (recipeOpt.isEmpty()) {
            if (this.progress > 0) {
                this.progress = 0;
                markDirty();
            }
            return;
        }

        PressRecipe recipe = recipeOpt.get();
        ItemStack result = recipe.output().copy();

        if (!canInsertIntoOutput(result, outputStack)) {
            return;
        }
        if (pressing) {
            this.progress++;
        }

        int maxProgress = 10;
        if (this.progress >= maxProgress) {
            inputStack.decrement(1);

            if (outputStack.isEmpty()) {
                outputInv.setStack(0, result);
            } else {
                outputStack.increment(result.getCount());
            }

            this.progress = 0;
            pressing = false;
            markDirty();
        }
    }

    private boolean canInsertIntoOutput(ItemStack result, ItemStack outputStack) {
        if (outputStack.isEmpty()) return true;
        if (!outputStack.isOf(result.getItem())) return false;
        return outputStack.getCount() < outputStack.getMaxCount();
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        nbt.put("Inventory", this.inventoryStorage.writeNbt());
        nbt.putBoolean("Pressing", this.pressing);
        nbt.putInt("Progress", this.progress);
    }
    @Override
    public void readNbt(NbtCompound nbt) {
        this.inventoryStorage.readNbt(nbt.getList("Inventory", NbtElement.COMPOUND_TYPE));
        this.pressing = nbt.getBoolean("Pressing");
        this.progress = nbt.getInt("Progress");
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
        return new PrimitivePressScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }
    public WrappedInventoryStorage<SimpleInventory> getWrappedInventoryStorage() {
        return this.inventoryStorage;
    }
    public InventoryStorage getInventoryProvider(Direction direction) {
        return this.inventoryStorage.getStorage(direction);
    }
    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    public void tryStartPressing() {
        var input = Objects.requireNonNull(inventoryStorage.getInventory(0)).getStack(0);
        if (input.isEmpty()) return;
        Optional<PressRecipe> recipe = PressRecipeManager.getInstance().getRecipeFor(input);
        recipe.ifPresent(r -> {
            this.pressing = true;
            markDirty();
        });
    }
}

package net.infugogr.barracuda.block.entity;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.infugogr.barracuda.Barracuda;
import net.infugogr.barracuda.block.recipes.CentrifugeRecipe;
import net.infugogr.barracuda.block.recipes.CentrifugeRecipeManager;
import net.infugogr.barracuda.screenhandler.CentrifugeScreenHandler;
import net.infugogr.barracuda.util.SyncableStorage;
import net.infugogr.barracuda.util.SyncableTickableBlockEntity;
import net.infugogr.barracuda.util.UpdatableBlockEntity;
import net.infugogr.barracuda.util.energy.EnergySpreader;
import net.infugogr.barracuda.util.energy.SyncingEnergyStorage;
import net.infugogr.barracuda.util.energy.WrappedEnergyStorage;
import net.infugogr.barracuda.util.inventory.OutputSimpleInventory;
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
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
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

public class CentrifugeBlockEntity extends UpdatableBlockEntity implements SyncableTickableBlockEntity, EnergySpreader, ExtendedScreenHandlerFactory, GeoBlockEntity {
    public static final Text TITLE = Barracuda.containerTitle("centrifuge");
    private final WrappedEnergyStorage energyStorage = new WrappedEnergyStorage();
    private final WrappedInventoryStorage<SimpleInventory> inventoryStorage = new WrappedInventoryStorage<>();
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    protected final PropertyDelegate propertyDelegate;
    private int progress = 0;
    private int maxProgress = 0;

    public CentrifugeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityType.CENTRIFUGE, pos, state);
        this.energyStorage.addStorage(new SyncingEnergyStorage(this, 5000, 100, 0));
        this.inventoryStorage.addInventory(new SyncingSimpleInventory(this, 3));
        this.inventoryStorage.addInventory(new SyncingSimpleInventory(this, 1), Direction.UP);
        this.inventoryStorage.addInventory(new OutputSimpleInventory(this, 9), Direction.DOWN);
        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> CentrifugeBlockEntity.this.progress;
                    case 1 -> CentrifugeBlockEntity.this.maxProgress;
                    case 2 -> (int) CentrifugeBlockEntity.this.energyStorage.getStorage(null).getAmount();
                    case 3 -> (int) CentrifugeBlockEntity.this.energyStorage.getStorage(null).getCapacity();
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
        var upgrade_slots = (SyncingSimpleInventory) this.inventoryStorage.getInventory(0);
        var input_slot = (SyncingSimpleInventory) this.inventoryStorage.getInventory(1);
        var output_slots = (SyncingSimpleInventory) this.inventoryStorage.getInventory(2);
        assert upgrade_slots != null;
        assert input_slot != null;
        assert output_slots != null;
        return List.of(energy, upgrade_slots, input_slot, output_slots);
    }

    @Override
    public void onTick() {
        if (world == null || world.isClient) return;

        SimpleInventory inputInv = inventoryStorage.getInventory(1);
        SimpleInventory outputInv = inventoryStorage.getInventory(2);
        assert inputInv != null;
        assert outputInv != null;

        ItemStack input = inputInv.getStack(0);
        Optional<CentrifugeRecipe> recipeOpt = CentrifugeRecipeManager.getInstance().getRecipeFor(input);

        if (recipeOpt.isEmpty()) {
            progress = 0;
            maxProgress = 0;
            return;
        }

        CentrifugeRecipe recipe = recipeOpt.get();

        // если только начали — установи время из JSON
        if (maxProgress == 0) {
            maxProgress = recipe.craftTime();
        }

        // нужно Х энергии за тик (например, 20 FE)
        final long energyPerTick = 20;
        SimpleEnergyStorage storage = energyStorage.getStorage(null);

        if (storage.getAmount() < energyPerTick) {
            progress = 0;
            markDirty();
            sync();
            return;
        }

        // забираем энергию
        storage.amount -= 20;

        // увеличиваем прогресс
        progress++;

        if (progress >= maxProgress) {
            // забираем вход
            input.decrement(1);

            // выдаём выходы
            for (ItemStack stack : recipe.outputs()) {
                tryInsertStack(outputInv, stack.copy());
            }

            // сбрасываем прогресс
            progress = 0;
            maxProgress = 0;
        }

        markDirty();
    }

    private void tryInsertStack(SimpleInventory inv, ItemStack stack) {
        for (int i = 0; i < inv.size(); i++) {
            ItemStack existing = inv.getStack(i);
            if (existing.isEmpty()) {
                inv.setStack(i, stack);
                return;
            } else if (ItemStack.canCombine(existing, stack)) {
                int space = existing.getMaxCount() - existing.getCount();
                int move = Math.min(space, stack.getCount());
                existing.increment(move);
                stack.decrement(move);
                if (stack.isEmpty()) return;
            }
        }
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
        return new CentrifugeScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
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

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> state) {
        if (progress > 0) {
            state.getController().setAnimation(
                    RawAnimation.begin().then("centrifuge", Animation.LoopType.LOOP)
            );
        } else {
            state.getController().setAnimation(
                    RawAnimation.begin().then("idle", Animation.LoopType.LOOP)
            );
        }
        return PlayState.CONTINUE;
    }

    public void sync() {
        if (world != null && !world.isClient) {
            world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
            markDirty();
        }
    }


    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}

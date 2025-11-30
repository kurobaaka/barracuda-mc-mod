package net.infugogr.barracuda.block.entity;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.infugogr.barracuda.Barracuda;
import net.infugogr.barracuda.block.recipes.CircuitImprinterRecipes;
import net.infugogr.barracuda.screenhandler.CircuitImprinterScreenHandler;
import net.infugogr.barracuda.util.SyncableStorage;
import net.infugogr.barracuda.util.SyncableTickableBlockEntity;
import net.infugogr.barracuda.util.UpdatableBlockEntity;
import net.infugogr.barracuda.util.energy.EnergySpreader;
import net.infugogr.barracuda.util.energy.SyncingEnergyStorage;
import net.infugogr.barracuda.util.energy.WrappedEnergyStorage;
import net.infugogr.barracuda.util.inventory.SyncingSimpleInventory;
import net.infugogr.barracuda.util.inventory.WrappedInventoryStorage;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
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

public class CircuitImprinterBlockEntity extends UpdatableBlockEntity implements SyncableTickableBlockEntity, EnergySpreader, ExtendedScreenHandlerFactory, GeoBlockEntity {
    public static final Text TITLE = Barracuda.containerTitle("circuit_imprinter");
    private final WrappedEnergyStorage energyStorage = new WrappedEnergyStorage();
    private final WrappedInventoryStorage<SimpleInventory> inventoryStorage = new WrappedInventoryStorage<>();
    protected final PropertyDelegate propertyDelegate;
    private int progress = 0;
    private int maxProgress = 100;
    private int currentRecipe = 99;
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    public CircuitImprinterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityType.CIRCUIT_IMPRINTER, pos, state);
        this.inventoryStorage.addInventory(new SyncingSimpleInventory(this, 4));
        this.energyStorage.addStorage(new SyncingEnergyStorage(this, 5000, 100, 0));
        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> CircuitImprinterBlockEntity.this.progress;
                    case 1 -> CircuitImprinterBlockEntity.this.maxProgress;
                    case 2 -> (int)CircuitImprinterBlockEntity.this.energyStorage.getStorage(null).getAmount();
                    case 3 -> (int)CircuitImprinterBlockEntity.this.energyStorage.getStorage(null).getCapacity();
                    case 4 -> CircuitImprinterBlockEntity.this.currentRecipe;
                    default -> 0;
                };
            }
            @Override
            public void set(int index, int value) {
            }
            @Override
            public int size() {
                return 5;
            }
        };
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
        if (world == null || world.isClient) return;

        SimpleEnergyStorage energyStorage = this.energyStorage.getStorage(null);

        spread(this.world, this.pos, energyStorage);

        if (energyStorage.getAmount() < 20)
            return;

        if (currentRecipe == 99) {
            progress = 0;
            return;
        }

        // Проверяем наличие ресурсов и энергии (если нужно)
        if (!hasRequiredInputs(currentRecipe)) {
            progress = 0;
            return;
        }

        progress++;
        energyStorage.amount -=20;
        propertyDelegate.set(0, progress);
        propertyDelegate.set(1, maxProgress);

        if (progress >= maxProgress) {
            ItemStack output = CircuitImprinterRecipes.getRecipePlate(currentRecipe);
            dropStack(output);
            consumeInputs(currentRecipe);
            progress = 0;
            currentRecipe = 99;
            markDirty();
        }
    }


    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new CircuitImprinterScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }

    private void dropStack(ItemStack stack) {
        assert world != null;
        ItemEntity itemEntity = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, stack);
        world.spawnEntity(itemEntity);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf packetByteBuf) {
        packetByteBuf.writeBlockPos(this.pos);
    }
    @Override
    public void writeNbt(NbtCompound nbt) {
        nbt.put("EnergyStorage", this.energyStorage.writeNbt());
        nbt.put("Inventory", this.inventoryStorage.writeNbt());
        nbt.putInt("Progress", this.progress);
        nbt.putInt("MaxProgress", this.maxProgress);
    }
    @Override
    public void readNbt(NbtCompound nbt) {
        this.energyStorage.readNbt(nbt.getList("EnergyStorage", NbtElement.COMPOUND_TYPE));
        this.inventoryStorage.readNbt(nbt.getList("Inventory", NbtElement.COMPOUND_TYPE));
        this.progress = nbt.getInt("Progress");
        this.maxProgress = nbt.getInt("MaxProgress");
    }
    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }
    @Override
    public Text getDisplayName() {
        return TITLE;
    }
    public WrappedInventoryStorage<SimpleInventory> getWrappedInventoryStorage() {
        return this.inventoryStorage;
    }
    public EnergyStorage getEnergyProvider(Direction direction) {
        return this.energyStorage.getStorage(direction);
    }
    public boolean isValid(ItemStack itemStack, int slot) {
        return slot == 0 && itemStack.getItem() == Items.GOLD_INGOT;
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

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> tAnimationState) {
        tAnimationState.getController().setAnimation(RawAnimation.begin().then("print", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    public void setCurrentRecipe(int i) {
        this.currentRecipe = i;
        this.progress = 0;
        markDirty();
    }

    private boolean hasRequiredInputs(int i) {

        SimpleInventory storage = this.inventoryStorage.getInventory(0);
        for (int l = 0; l < 4; ++l) {
            assert storage != null;
            if (storage.getStack(l).getCount() < CircuitImprinterRecipes.getRecipeMaterials(i,l)) {
                return false;
            }
        }
        return true;
    }

    private void consumeInputs(int i) {
        SimpleInventory storage = this.inventoryStorage.getInventory(0);
        for (int l = 0; l < 4; ++l) {
            assert storage != null;
            storage.getStack(l).decrement(CircuitImprinterRecipes.getRecipeMaterials(i,l));
        }
    }
}

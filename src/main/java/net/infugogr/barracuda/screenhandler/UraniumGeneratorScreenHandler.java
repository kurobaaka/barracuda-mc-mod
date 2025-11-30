package net.infugogr.barracuda.screenhandler;

import net.infugogr.barracuda.block.ModBlocks;
import net.infugogr.barracuda.block.entity.FuelGeneratorBlockEntity;
import net.infugogr.barracuda.block.entity.UraniumGeneratorBlockEntity;
import net.infugogr.barracuda.screenhandler.slot.OutputSlot;
import net.infugogr.barracuda.screenhandler.slot.PredicateSlot;
import net.infugogr.barracuda.util.inventory.WrappedInventoryStorage;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;

import java.util.Arrays;
import java.util.Objects;

public class UraniumGeneratorScreenHandler extends ScreenHandler {
    private final PropertyDelegate propertyDelegate;
    private final UraniumGeneratorBlockEntity blockEntity;
    private final ScreenHandlerContext context;

    public UraniumGeneratorScreenHandler(int syncId, PlayerInventory inventory, PacketByteBuf buf) {
        this(syncId, inventory, Objects.requireNonNull(inventory.player.getWorld().getBlockEntity(buf.readBlockPos())),
                new ArrayPropertyDelegate(4));
    }

    public UraniumGeneratorScreenHandler(int syncId, PlayerInventory playerInventory,
                                         BlockEntity blockEntity, PropertyDelegate arrayPropertyDelegate) {
        super(ModScreenHandlerType.URANIUM_GENERATOR, syncId);

        this.propertyDelegate = arrayPropertyDelegate;
        this.blockEntity = ((UraniumGeneratorBlockEntity) blockEntity);
        this.context = ScreenHandlerContext.create(blockEntity.getWorld(), blockEntity.getPos());

        WrappedInventoryStorage<SimpleInventory> inventory = this.blockEntity.getWrappedInventoryStorage();
        inventory.onOpen(playerInventory.player);
        inventory.checkSize(5);
        addBlockEntityInventory();
        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
        addProperties(arrayPropertyDelegate);
    }

    private void addBlockEntityInventory() {
        addSlot(new PredicateSlot( this.blockEntity.getWrappedInventoryStorage().getInventory(0), 0, 36, 32,
                itemStack -> this.blockEntity.isValid(itemStack, 0)));
        addSlot(new OutputSlot( this.blockEntity.getWrappedInventoryStorage().getInventory(0), 1, 36, 50));
        addSlot(new PredicateSlot( this.blockEntity.getWrappedInventoryStorage().getInventory(0), 2, 8, 14));
        addSlot(new PredicateSlot( this.blockEntity.getWrappedInventoryStorage().getInventory(0), 3, 8, 32));
        addSlot(new PredicateSlot( this.blockEntity.getWrappedInventoryStorage().getInventory(0), 4, 8, 50));
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        ItemStack result;
        Slot slot = this.slots.get(index);
        if (!slot.hasStack()) return ItemStack.EMPTY;
        ItemStack stackInSlot = slot.getStack();
        result = stackInSlot.copy();
        final int FUEL_SLOT = 0;
        final int BUCKET_SLOT = 1;
        final int WORK_START = 2;
        final int WORK_END = 5;

        if (index < WORK_END) {
            if (!this.insertItem(stackInSlot, WORK_END, this.slots.size(), true)) {
                return ItemStack.EMPTY;
            }
            slot.onTakeItem(player, stackInSlot);
        } else {
            if (isFuel(stackInSlot)) {
                if (!this.insertItem(stackInSlot, FUEL_SLOT, FUEL_SLOT + 1, false)) {
                    if (!this.insertItem(stackInSlot, WORK_START, WORK_END, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            }
            else if (isEmptyBucket(stackInSlot)) {
                if (!this.insertItem(stackInSlot, BUCKET_SLOT, BUCKET_SLOT + 1, false)) {
                    if (!this.insertItem(stackInSlot, WORK_START, WORK_END, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            }
            else if (!this.insertItem(stackInSlot, WORK_START, WORK_END, false)) {
                return ItemStack.EMPTY;
            }
        }
        if (stackInSlot.isEmpty()) {
            slot.setStack(ItemStack.EMPTY);
        } else {
            slot.markDirty();
        }
        if (ItemStack.areEqual(result, stackInSlot)) {
            return ItemStack.EMPTY;
        }
        slot.onTakeItem(player, stackInSlot);
        return result;
    }

    private boolean isFuel(ItemStack stack) {
        if (stack.isEmpty()) return false;
        return net.fabricmc.fabric.api.registry.FuelRegistry.INSTANCE.get(stack.getItem()) != null;
    }

    private boolean isEmptyBucket(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() == net.minecraft.item.Items.BUCKET;
    }

    private void addPlayerInventory(PlayerInventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(PlayerInventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return canUse(player, ModBlocks.URANIUM_GENERATOR);
    }

    private boolean canUse(PlayerEntity player, Block... blocks) {
        return Arrays.stream(blocks).anyMatch(block -> canUse(this.context, player, block));
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.blockEntity.getWrappedInventoryStorage().onClose(player);
    }

    int getEnergyPerTick(){return this.propertyDelegate.get(1);}

    int getProgress(){return this.propertyDelegate.get(0);}

    int getMaxProgress(){return this.propertyDelegate.get(1);}

    int getEnergy(){return this.propertyDelegate.get(2);}

    int getMaxEnergy(){return this.propertyDelegate.get(3);}
}

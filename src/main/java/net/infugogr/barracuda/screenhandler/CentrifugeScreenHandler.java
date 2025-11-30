package net.infugogr.barracuda.screenhandler;

import net.infugogr.barracuda.block.ModBlocks;
import net.infugogr.barracuda.block.entity.CentrifugeBlockEntity;
import net.infugogr.barracuda.block.entity.ElectricSmelterBlockEntity;
import net.infugogr.barracuda.util.inventory.WrappedInventoryStorage;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
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

public class CentrifugeScreenHandler extends ScreenHandler {
    private final PropertyDelegate propertyDelegate;
    private final CentrifugeBlockEntity blockEntity;
    private final ScreenHandlerContext context;

    public CentrifugeScreenHandler(int syncId, PlayerInventory inventory, PacketByteBuf buf) {
        this(syncId, inventory, Objects.requireNonNull(inventory.player.getWorld().getBlockEntity(buf.readBlockPos())),
                new ArrayPropertyDelegate(4));
    }

    public CentrifugeScreenHandler(int syncId, PlayerInventory playerInventory,
                                   BlockEntity blockEntity, PropertyDelegate arrayPropertyDelegate) {
        super(ModScreenHandlerType.CENTRIFUGE, syncId);

        this.propertyDelegate = arrayPropertyDelegate;
        this.blockEntity = ((CentrifugeBlockEntity) blockEntity);
        this.context = ScreenHandlerContext.create(blockEntity.getWorld(), blockEntity.getPos());

        WrappedInventoryStorage<SimpleInventory> inventory = this.blockEntity.getWrappedInventoryStorage();
        inventory.onOpen(playerInventory.player);
        inventory.checkSize(13);
        addBlockEntityInventory();
        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
        addProperties(arrayPropertyDelegate);
    }

    private void addBlockEntityInventory() {
        Inventory inventoryUpgrades = this.blockEntity.getWrappedInventoryStorage().getInventory(0);
        Inventory inventoryInput = this.blockEntity.getWrappedInventoryStorage().getInventory(1);
        Inventory inventoryOutput = this.blockEntity.getWrappedInventoryStorage().getInventory(2);
        addSlot(new Slot(inventoryUpgrades, 0, 8, 16));
        addSlot(new Slot(inventoryUpgrades, 1, 8, 34));
        addSlot(new Slot(inventoryUpgrades, 2, 8, 52));
        addSlot(new Slot(inventoryInput, 0, 43, 34));
        addSlot(new Slot(inventoryOutput, 0, 98, 16));
        addSlot(new Slot(inventoryOutput, 1, 116, 16));
        addSlot(new Slot(inventoryOutput, 2, 134, 16));
        addSlot(new Slot(inventoryOutput, 3, 98, 34));
        addSlot(new Slot(inventoryOutput, 4, 116, 34));
        addSlot(new Slot(inventoryOutput, 5, 134, 34));
        addSlot(new Slot(inventoryOutput, 6, 98, 52));
        addSlot(new Slot(inventoryOutput, 7, 116, 52));
        addSlot(new Slot(inventoryOutput, 8, 134, 52));
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slotObject = this.slots.get(slot);

        if (slotObject.hasStack()) {
            ItemStack stackInSlot = slotObject.getStack();
            stack = stackInSlot.copy();

            if (slot < 0) {
                if (!insertItem(stackInSlot, 0, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!insertItem(stackInSlot, 0, 0, false)) {
                return ItemStack.EMPTY;
            }

            if (stackInSlot.isEmpty()) {
                slotObject.setStack(ItemStack.EMPTY);
            } else {
                slotObject.markDirty();
            }
        }

        return stack;
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
        return canUse(player, ModBlocks.CENTRIFUGE);
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

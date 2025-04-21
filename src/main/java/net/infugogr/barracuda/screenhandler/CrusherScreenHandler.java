package net.infugogr.barracuda.screenhandler;

import net.infugogr.barracuda.block.ModBlocks;
import net.infugogr.barracuda.block.entity.CrusherBlockEntity;
import net.infugogr.barracuda.block.entity.FuelGeneratorBlockEntity;
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

public class CrusherScreenHandler extends ScreenHandler {
    private final PropertyDelegate propertyDelegate;
    private final CrusherBlockEntity blockEntity;
    private final ScreenHandlerContext context;

    public CrusherScreenHandler(int syncId, PlayerInventory inventory, PacketByteBuf buf) {
        this(syncId, inventory, Objects.requireNonNull(inventory.player.getWorld().getBlockEntity(buf.readBlockPos())),
                new ArrayPropertyDelegate(4));
    }

    public CrusherScreenHandler(int syncId, PlayerInventory playerInventory,
                                BlockEntity blockEntity, PropertyDelegate arrayPropertyDelegate) {
        super(ModScreenHandlerType.CRUSHER, syncId);

        this.propertyDelegate = arrayPropertyDelegate;
        this.blockEntity = ((CrusherBlockEntity) blockEntity);
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
        WrappedInventoryStorage<?> inventory = this.blockEntity.getWrappedInventoryStorage();
        addSlot(new Slot(inventory.getInventory(0), 0, 8, 14));
        addSlot(new Slot(inventory.getInventory(1), 0, 8, 32));
        addSlot(new Slot(inventory.getInventory(2), 0, 8, 50));
        addSlot(new Slot(inventory.getInventory(3), 0, 53, 32));
        addSlot(new Slot(inventory.getInventory(4), 0, 109, 32));
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
        return canUse(player, ModBlocks.CRUSHER);
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

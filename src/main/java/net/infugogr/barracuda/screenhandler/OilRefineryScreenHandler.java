package net.infugogr.barracuda.screenhandler;

import net.infugogr.barracuda.block.ModBlocks;
import net.infugogr.barracuda.block.entity.OilRefineryBlockEntity;
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

public class OilRefineryScreenHandler extends ScreenHandler {
    private final PropertyDelegate propertyDelegate;
    private final OilRefineryBlockEntity blockEntity;
    private final ScreenHandlerContext context;

    public OilRefineryScreenHandler(int syncId, PlayerInventory inventory, PacketByteBuf buf) {
        this(syncId, inventory, Objects.requireNonNull(inventory.player.getWorld().getBlockEntity(buf.readBlockPos())),
                new ArrayPropertyDelegate(10));
    }

    public OilRefineryScreenHandler(int syncId, PlayerInventory playerInventory,
                                    BlockEntity blockEntity, PropertyDelegate arrayPropertyDelegate) {
        super(ModScreenHandlerType.OIL_REFINERY, syncId);

        this.propertyDelegate = arrayPropertyDelegate;
        this.blockEntity = ((OilRefineryBlockEntity) blockEntity);
        this.context = ScreenHandlerContext.create(blockEntity.getWorld(), blockEntity.getPos());

        WrappedInventoryStorage<SimpleInventory> inventory = this.blockEntity.getWrappedInventoryStorage();
        inventory.onOpen(playerInventory.player);
        inventory.checkSize(3);
        addBlockEntityInventory();
        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
        addProperties(arrayPropertyDelegate);
    }

    private void addBlockEntityInventory() {
        WrappedInventoryStorage<?> inventory = this.blockEntity.getWrappedInventoryStorage();
        addSlot(new Slot(inventory.getInventory(0), 0, 8, 17));
        addSlot(new Slot(inventory.getInventory(1), 0, 8, 35));
        addSlot(new Slot(inventory.getInventory(2), 0, 8, 53));
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
        return canUse(player, ModBlocks.OIL_REFINERY);
    }

    private boolean canUse(PlayerEntity player, Block... blocks) {
        return Arrays.stream(blocks).anyMatch(block -> canUse(this.context, player, block));
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.blockEntity.getWrappedInventoryStorage().onClose(player);
    }

    int getProgress(){return this.propertyDelegate.get(0);}

    int getMaxProgress(){return this.propertyDelegate.get(1);}

    int getEnergy(){return this.propertyDelegate.get(2);}

    int getMaxEnergy(){return this.propertyDelegate.get(3);}

    int getCapacity(){return this.propertyDelegate.get(4);}

    int getOil(){return this.propertyDelegate.get(5);}

    int getHeavyOil(){return this.propertyDelegate.get(6);}

    int getDiesel(){return this.propertyDelegate.get(7);}

    int getGas(){return this.propertyDelegate.get(8);}

    int getWater(){return this.propertyDelegate.get(9);}
}

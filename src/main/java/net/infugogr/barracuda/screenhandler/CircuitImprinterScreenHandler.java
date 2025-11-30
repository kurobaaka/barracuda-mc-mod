package net.infugogr.barracuda.screenhandler;

import net.infugogr.barracuda.Barracuda;
import net.infugogr.barracuda.block.ModBlocks;
import net.infugogr.barracuda.block.entity.CircuitImprinterBlockEntity;
import net.infugogr.barracuda.item.ModItems;
import net.infugogr.barracuda.screenhandler.slot.FilterSlot;
import net.infugogr.barracuda.util.inventory.WrappedInventoryStorage;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

import java.util.Arrays;
import java.util.Objects;

public class CircuitImprinterScreenHandler extends ScreenHandler {
    private final PropertyDelegate propertyDelegate;
    private final CircuitImprinterBlockEntity blockEntity;
    private final ScreenHandlerContext context;

    public CircuitImprinterScreenHandler(int syncId, PlayerInventory inventory, PacketByteBuf buf) {
        this(syncId, inventory, Objects.requireNonNull(inventory.player.getWorld().getBlockEntity(buf.readBlockPos())),
                new ArrayPropertyDelegate(5));
    }

    public CircuitImprinterScreenHandler(int syncId, PlayerInventory playerInventory,
                                         BlockEntity blockEntity, PropertyDelegate arrayPropertyDelegate) {
        super(ModScreenHandlerType.CIRCUIT_IMPRINTER, syncId);

        this.propertyDelegate = arrayPropertyDelegate;
        this.blockEntity = ((CircuitImprinterBlockEntity) blockEntity);
        this.context = ScreenHandlerContext.create(blockEntity.getWorld(), blockEntity.getPos());

        WrappedInventoryStorage<SimpleInventory> inventory = this.blockEntity.getWrappedInventoryStorage();
        inventory.onOpen(playerInventory.player);
        inventory.checkSize(4);
        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
        addProperties(arrayPropertyDelegate);
        addBlockEntityInventory();
    }

    private void addBlockEntityInventory() {
        WrappedInventoryStorage<?> inventory = this.blockEntity.getWrappedInventoryStorage();

        addSlot(new FilterSlot(inventory.getInventory(0), 0, 8, 15, stack -> stack.getItem() == ModItems.IRON_PLATE));
        addSlot(new FilterSlot(inventory.getInventory(0), 1, 8, 33, stack -> stack.getItem() == Items.GLASS));
        addSlot(new FilterSlot(inventory.getInventory(0), 2, 8, 51, stack -> stack.getItem() == Items.GOLD_INGOT));
        addSlot(new FilterSlot(inventory.getInventory(0), 3, 8, 69, stack -> stack.getItem() == ModItems.REDSTONEIUM_INGOT));
    }

    private void addPlayerInventory(PlayerInventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 102 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(PlayerInventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 160));
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return canUse(player, ModBlocks.CIRCUIT_IMPRINTER);
    }

    private boolean canUse(PlayerEntity player, Block... blocks) {
        return Arrays.stream(blocks).anyMatch(block -> canUse(this.context, player, block));
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        ItemStack originalStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasStack()) {
            ItemStack stackInSlot = slot.getStack();
            originalStack = stackInSlot.copy();

            int slotCount = this.slots.size();

            if (index < 4) {
                if (!this.insertItem(stackInSlot, 4, slotCount, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.insertItem(stackInSlot, 0, 4, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (stackInSlot.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }

        return originalStack;
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.blockEntity.getWrappedInventoryStorage().onClose(player);
    }

    @Override
    public boolean onButtonClick(PlayerEntity player, int id) {
        assert this.blockEntity.getWorld() != null;
        if (getCurrentRecipe() == 99) {
            setCurrentRecipe(id);
            this.blockEntity.getWorld().playSound(null, this.blockEntity.getPos(), SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.BLOCKS, 1.0F, this.blockEntity.getWorld().random.nextFloat() * 0.1F + 0.9F);
            return true;
        } else {
            return false;
        }
    }

    int getProgress(){return this.propertyDelegate.get(0);}

    int getMaxProgress(){return this.propertyDelegate.get(1);}

    int getEnergy(){return this.propertyDelegate.get(2);}

    int getMaxEnergy(){return this.propertyDelegate.get(3);}

    int getCurrentRecipe(){return this.propertyDelegate.get(4);}

    void setCurrentRecipe(int currentRecipe){blockEntity.setCurrentRecipe(currentRecipe);}
}

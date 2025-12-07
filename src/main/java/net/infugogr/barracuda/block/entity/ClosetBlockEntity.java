package net.infugogr.barracuda.block.entity;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.infugogr.barracuda.Barracuda;
import net.infugogr.barracuda.block.OilRefineryBlock;
import net.infugogr.barracuda.screenhandler.ClosetScreenHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import org.jetbrains.annotations.Nullable;

public class ClosetBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory {
    public static final Text TITLE = Text.translatable("container." + Barracuda.MOD_ID + ".closet");
    private final SimpleInventory inventory = new SimpleInventory(54) {
        @Override
        public void markDirty() {
            super.markDirty();
            update();
        }

        @Override
        public void onOpen(PlayerEntity player) {
            super.onOpen(player);
            ClosetBlockEntity.this.numPlayersOpen++;
            update();
        }

        @Override
        public void onClose(PlayerEntity player) {
            super.onClose(player);
            ClosetBlockEntity.this.numPlayersOpen--;
            update();
        }
    };

    private final InventoryStorage inventoryStorage = InventoryStorage.of(inventory, null);

    private int numPlayersOpen;
    public float lidAngle;

    public ClosetBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityType.CLOSET, pos, state);
    }

    @Override
    public Text getDisplayName() {
        return TITLE;
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new ClosetScreenHandler(syncId, playerInventory, this);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, this.inventory.getHeldStacks());

        if(nbt.contains("NumPlayersOpen", NbtElement.INT_TYPE)) {
            this.numPlayersOpen = nbt.getInt("NumPlayersOpen");
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        Inventories.writeNbt(nbt, this.inventory.getHeldStacks());
        super.writeNbt(nbt);
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        var nbt = super.toInitialChunkDataNbt();
        writeNbt(nbt);
        nbt.putInt("NumPlayersOpen", this.numPlayersOpen);
        return nbt;
    }

    private void update() {
        markDirty();
        if(world != null)
            world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
    }

    public InventoryStorage getInventoryProvider(Direction direction) {
        return inventoryStorage;
    }

    public SimpleInventory getInventory() {
        return this.inventory;
    }

    public int getNumPlayersOpen() {
        return this.numPlayersOpen;
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf packetByteBuf) {
        packetByteBuf.writeBlockPos(this.pos);
    }
}
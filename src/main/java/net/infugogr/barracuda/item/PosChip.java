package net.infugogr.barracuda.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static net.minecraft.block.Block.dropStack;

public class PosChip extends Item {
    public PosChip(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        if (!world.isClient() && context.getStack().getNbt() == null) {
            PlayerEntity player = context.getPlayer();
            BlockPos pos = context.getBlockPos();
            ItemStack stack = context.getStack();

            // Сохраняем позицию в NBT
            NbtCompound nbt = stack.getOrCreateNbt();
            nbt.putInt("posX", pos.getX());
            nbt.putInt("posY", pos.getY());
            nbt.putInt("posZ", pos.getZ());

            if (player != null) {
                player.sendMessage(Text.literal("Позиция сохранена: " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ()).formatted(Formatting.GREEN));
            }
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);

        NbtCompound nbt = stack.getNbt();
        if (nbt != null && nbt.contains("posX")) {
            int x = nbt.getInt("posX");
            int y = nbt.getInt("posY");
            int z = nbt.getInt("posZ");
            tooltip.add(Text.literal("Позиция: " + x + ", " + y + ", " + z).formatted(Formatting.GRAY));
        } else {
            tooltip.add(Text.literal("Пусто").formatted(Formatting.GRAY));
        }
    }

    public BlockPos getPos(ItemStack stack) {
        NbtCompound nbt = stack.getNbt();
        if (nbt != null && nbt.contains("posX")) {
            return new BlockPos(nbt.getInt("posX"), nbt.getInt("posY"), nbt.getInt("posZ"));
        }
        return null;
    }
}

package net.infugogr.barracuda.block.recipes;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

import java.util.ArrayList;
import java.util.List;

public class MachineFrameRecipes {
    private static final List<MachineFrameRecipe> RECIPES = new ArrayList<>();

    static {
        RECIPES.add(new MachineFrameRecipe(
                net.infugogr.barracuda.item.ModItems.FUEL_GENERATOR_PLATE,
                net.infugogr.barracuda.block.ModBlocks.LVCABLE.asItem().getDefaultStack().copyWithCount(10),
                net.infugogr.barracuda.item.ModItems.CAPACITOR.getDefaultStack().copyWithCount(1),
                net.infugogr.barracuda.block.ModBlocks.FUEL_GENERATOR
        ));
        RECIPES.add(new MachineFrameRecipe(
                net.infugogr.barracuda.item.ModItems.SMES_PLATE,
                net.infugogr.barracuda.block.ModBlocks.HVCABLE.asItem().getDefaultStack().copyWithCount(10),
                net.infugogr.barracuda.item.ModItems.CAPACITOR.getDefaultStack().copyWithCount(1),
                net.infugogr.barracuda.block.ModBlocks.SMES
        ));
    }

    public static Block getOutput(DefaultedList<ItemStack> inventory) {
        for (MachineFrameRecipe recipe : RECIPES) {
            if (recipe.matches(inventory)) {
                return recipe.getResult();
            }
        }
        return net.minecraft.block.Blocks.AIR;
    }
}

class MachineFrameRecipe {
    private final Item plate;
    private final ItemStack cables;
    private final ItemStack capacitor;
    private final Block result;

    public MachineFrameRecipe(Item plate, ItemStack cables, ItemStack capacitor, Block result) {
        this.plate = plate;
        this.cables = cables;
        this.capacitor = capacitor;
        this.result = result;
    }

    public boolean matches(DefaultedList<ItemStack> inv) {
        return plate == inv.get(0).getItem()
                && ItemStack.areItemsEqual(cables, inv.get(1))
                && ItemStack.areItemsEqual(capacitor, inv.get(2));
    }

    public Block getResult() {
        return result;
    }
}

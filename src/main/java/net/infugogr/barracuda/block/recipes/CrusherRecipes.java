package net.infugogr.barracuda.block.recipes;

import net.infugogr.barracuda.block.ModBlocks;
import net.infugogr.barracuda.item.ModItems;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

public class CrusherRecipes {
    public static Item getCrusherOutput(Item input) {
        if (input == ModBlocks.URANIUM_ORE.asItem() || input == ModBlocks.DEEPSLATE_URANIUM_ORE.asItem()) {
            return ModItems.URANIUM_DUST;
        }
        return Items.AIR;
    }
}

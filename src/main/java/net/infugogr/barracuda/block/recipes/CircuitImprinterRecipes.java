package net.infugogr.barracuda.block.recipes;

import net.infugogr.barracuda.Barracuda;
import net.infugogr.barracuda.block.ModBlocks;
import net.infugogr.barracuda.block.entity.CircuitImprinterBlockEntity;
import net.infugogr.barracuda.item.ModItems;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class CircuitImprinterRecipes {
    private static final List<CircuitImprinterRecipe> RECIPES = new ArrayList<>();
    public static final Text STEEL = Barracuda.containerTitle("steel");
    public static final Text GLASS = Barracuda.containerTitle("glass");
    public static final Text GOLD = Barracuda.containerTitle("gold");
    public static final Text REDSTONEIUM = Barracuda.containerTitle("redstoneium");

    static {
        RECIPES.add(new CircuitImprinterRecipe(
                ModBlocks.FUEL_GENERATOR,
                ModItems.FUEL_GENERATOR_PLATE,
                1,
                1,
                1,
                1
        ));
        RECIPES.add(new CircuitImprinterRecipe(
                ModBlocks.SMES,
                ModItems.SMES_PLATE,
                8,
                5,
                10,
                4
        ));
    }

    public static ItemStack getRecipeOutput(int index) {
        return RECIPES.get(index).getIcon().asItem().getDefaultStack();
    }

    public static ItemStack getRecipePlate(int index) {
        return RECIPES.get(index).getResult().getDefaultStack();
    }

    public static int getRecipeMaterials(int RecipeIndex, int MaterialIndex) {
        return switch (MaterialIndex) {
            case 0 -> CircuitImprinterRecipes.RECIPES.get(RecipeIndex).getSteel();
            case 1 -> CircuitImprinterRecipes.RECIPES.get(RecipeIndex).getGlass();
            case 2 -> CircuitImprinterRecipes.RECIPES.get(RecipeIndex).getGold();
            case 3 -> CircuitImprinterRecipes.RECIPES.get(RecipeIndex).getRedstoneium();
            default -> 0;
        };
    }

    public static List<Text> getRecipeTooltip(int index) {
        return List.of(
                Text.of((Text.translatable( "item.barracuda."+RECIPES.get(index).getResult().toString()))),
                Text.of(STEEL.getString() + ": " + RECIPES.get(index).getSteel()),
                Text.of(GLASS.getString() + ": " + RECIPES.get(index).getGlass()),
                Text.of(GOLD.getString() + ": " + RECIPES.get(index).getGold()),
                Text.of(REDSTONEIUM.getString() + ": " + RECIPES.get(index).getRedstoneium()));
    }

    public static int getCountRecipe() {
        return RECIPES.size();
    }
}

class CircuitImprinterRecipe {
    private final Block icon;
    private final Item result;
    private final int steel;
    private final int glass;
    private final int gold;
    private final int redstoneium;

    public CircuitImprinterRecipe(Block icon, Item result, int steel, int glass, int gold, int redstoneium) {
        this.icon = icon;
        this.result = result;
        this.steel = steel;
        this.glass = glass;
        this.gold = gold;
        this.redstoneium = redstoneium;
    }
    public Block getIcon() {
        return icon;
    }
    public Item getResult() {
        return result;
    }
    public int getSteel() {
        return steel;
    }
    public int getGlass() {
        return glass;
    }
    public int getGold() {
        return gold;
    }
    public int getRedstoneium() {
        return redstoneium;
    }
}

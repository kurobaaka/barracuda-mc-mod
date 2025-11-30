package net.infugogr.barracuda.block.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.List;

public class ChemicalPlantRecipe {

    public final Identifier id;
    public final ChemicalRecipeType type;

    public final List<ItemStack> inputs;
    public final Fluid inputFluid;
    public final int inputFluidAmount;

    public final ItemStack outputItem;
    public final Fluid outputFluid;
    public final int outputFluidAmount;

    public final int craftTime;

    public ChemicalPlantRecipe(
            Identifier id,
            ChemicalRecipeType type,
            List<ItemStack> inputs,
            Fluid inputFluid, int inputFluidAmount,
            ItemStack outputItem,
            Fluid outputFluid, int outputFluidAmount,
            int craftTime
    ) {
        this.id = id;
        this.type = type;
        this.inputs = inputs;
        this.inputFluid = inputFluid;
        this.inputFluidAmount = inputFluidAmount;
        this.outputItem = outputItem;
        this.outputFluid = outputFluid;
        this.outputFluidAmount = outputFluidAmount;
        this.craftTime = craftTime;
    }
}

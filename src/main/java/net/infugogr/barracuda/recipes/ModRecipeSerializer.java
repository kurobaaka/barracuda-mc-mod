package net.infugogr.barracuda.recipes;


import com.mojang.serialization.Codec;
import net.infugogr.barracuda.Barracuda;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public interface ModRecipeSerializer<T extends Recipe<?>> {

    Codec<T> codec();

    static <S extends RecipeSerializer<T>, T extends Recipe<?>> S register(String name, S serializer) {
        Barracuda.LOGGER.info(name, serializer);
        return Registry.register(Registries.RECIPE_SERIALIZER, new Identifier(Barracuda.MOD_ID, name), serializer);
    }

    static void registerModRecipes() {
        Barracuda.LOGGER.info("Registering Mod Recipes for " + Barracuda.MOD_ID);
    }
}

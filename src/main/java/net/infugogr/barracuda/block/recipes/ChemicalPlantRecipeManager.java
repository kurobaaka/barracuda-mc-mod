package net.infugogr.barracuda.block.recipes;

import com.mojang.logging.LogUtils;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import com.google.gson.*;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.infugogr.barracuda.Barracuda;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;


public class ChemicalPlantRecipeManager implements SimpleSynchronousResourceReloadListener {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new Gson();
    private static final Identifier ID = Barracuda.id("chemical_plant_manager");

    private final Map<Identifier, ChemicalPlantRecipe> recipes = new HashMap<>();
    private static ChemicalPlantRecipeManager INSTANCE;

    private ChemicalPlantRecipeManager() {}

    public static ChemicalPlantRecipeManager getInstance() {
        if (INSTANCE == null) INSTANCE = new ChemicalPlantRecipeManager();
        return INSTANCE;
    }

    public static void register() {
        ResourceManagerHelper.get(ResourceType.SERVER_DATA)
                .registerReloadListener(getInstance());
        LOGGER.info("Registered ChemicalPlantRecipeManager reload listener");
    }

    @Override
    public Identifier getFabricId() { return ID; }

    @Override
    public void reload(ResourceManager manager) {

        recipes.clear();
        LOGGER.info("Loading chemical plant recipes from data/barracuda/chemical_plant_recipes/");

        Map<Identifier, Resource> found = manager.findResources(
                "chemical_plant_recipes",
                id -> id.getPath().endsWith(".json")
        );

        for (var entry : found.entrySet()) {

            Identifier id = entry.getKey();
            Resource res = entry.getValue();

            try {
                JsonObject root = GSON.fromJson(
                        new InputStreamReader(res.getInputStream(), StandardCharsets.UTF_8),
                        JsonObject.class
                );
                if (root == null) continue;

                ChemicalRecipeType type = ChemicalRecipeType.fromString(
                        root.get("type").getAsString()
                );

                List<ItemStack> inputs =
                        CentrifugeRecipe.parseResults(root.getAsJsonArray("inputs"));

                Fluid inFluid = null;
                int inAmt = 0;

                if (root.has("input_fluid")) {
                    inFluid = Registries.FLUID.get(new Identifier(root.get("input_fluid").getAsString()));
                    inAmt = (int)(root.get("input_fluid_buckets").getAsDouble() * 81000);
                }

                ItemStack outputItem = ItemStack.EMPTY;
                if (root.has("output")) {
                    var o = root.getAsJsonObject("output");
                    outputItem = new ItemStack(
                            Registries.ITEM.get(new Identifier(o.get("item").getAsString())),
                            o.get("count").getAsInt()
                    );
                }

                Fluid outFluid = null;
                int outAmt = 0;
                if (root.has("output_fluid")) {
                    outFluid = Registries.FLUID.get(new Identifier(root.get("output_fluid").getAsString()));
                    outAmt = (int)(root.get("output_fluid_buckets").getAsDouble() * 81000);
                }

                int craftTime = root.get("craft_time").getAsInt();

                String fileName = id.getPath().substring(id.getPath().lastIndexOf('/') + 1);
                fileName = fileName.replace(".json", "");

                Identifier recipeId = new Identifier("barracuda", fileName);

                recipes.put(recipeId, new ChemicalPlantRecipe(
                        recipeId, type, inputs,
                        inFluid, inAmt,
                        outputItem,
                        outFluid, outAmt,
                        craftTime
                ));

                LOGGER.info("Loaded chemical plant recipe: {}", recipeId);

            } catch (Exception e) {
                LOGGER.error("Failed to load chemical plant recipe {}", id, e);
            }
        }

        LOGGER.info("ChemicalPlant recipes loaded: {}", recipes.size());
    }

    public Optional<ChemicalPlantRecipe> getRecipeFor(ItemStack a, ItemStack b, Fluid fluid) {

        for (var recipe : recipes.values()) {

            boolean matchA = false;
            boolean matchB = false;
            boolean matchFluid = false;

            switch (recipe.type) {

                case ITEM -> {
                    matchA = !a.isEmpty()
                            && a.getItem() == recipe.inputs.get(0).getItem()
                            && a.getCount() >= recipe.inputs.get(0).getCount();

                    if (matchA) return Optional.of(recipe);
                }

                case ITEM_ITEM -> {
                    matchA = !a.isEmpty()
                            && a.getItem() == recipe.inputs.get(0).getItem()
                            && a.getCount() >= recipe.inputs.get(0).getCount();

                    matchB = !b.isEmpty()
                            && b.getItem() == recipe.inputs.get(1).getItem()
                            && b.getCount() >= recipe.inputs.get(1).getCount();

                    if (matchA && matchB) return Optional.of(recipe);
                }

                case ITEM_FLUID -> {
                    matchA = !a.isEmpty()
                            && a.getItem() == recipe.inputs.get(0).getItem()
                            && a.getCount() >= recipe.inputs.get(0).getCount();

                    matchFluid = recipe.inputFluid != null
                            && recipe.inputFluid == fluid;

                    if (matchA && matchFluid) return Optional.of(recipe);
                }

                case ITEM_ITEM_FLUID -> {
                    matchA = !a.isEmpty()
                            && a.getItem() == recipe.inputs.get(0).getItem()
                            && a.getCount() >= recipe.inputs.get(0).getCount();

                    matchB = !b.isEmpty()
                            && b.getItem() == recipe.inputs.get(1).getItem()
                            && b.getCount() >= recipe.inputs.get(1).getCount();

                    matchFluid = recipe.inputFluid != null
                            && recipe.inputFluid == fluid;

                    if (matchA && matchB && matchFluid) return Optional.of(recipe);
                }
            }
        }

        return Optional.empty();
    }
}
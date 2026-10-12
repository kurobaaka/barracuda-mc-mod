package net.infugogr.barracuda.recipes;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PressRecipeManager implements SimpleSynchronousResourceReloadListener {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new Gson();
    private static final Identifier ID = new Identifier("barracuda", "press_manager");

    private final Map<Identifier, PressRecipe> recipes = new HashMap<>();
    private static PressRecipeManager INSTANCE;


    private PressRecipeManager() {}


    public static PressRecipeManager getInstance() {
        if (INSTANCE == null) INSTANCE = new PressRecipeManager();
        return INSTANCE;
    }


    public static void register() {
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(getInstance());
        LOGGER.info("Registered PressRecipeManager reload listener");
    }


    @Override
    public Identifier getFabricId() { return ID; }


    @Override
    public void reload(ResourceManager manager) {

        recipes.clear();
        LOGGER.info("Loading press recipes from data/barracuda/press_recipes/");

        Map<Identifier, Resource> found = manager.findResources(
                "press_recipes",
                id -> id.getPath().endsWith(".json")
        );

        for (Map.Entry<Identifier, Resource> entry : found.entrySet()) {

            Identifier id = entry.getKey();
            Resource res = entry.getValue();

            try {
                InputStreamReader reader = new InputStreamReader(
                        res.getInputStream(),
                        StandardCharsets.UTF_8
                );

                JsonObject root = GSON.fromJson(reader, JsonObject.class);
                if (root == null) continue;

                String input = root.has("input") ? root.get("input").getAsString() : null;
                if (input == null) {
                    LOGGER.warn("Press recipe {} missing 'input' field", id);
                    continue;
                }

                // Parse shape results
                Map<String, ItemStack> shapeResults = new HashMap<>();
                if (root.has("results") && root.get("results").isJsonObject()) {
                    JsonObject resultsObj = root.getAsJsonObject("results");
                    for (String shapeId : resultsObj.keySet()) {
                        String resultStr = resultsObj.get(shapeId).getAsString();
                        ItemStack result = PressRecipe.parseResult(resultStr);
                        if (!result.isEmpty()) {
                            shapeResults.put(shapeId, result);
                        }
                    }
                }

                // Parse block output (9 items → block)
                String blockOutput = root.has("block_output") ? root.get("block_output").getAsString() : null;

                if (shapeResults.isEmpty() && blockOutput == null) {
                    LOGGER.warn("Press recipe {} has no valid shape results or block output", id);
                    continue;
                }

                String fileName = id.getPath().substring(id.getPath().lastIndexOf('/') + 1);
                if (fileName.endsWith(".json")) {
                    fileName = fileName.substring(0, fileName.length() - 5);
                }

                Identifier recipeId = new Identifier("barracuda", fileName);

                recipes.put(recipeId, new PressRecipe(recipeId, input, shapeResults, blockOutput));

                LOGGER.info("Loaded press recipe: {} with {} shape variants and block output: {}", recipeId, shapeResults.size(), blockOutput != null);

            } catch (Exception e) {
                LOGGER.error("Failed to load press recipe {}", id, e);
            }
        }

        LOGGER.info("Press recipes loaded: {}", recipes.size());
    }

    public Optional<PressRecipe> getRecipeFor(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return Optional.empty();
        String stackId = Registries.ITEM.getId(stack.getItem()).toString();
        for (PressRecipe r : recipes.values()) {
            if (r.getInputId().equals(stackId)) return Optional.of(r);
        }
        return Optional.empty();
    }
}


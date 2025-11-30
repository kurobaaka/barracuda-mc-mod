package net.infugogr.barracuda.block.recipes;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;


import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class CentrifugeRecipeManager implements SimpleSynchronousResourceReloadListener {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new Gson();
    private static final Identifier ID = new Identifier("barracuda", "centrifuge_manager");

    private final Map<Identifier, CentrifugeRecipe> recipes = new HashMap<>();
    private static CentrifugeRecipeManager INSTANCE;


    private CentrifugeRecipeManager() {}


    public static CentrifugeRecipeManager getInstance() {
        if (INSTANCE == null) INSTANCE = new CentrifugeRecipeManager();
        return INSTANCE;
    }


    public static void register() {
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(getInstance());
        LOGGER.info("Registered CentrifugeRecipeManager reload listener");
    }


    @Override
    public Identifier getFabricId() { return ID; }


    @Override
    public void reload(ResourceManager manager) {

        recipes.clear();
        LOGGER.info("Loading centrifuge recipes from data/barracuda/centrifuge_recipes/");

        Map<Identifier, Resource> found = manager.findResources(
                "centrifuge_recipes",
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
                JsonArray resultsArr = root.has("results") ? root.getAsJsonArray("results") : null;

                float xp = root.has("experience") ? root.get("experience").getAsFloat() : 0f;
                int time = root.has("craft_time") ? root.get("craft_time").getAsInt() : 200;

                List<ItemStack> outputs = CentrifugeRecipe.parseResults(resultsArr);

                String fileName = id.getPath().substring(id.getPath().lastIndexOf('/') + 1);
                if (fileName.endsWith(".json")) {
                    fileName = fileName.substring(0, fileName.length() - 5);
                }

                Identifier recipeId = new Identifier("barracuda", fileName);

                recipes.put(recipeId, new CentrifugeRecipe(recipeId, input, outputs, time));

                LOGGER.info("Loaded centrifuge recipe: {}", recipeId);

            } catch (Exception e) {
                LOGGER.error("Failed to load centrifuge recipe {}", id, e);
            }
        }

        LOGGER.info("Centrifuge recipes loaded: {}", recipes.size());
    }

    public Optional<CentrifugeRecipe> getRecipeFor(net.minecraft.item.ItemStack stack) {
        if (stack == null || stack.isEmpty()) return Optional.empty();
        String stackId = Registries.ITEM.getId(stack.getItem()).toString();
        for (CentrifugeRecipe r : recipes.values()) {
            if (r.getInputId().equals(stackId)) return Optional.of(r);
        }
        return Optional.empty();
    }
}


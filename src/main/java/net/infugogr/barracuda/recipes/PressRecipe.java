package net.infugogr.barracuda.recipes;

import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.Map;

public record PressRecipe(Identifier id, String input, Map<String, ItemStack> shapeResults, String blockOutput) {

    public String getInputId() {
        return input;
    }

    public ItemStack getResultForShape(String shapeId) {
        if (shapeId == null || shapeId.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack result = shapeResults.getOrDefault(shapeId, ItemStack.EMPTY);
        return result.isEmpty() ? ItemStack.EMPTY : result.copy();
    }

    public ItemStack getBlockResult() {
        if (blockOutput == null || blockOutput.isEmpty()) {
            return ItemStack.EMPTY;
        }
        return parseResult(blockOutput);
    }

    // Helper parser for individual result object
    public static ItemStack parseResult(JsonObject obj) {
        if (obj == null) return ItemStack.EMPTY;
        String itemId = obj.has("item") ? obj.get("item").getAsString() : null;
        int count = obj.has("count") ? obj.get("count").getAsInt() : 1;
        if (itemId == null) return ItemStack.EMPTY;
        try {
            Identifier id = new Identifier(itemId);
            Item item = Registries.ITEM.get(id);
            if (item == Items.AIR) return ItemStack.EMPTY;
            return new ItemStack(item, Math.max(1, Math.min(64, count)));
        } catch (Exception e) {
            return ItemStack.EMPTY;
        }
    }

    public static ItemStack parseResult(String result) {
        if (result == null || result.isEmpty()) {
            return ItemStack.EMPTY;
        }
        String itemPart = result;
        int count = 1;
        if (result.contains("*")) {
            String[] split = result.split("\\*", 2);
            itemPart = split[0];

            try {
                count = Integer.parseInt(split[1]);
            } catch (NumberFormatException e) {
                count = 1;
            }
        }
        Identifier id = Identifier.tryParse(itemPart);
        if (id == null) {
            return ItemStack.EMPTY;
        }
        Item item = Registries.ITEM.get(id);

        if (item == Items.AIR) {
            return ItemStack.EMPTY;
        }

        return new ItemStack(item, count);
    }
}
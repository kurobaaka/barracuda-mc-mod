package net.infugogr.barracuda.block.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

/**
 * @param input   "minecraft:iron_ore" etc.
 * @param outputs up to 9
 */
public record CentrifugeRecipe(Identifier id, String input, List<ItemStack> outputs, int craftTime) {
    public String getInputId() {
        return input;
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

    public static List<ItemStack> parseResults(JsonArray arr) {
        List<ItemStack> outs = new ArrayList<>();
        if (arr == null) return outs;
        for (JsonElement e : arr) {
            if (!e.isJsonObject()) continue;
            ItemStack s = parseResult(e.getAsJsonObject());
            if (!s.isEmpty()) outs.add(s);
            if (outs.size() >= 9) break;
        }
        return outs;
    }
}
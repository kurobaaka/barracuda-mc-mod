package net.infugogr.barracuda.util;

import net.infugogr.barracuda.Barracuda;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ModTags {
    public static final TagKey<Item> PARTS = of("parts");
    public static final TagKey<Item> PLATES = of("plates");
    public static final TagKey<Item> CABELS = of("cabels");

    private static TagKey<Item> of(String name) {
        return TagKey.of(RegistryKeys.ITEM, Identifier.of(Barracuda.MOD_ID, name));
    }

}

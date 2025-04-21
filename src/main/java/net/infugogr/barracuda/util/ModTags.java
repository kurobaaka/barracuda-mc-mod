package net.infugogr.barracuda.util;

import net.infugogr.barracuda.Barracuda;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ModTags {
    public static final TagKey<Item> PARTS = of("parts");
    public static final TagKey<Item> PLATES = of("plates");
    public static final TagKey<Item> CABELS = of("cabels");
    public static final TagKey<Item> FLUIDS = of("fluids");
    public static final TagKey<Item> CRUSHER_RECIPE_ITEMS = of("crusher");

    private static TagKey<Item> of(String name) {
        return TagKey.of(RegistryKeys.ITEM, Identifier.of(Barracuda.MOD_ID, name));
    }
    public static class Fluids {
        public static final TagKey<Fluid> CRUDE_OIL = TagKey.of(RegistryKeys.FLUID, Barracuda.id("crude_oil"));
    }
}

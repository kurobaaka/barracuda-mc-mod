package net.infugogr.barracuda.util;

import net.infugogr.barracuda.Barracuda;
import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.event.GeoRenderEvent;

public class ModTags {
    public static final TagKey<Item> PARTS = of("parts");
    public static final TagKey<Item> PLATES = of("plates");
    public static final TagKey<Item> CABELS = of("cabels");
    public static final TagKey<Item> FLUIDS = of("fluids");
    public static final TagKey<Item> CRUSHER_RECIPE_ITEMS = of("crusher");
    public static final TagKey<Block> SHUTTLE_BLOCKS = off("shuttle_blocks");
    private static TagKey<Item> of(String name) {
        return TagKey.of(RegistryKeys.ITEM, Identifier.of(Barracuda.MOD_ID, name));
    }
    private static TagKey<Block> off(String name) {
        return TagKey.of(RegistryKeys.BLOCK, Identifier.of(Barracuda.MOD_ID, name));
    }
    public static class Fluids {
        public static final TagKey<Fluid> CRUDE_OIL = TagKey.of(RegistryKeys.FLUID, Barracuda.id("crude_oil"));
        public static final TagKey<Fluid> HEAVY_OIL = TagKey.of(RegistryKeys.FLUID, Barracuda.id("heavy_oil"));
        public static final TagKey<Fluid> DIESEL = TagKey.of(RegistryKeys.FLUID, Barracuda.id("diesel"));
        public static final TagKey<Fluid> GAS = TagKey.of(RegistryKeys.FLUID, Barracuda.id("gas"));

    }
}

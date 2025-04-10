package net.infugogr.barracuda.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.infugogr.barracuda.Barracuda;
import net.infugogr.barracuda.fluid.ModFluids;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import java.util.function.Function;

public class ModItems {
    public static final Item AQUATIC_DUST = register("aquatic_dust");
    public static final Item SEA_MINERAL = register("sea_mineral");
    public static final Item SEA_INGOT = register("sea_ingot");
    public static final Item CURSED_GOLD_INGOT = register("cursed_gold_ingot");
    public static final TormentedSoul TORMENTED_SOUL = register("tormented_soul",
            TormentedSoul::new, settings -> settings.maxCount(16));
    public static final Item REDSTONEIUM_INGOT = register("redstoneium_ingot");
    public static final Item CHARGED_REDSTONEIUM_INGOT = register("charged_redstoneium_ingot");
    public static final Item HEART_OF_RED_HARBOR = register("heart_of_red_harbor");

    //public static final SwordItem CURSED_KNIFE = registerItem("cursed_knife",
    //        new SwordItem(ToolMaterial.GOLD, 3.0F, -2.4F, new Item.Settings()));
    public static final Item STONE_MORTAR = register("stone_mortar");
    public static final Item URANIUM_DUST = register("uranium_dust");
    public static final Item URANIUM_INGOT = register("uranium_ingot");
    public static final Item URANIUM_238 = register("uranium_238");
    public static final Item URANIUM_235 = register("uranium_235");
    public static final Item DISCHARGED_URANIUM_INGOT = register("discharged_uranium_ingot");
    public static final Item URANIUM_NUGGET = register("uranium_nugget");
    public static final Item DISCHARGED_URANIUM_NUGGET = register("discharged_uranium_nugget");
    public static final Item FUEL_GENERATOR_PLATE= register("fuel_generator_plate",
            Item::new, settings -> settings.maxCount(16));
    public static final Item SMES_PLATE= register("smes_plate",
            Item::new, settings -> settings.maxCount(16));
    public static final Item CAPACITOR = register("capacitor");
    public static final Item SCREWDRIVER = register("screwdriver");
    public static final Item HAMMER = register("hammer");

    public static final Item LOST_ANCHOR = registerItem("lost_anchor",
    new SwordItem(ModToolMaterial.RUBY, 5, 3f, new FabricItemSettings()));
    public static final Item ANCHOR = registerItem("anchor",
    new SwordItem(ModToolMaterial.RUBY, 5, 3f, new FabricItemSettings()));
    public static final Item SAPPHIRE = register("sapphire");
    public static final Item SAPPHIRE_DUST = registerItem("sapphire_dust",
            new Item(new FabricItemSettings()));
    public static final Item RUBY = register("ruby");
    public static final Item RUBY_DUST = registerItem("ruby_dust",
            new Item(new FabricItemSettings()));
    public static final Item MUD = register("mud");
    public static final Item FIR_CONE = register("fir_cone");
    public static final Item PINE_CONE = register("pine_cone");
    public static final Item BRONZE_BLADE = register("bronze_blade");
    public static final Item BRONZE_INGOT = register("bronze_ingot");
    public static final Item BRONZE_PLATE = register("bronze_plate");
    public static final Item BRONZE_SHARDS = register("bronze_shards");
    public static final Item METAL_BLADE = register("metal_blade");
    public static final Item METAL_PLATE = register("metal_plate");
    public static final Item METAL_SHARDS = register("metal_shards");
    public static final Item SILVER_INGOT = register("silver_ingot");
    public static final Item STEEL_BLADE = register("steel_blade");
    public static final Item STEEL_INGOT = register("steel_ingot");
    public static final Item STEEL_PLATE = register("steel_plate");
    public static final Item STEEL_SHARDS = register("steel_shards");
    public static final Item DOUBLOON = register("doubloon");
    public static final LiveCapsule LIVE_CAPSULE = registerItem("live_capsule",
            new LiveCapsule(null, new FabricItemSettings()));
    public static final Item CRUDE_OIL_BUCKET = ModFluids.CRUDE_OIL.bucket();
    public static final Item HEAVY_OIL_BUCKET = register("heavy_oil_bucket",
            Item::new, settings -> settings.maxCount(1).recipeRemainder(Items.BUCKET));
    public static final Item DIESEL_BUCKET= register("diesel_bucket",
            Item::new, settings -> settings.maxCount(1).recipeRemainder(Items.BUCKET));
    public static final Item GAS_CAPSULE = register("gas_capsule",
            Item::new, settings -> settings.maxCount(16).recipeRemainder(ModItems.EMPTY_CAPSULE));
    public static final Item WATER_CAPSULE = registerItem("water_capsule",
            (Item)(new CapsuleItem(Fluids.WATER, (new Item.Settings()).recipeRemainder(ModItems.EMPTY_CAPSULE).maxCount(16))));
    public static final Item DIESEL_CAPSULE = register("diesel_capsule",
            Item::new, settings -> settings.maxCount(16).recipeRemainder(ModItems.EMPTY_CAPSULE));
    public static final Item HEAVY_OIL_CAPSULE = register("heavy_oil_capsule",
            Item::new, settings -> settings.maxCount(16).recipeRemainder(ModItems.EMPTY_CAPSULE));
    public static final Item CRUDE_OIL_CAPSULE = registerItem("crude_oil_capsule",
            (Item)(new CapsuleItem(ModFluids.CRUDE_OIL.still(), (new Item.Settings()).recipeRemainder(ModItems.EMPTY_CAPSULE).maxCount(16))));
    public static final Item LAVA_CAPSULE = registerItem("lava_capsule",
            (Item)(new CapsuleItem(Fluids.LAVA, (new Item.Settings()).recipeRemainder(ModItems.EMPTY_CAPSULE).maxCount(16))));
    public static final Item EMPTY_CAPSULE = registerItem("empty_capsule",
            (Item)(new CapsuleItem(Fluids.EMPTY, (new Item.Settings()).maxCount(16))));

        // Weapons stuff

    public static final Item RUBY_PICKAXE = registerItem("ruby_pickaxe",
            new PickaxeItem(ModToolMaterial.RUBY, 2, 2f, new FabricItemSettings()));
    public static final Item RUBY_AXE = registerItem("ruby_axe",
            new AxeItem(ModToolMaterial.RUBY, 3, 1f, new FabricItemSettings()));
    public static final Item RUBY_SHOVEL = registerItem("ruby_shovel",
            new ShovelItem(ModToolMaterial.RUBY, 0, 0f, new FabricItemSettings()));
    public static final Item RUBY_SWORD = registerItem("ruby_sword",
            new SwordItem(ModToolMaterial.RUBY, 5, 3f, new FabricItemSettings()));
    public static final Item RUBY_HOE = registerItem("ruby_hoe",
            new HoeItem(ModToolMaterial.RUBY, 0, 0f, new FabricItemSettings()));

    public static final Item ENERGY_PICKAXE = registerItem("energy_pickaxe",
            new PickaxeItem(ModToolMaterial.ENERGY, 2, 2f, new FabricItemSettings()));
    public static final Item ENERGY_AXE = registerItem("energy_axe",
            new AxeItem(ModToolMaterial.ENERGY, 3, 1f, new FabricItemSettings()));
    public static final Item ENERGY_SHOVEL = registerItem("energy_shovel",
            new ShovelItem(ModToolMaterial.ENERGY, 0, 0f, new FabricItemSettings()));
    public static final Item ENERGY_SWORD = registerItem("energy_sword",
            new SwordItem(ModToolMaterial.ENERGY, 5, 3f, new FabricItemSettings()));
    public static final Item ENERGY_HOE = registerItem("energy_hoe",
            new HoeItem(ModToolMaterial.ENERGY, 0, 0f, new FabricItemSettings()));

    public static final Item AQUATIC_SWORD = registerItem("aquatic_longsword",
            new SwordItem(ModToolMaterial.AQUATIC, 5, 3f, new FabricItemSettings()));

    public static final Item DWARVEN_PICKAXE = registerItem("dwarven_pickaxe",
            new PickaxeItem(ModToolMaterial.SAPPHIRE, 2, 2f, new FabricItemSettings()));
    public static final Item DWARVEN_AXE = registerItem("dwarven_axe",
            new AxeItem(ModToolMaterial.SAPPHIRE, 3, 1f, new FabricItemSettings()));
    public static final Item DWARVEN_SHOVEL = registerItem("dwarven_shovel",
            new ShovelItem(ModToolMaterial.SAPPHIRE, 0, 0f, new FabricItemSettings()));
            


    public static final Item SAPPHIRE_PICKAXE = registerItem("sapphire_pickaxe",
            new PickaxeItem(ModToolMaterial.SAPPHIRE, 2, 2f, new FabricItemSettings()));
    public static final Item SAPPHIRE_AXE = registerItem("sapphire_axe",
            new AxeItem(ModToolMaterial.SAPPHIRE, 3, 1f, new FabricItemSettings()));
    public static final Item SAPPHIRE_SHOVEL = registerItem("sapphire_shovel",
            new ShovelItem(ModToolMaterial.SAPPHIRE, 0, 0f, new FabricItemSettings()));
    public static final Item SAPPHIRE_SWORD = registerItem("sapphire_sword",
            new SwordItem(ModToolMaterial.SAPPHIRE, 5, 3f, new FabricItemSettings()));
    public static final Item SAPPHIRE_HOE = registerItem("sapphire_hoe",
            new HoeItem(ModToolMaterial.SAPPHIRE, 0, 0f, new FabricItemSettings()));

            
    public static final Item EXECUTION_SWORD = registerItem("execution_sword",
            new SwordItem(ModToolMaterial.STEEL, 5, -3.5f, new FabricItemSettings()));
    public static final Item EXECUTION_AXE = registerItem("execution_axe",
            new SwordItem(ModToolMaterial.STEEL, 5, -3.5f, new FabricItemSettings()));
        
        // Armor stuff

    public static final Item ENERGY_HELMET = registerItem("energy_helmet",
        new ArmorItem(ModArmorMaterials.ENERGY, ArmorItem.Type.HELMET, new FabricItemSettings()));
    public static final Item ENERGY_CHESTPLATE = registerItem("energy_chestplate",
        new ArmorItem(ModArmorMaterials.ENERGY, ArmorItem.Type.CHESTPLATE, new FabricItemSettings()));
    public static final Item ENERGY_LEGGINGS = registerItem("energy_leggings",
        new ArmorItem(ModArmorMaterials.ENERGY, ArmorItem.Type.LEGGINGS, new FabricItemSettings()));
    public static final Item ENERGY_BOOTS = registerItem("energy_boots",
        new ArmorItem(ModArmorMaterials.ENERGY, ArmorItem.Type.BOOTS, new FabricItemSettings()));

    public static final Item RUBY_HELMET = registerItem("ruby_helmet",
        new ArmorItem(ModArmorMaterials.RUBY, ArmorItem.Type.HELMET, new FabricItemSettings()));
    public static final Item RUBY_CHESTPLATE = registerItem("ruby_chestplate",
        new ArmorItem(ModArmorMaterials.RUBY, ArmorItem.Type.CHESTPLATE, new FabricItemSettings()));
    public static final Item RUBY_LEGGINGS = registerItem("ruby_leggings",
        new ArmorItem(ModArmorMaterials.RUBY, ArmorItem.Type.LEGGINGS, new FabricItemSettings()));
    public static final Item RUBY_BOOTS = registerItem("ruby_boots",
        new ArmorItem(ModArmorMaterials.RUBY, ArmorItem.Type.BOOTS, new FabricItemSettings()));


    public static final Item ENGINEER_HELMET = registerItem("engineer_helmet",
            new ArmorItem(ModArmorMaterials.ENGINEER, ArmorItem.Type.HELMET, new FabricItemSettings()));
    public static final Item ENGINEER_CHESTPLATE = registerItem("engineer_chestplate",
            new ArmorItem(ModArmorMaterials.ENGINEER, ArmorItem.Type.CHESTPLATE, new FabricItemSettings()));
    public static final Item ENGINEER_LEGGINGS = registerItem("engineer_leggings",
            new ArmorItem(ModArmorMaterials.ENGINEER, ArmorItem.Type.LEGGINGS, new FabricItemSettings()));
    public static final Item ENGINEER_BOOTS = registerItem("engineer_boots",
            new ArmorItem(ModArmorMaterials.ENGINEER, ArmorItem.Type.BOOTS, new FabricItemSettings()));

    public static final Item SAPPHIRE_HELMET = registerItem("sapphire_helmet",
            new ArmorItem(ModArmorMaterials.SAPPHIRE, ArmorItem.Type.HELMET, new FabricItemSettings()));
    public static final Item SAPPHIRE_CHESTPLATE = registerItem("sapphire_chestplate",
            new ArmorItem(ModArmorMaterials.SAPPHIRE, ArmorItem.Type.CHESTPLATE, new FabricItemSettings()));
    public static final Item SAPPHIRE_LEGGINGS = registerItem("sapphire_leggings",
            new ArmorItem(ModArmorMaterials.SAPPHIRE, ArmorItem.Type.LEGGINGS, new FabricItemSettings()));
    public static final Item SAPPHIRE_BOOTS = registerItem("sapphire_boots",
            new ArmorItem(ModArmorMaterials.SAPPHIRE, ArmorItem.Type.BOOTS, new FabricItemSettings()));

    public static final Item SHROOM_HELMET = registerItem("shroom_helmet",
            new ArmorItem(ModArmorMaterials.SHROOM, ArmorItem.Type.HELMET, new FabricItemSettings()));
    public static final Item SHROOM_CHESTPLATE = registerItem("shroom_chestplate",
            new ArmorItem(ModArmorMaterials.SHROOM, ArmorItem.Type.CHESTPLATE, new FabricItemSettings()));
    public static final Item SHROOM_LEGGINGS = registerItem("shroom_leggings",
            new ArmorItem(ModArmorMaterials.SHROOM, ArmorItem.Type.LEGGINGS, new FabricItemSettings()));
    public static final Item SHROOM_BOOTS = registerItem("shroom_boots",
            new ArmorItem(ModArmorMaterials.SHROOM, ArmorItem.Type.BOOTS, new FabricItemSettings()));

    public static final Item DRUID_HELMET = registerItem("druid_helmet",
            new ArmorItem(ModArmorMaterials.DRUID_WOOD, ArmorItem.Type.HELMET, new FabricItemSettings()));
    public static final Item DRUID_CHESTPLATE = registerItem("druid_chestplate",
            new ArmorItem(ModArmorMaterials.DRUID_WOOD, ArmorItem.Type.CHESTPLATE, new FabricItemSettings()));
    public static final Item DRUID_LEGGINGS = registerItem("druid_leggings",
            new ArmorItem(ModArmorMaterials.DRUID_WOOD, ArmorItem.Type.LEGGINGS, new FabricItemSettings()));
    public static final Item DRUID_BOOTS = registerItem("druid_boots",
            new ArmorItem(ModArmorMaterials.DRUID_WOOD, ArmorItem.Type.BOOTS, new FabricItemSettings()));

    public static final Item MOSS_HELMET = registerItem("moss_helmet",
            new ArmorItem(ModArmorMaterials.MOSS, ArmorItem.Type.HELMET, new FabricItemSettings()));
    public static final Item MOSS_CHESTPLATE = registerItem("moss_chestplate",
            new ArmorItem(ModArmorMaterials.MOSS, ArmorItem.Type.CHESTPLATE, new FabricItemSettings()));

    public static final Item ILLUSIONER_CHESTPLATE = registerItem("illusioner_chestplate",
            new ArmorItem(ModArmorMaterials.FABRIC, ArmorItem.Type.CHESTPLATE, new FabricItemSettings()));
    public static final Item ILLUSIONER_BOOTS = registerItem("illusioner_boots",
            new ArmorItem(ModArmorMaterials.FABRIC, ArmorItem.Type.BOOTS, new FabricItemSettings()));

    public static final Item EVOKER_CHESTPLATE = registerItem("evoker_chestplate",
            new ArmorItem(ModArmorMaterials.FABRIC_1, ArmorItem.Type.CHESTPLATE, new FabricItemSettings()));
    public static final Item EVOKER_BOOTS = registerItem("evoker_boots",
            new ArmorItem(ModArmorMaterials.FABRIC_1, ArmorItem.Type.BOOTS, new FabricItemSettings()));

        // food items

    public static final Item PORK = registerItem("pork", new Item(new FabricItemSettings().food(ModFoodComponents.PORK)));
    public static final Item PORK_COOKED = registerItem("pork_cooked", new Item(new FabricItemSettings().food(ModFoodComponents.PORK_COOKED)));
    public static final Item CHICKEN_WING = registerItem("chicken_wing", new Item(new FabricItemSettings().food(ModFoodComponents.CHICKEN_WING)));
    public static final Item CHICKEN_WING_COOKED = registerItem("chicken_wing_cooked", new Item(new FabricItemSettings().food(ModFoodComponents.CHICKEN_WING_COOKED)));
    public static final Item FISH_EEL = registerItem("fish_eel", new Item(new FabricItemSettings().food(ModFoodComponents.FISH_EEL)));
    public static final Item FISH_BLUEFISH = registerItem("fish_bluefish", new Item(new FabricItemSettings().food(ModFoodComponents.FISH_BLUEFISH)));
    public static final Item FISH_BREAM = registerItem("fish_bream", new Item(new FabricItemSettings().food(ModFoodComponents.FISH_BREAM)));
    public static final Item FISH_TILAPIA = registerItem("fish_tilapia", new Item(new FabricItemSettings().food(ModFoodComponents.FISH_TILAPIA)));
    public static final Item FISH_FLOUNDER = registerItem("fish_flounder", new Item(new FabricItemSettings().food(ModFoodComponents.FISH_FLOUNDER)));
    public static final Item FISH_GLASS_CATFISH = registerItem("fish_glass_catfish", new Item(new FabricItemSettings().food(ModFoodComponents.FISH_GLASS_CATFISH)));
    public static final Item FISH_DOLPHINFISH = registerItem("fish_dolphinfish", new Item(new FabricItemSettings().food(ModFoodComponents.FISH_DOLPHINFISH)));
    public static final Item FISH_PIKE = registerItem("fish_pike", new Item(new FabricItemSettings().food(ModFoodComponents.FISH_PIKE)));
    public static final Item FISH_HERRING = registerItem("fish_herring", new Item(new FabricItemSettings().food(ModFoodComponents.FISH_HERRING)));
    public static final Item FISH_CARP = registerItem("fish_carp", new Item(new FabricItemSettings().food(ModFoodComponents.FISH_CARP)));
    public static final Item FISH_CATFISH = registerItem("fish_catfish", new Item(new FabricItemSettings().food(ModFoodComponents.FISH_CATFISH)));
    public static final Item FISH_SHORT_COD = registerItem("fish_short_cod", new Item(new FabricItemSettings().food(ModFoodComponents.FISH_SHORT_COD)));
    public static final Item FISH_SALMON = registerItem("fish_salmon", new Item(new FabricItemSettings().food(ModFoodComponents.FISH_SALMON)));
    public static final Item FISH_OCTOPUS = registerItem("fish_octopus", new Item(new FabricItemSettings().food(ModFoodComponents.FISH_OCTOPUS)));
    public static final Item FISH_SANDFISH = registerItem("fish_sandfish", new Item(new FabricItemSettings().food(ModFoodComponents.FISH_SANDFISH)));
    public static final Item FISH_ANCHOVY = registerItem("fish_anchovy", new Item(new FabricItemSettings().food(ModFoodComponents.FISH_ANCHOVY)));
    public static final Item FISH_SARDINE = registerItem("fish_sardine", new Item(new FabricItemSettings().food(ModFoodComponents.FISH_SARDINE)));
    public static final Item FISH_BLACK_SEA_BASS = registerItem("fish_black_sea_bass", new Item(new FabricItemSettings().food(ModFoodComponents.FISH_BLACK_SEA_BASS)));
    public static final Item FISH_SEA_CUCUMBER = registerItem("fish_sea_cucumber", new Item(new FabricItemSettings().food(ModFoodComponents.FISH_SEA_CUCUMBER)));
    public static final Item FISH_ROCKFISH = registerItem("fish_rockfish", new Item(new FabricItemSettings().food(ModFoodComponents.FISH_ROCKFISH)));
    public static final Item FISH_STURGEON = registerItem("fish_sturgeon", new Item(new FabricItemSettings().food(ModFoodComponents.FISH_STURGEON)));
    public static final Item FISH_TUNA = registerItem("fish_tuna", new Item(new FabricItemSettings().food(ModFoodComponents.FISH_TUNA)));
    public static final Item FISH_SQUID = registerItem("fish_squid", new Item(new FabricItemSettings().food(ModFoodComponents.FISH_SQUID)));
    public static final Item COOKED_FISH_EEL = registerItem("cooked_fish_eel", new Item(new FabricItemSettings().food(ModFoodComponents.COOKED_FISH_EEL)));
    public static final Item COOKED_FISH_BLUEFISH = registerItem("cooked_fish_bluefish", new Item(new FabricItemSettings().food(ModFoodComponents.COOKED_FISH_BLUEFISH)));
    public static final Item COOKED_FISH_BREAM = registerItem("cooked_fish_bream", new Item(new FabricItemSettings().food(ModFoodComponents.COOKED_FISH_BREAM)));
    public static final Item COOKED_FISH_TILAPIA = registerItem("cooked_fish_tilapia", new Item(new FabricItemSettings().food(ModFoodComponents.COOKED_FISH_TILAPIA)));
    public static final Item COOKED_FISH_FLOUNDER = registerItem("cooked_fish_flounder", new Item(new FabricItemSettings().food(ModFoodComponents.COOKED_FISH_FLOUNDER)));
    public static final Item COOKED_FISH_GLASS_CATFISH = registerItem("cooked_fish_glass_catfish", new Item(new FabricItemSettings().food(ModFoodComponents.COOKED_FISH_GLASS_CATFISH)));
    public static final Item COOKED_FISH_DOLPHINFISH = registerItem("cooked_fish_dolphinfish", new Item(new FabricItemSettings().food(ModFoodComponents.COOKED_FISH_DOLPHINFISH)));
    public static final Item COOKED_FISH_PIKE = registerItem("cooked_fish_pike", new Item(new FabricItemSettings().food(ModFoodComponents.COOKED_FISH_PIKE)));
    public static final Item COOKED_FISH_HERRING = registerItem("cooked_fish_herring", new Item(new FabricItemSettings().food(ModFoodComponents.COOKED_FISH_HERRING)));
    public static final Item COOKED_FISH_CARP = registerItem("cooked_fish_carp", new Item(new FabricItemSettings().food(ModFoodComponents.COOKED_FISH_CARP)));
    public static final Item COOKED_FISH_CATFISH = registerItem("cooked_fish_catfish", new Item(new FabricItemSettings().food(ModFoodComponents.COOKED_FISH_CATFISH)));
    public static final Item COOKED_FISH_SHORT_COD = registerItem("cooked_fish_short_cod", new Item(new FabricItemSettings().food(ModFoodComponents.COOKED_FISH_SHORT_COD)));
    public static final Item COOKED_FISH_SALMON = registerItem("cooked_fish_salmon", new Item(new FabricItemSettings().food(ModFoodComponents.COOKED_FISH_SALMON)));
    public static final Item COOKED_FISH_OCTOPUS = registerItem("cooked_fish_octopus", new Item(new FabricItemSettings().food(ModFoodComponents.COOKED_FISH_OCTOPUS)));
    public static final Item COOKED_FISH_SANDFISH = registerItem("cooked_fish_sandfish", new Item(new FabricItemSettings().food(ModFoodComponents.COOKED_FISH_SANDFISH)));
    public static final Item COOKED_FISH_ANCHOVY = registerItem("cooked_fish_anchovy", new Item(new FabricItemSettings().food(ModFoodComponents.COOKED_FISH_ANCHOVY)));
    public static final Item COOKED_FISH_SARDINE = registerItem("cooked_fish_sardine", new Item(new FabricItemSettings().food(ModFoodComponents.COOKED_FISH_SARDINE)));
    public static final Item COOKED_FISH_BLACK_SEA_BASS = registerItem("cooked_fish_black_sea_bass", new Item(new FabricItemSettings().food(ModFoodComponents.COOKED_FISH_BLACK_SEA_BASS)));
    public static final Item COOKED_FISH_SEA_CUCUMBER = registerItem("cooked_fish_sea_cucumber", new Item(new FabricItemSettings().food(ModFoodComponents.COOKED_FISH_SEA_CUCUMBER)));
    public static final Item COOKED_FISH_ROCKFISH = registerItem("cooked_fish_rockfish", new Item(new FabricItemSettings().food(ModFoodComponents.COOKED_FISH_ROCKFISH)));
    public static final Item COOKED_FISH_STURGEON = registerItem("cooked_fish_sturgeon", new Item(new FabricItemSettings().food(ModFoodComponents.COOKED_FISH_STURGEON)));
    public static final Item COOKED_FISH_TUNA = registerItem("cooked_fish_tuna", new Item(new FabricItemSettings().food(ModFoodComponents.COOKED_FISH_TUNA)));
    public static final Item COOKED_FISH_SQUID = registerItem("cooked_fish_squid", new Item(new FabricItemSettings().food(ModFoodComponents.COOKED_FISH_SQUID)));
/**
    // Fishing Rod Components
    public static final Item MODULAR_FISHING_ROD_ITEM = register(new ModularFishingRodItem(new Item.Settings().maxCount(1)), "modular_fishing_rod");
    // Rods
    public static final Item OAK_ROD = register(new SimpleModeledPolymerItem(new Item.Settings(), Items.PAPER), "oak_rod");
    public static final Item SPRUCE_ROD = register(new SimpleModeledPolymerItem(new Item.Settings(), Items.PAPER), "spruce_rod");
    public static final Item ACACIA_ROD = register(new SimpleModeledPolymerItem(new Item.Settings(), Items.PAPER), "acacia_rod");
    public static final Item BIRCH_ROD = register(new SimpleModeledPolymerItem(new Item.Settings(), Items.PAPER), "birch_rod");
    public static final Item DARK_OAK_ROD = register(new SimpleModeledPolymerItem(new Item.Settings(), Items.PAPER), "dark_oak_rod");
    public static final Item JUNGLE_ROD = register(new SimpleModeledPolymerItem(new Item.Settings(), Items.PAPER), "jungle_rod");
    public static final Item CHERRY_ROD = register(new SimpleModeledPolymerItem(new Item.Settings(), Items.PAPER), "cherry_rod");
    public static final Item MANGROVE_ROD = register(new SimpleModeledPolymerItem(new Item.Settings(), Items.PAPER), "mangrove_rod");
    public static final Item BAMBOO_ROD = register(new SimpleModeledPolymerItem(new Item.Settings(), Items.PAPER), "bamboo_rod");
    public static final Item CRIMSON_ROD = register(new SimpleModeledPolymerItem(new Item.Settings(), Items.PAPER), "crimson_rod");
    public static final Item WARPED_ROD = register(new SimpleModeledPolymerItem(new Item.Settings(), Items.PAPER), "warped_rod");
    // Reels
    public static final Item WOODEN_REEL = register(new SimpleModeledPolymerItem(new Item.Settings(), Items.PAPER), "wooden_reel");
    public static final Item BAMBOO_REEL = register(new SimpleModeledPolymerItem(new Item.Settings(), Items.PAPER), "bamboo_reel");
    public static final Item COPPER_REEL = register(new SimpleModeledPolymerItem(new Item.Settings(), Items.PAPER), "copper_reel");
    // Hooks
    public static final Item IRON_HOOK = register(new SimpleModeledPolymerItem(new Item.Settings(), Items.PAPER), "iron_hook");
    public static final Item SPIKED_HOOK = register(new SimpleModeledPolymerItem(new Item.Settings(), Items.PAPER), "spiked_hook");
    public static final Item WEIGHTED_HOOK = register(new SimpleModeledPolymerItem(new Item.Settings(), Items.PAPER), "weighted_hook");
    // UI Items
    public static final Item BUTTON_ITEM = register(new UIItem(new Item.Settings()), "ui_button");
    // Other
    public static final Item FISH_BONES = register(new SimpleModeledPolymerItem(new Item.Settings(), Items.PAPER) {
//        @Override
        public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
            tooltip.add(Text.translatable("item.reel.fish_bones.tooltip").formatted(Formatting.GRAY));
        }
    }, "fish_bones");
    public static final Item BONEWORK_MECHANISM = register(new SimpleModeledPolymerItem(new Item.Settings(), Items.PAPER), "bonework_mechanism");
    public static final Item ROD_TABLE = register(new ModeledPolymerBlockItem(ReelBlocks.ROD_TABLE_BLOCK, new Item.Settings(), Items.SMITHING_TABLE), "rod_table");
    public static final Item BASSTIARY = register(new BasstiaryItem(new Item.Settings().maxCount(1), Items.PAPER), "basstiary");
    // Fish
    // Shallow
    public static final Item KETTLE_FISH = registerCookableFood(2, 0.1f, 5, 0.6f, "kettle_fish");
    public static final Item GILDED_FISH = registerCookableFood(2, 0.1f, 5, 0.6f, "gilded_fish");
    public static final Item ROSE_EYE = registerCookableFood(2, 0.1f, 5, 0.6f, "rose_eye");
    // Ocean
    public static final Item KELP_SPINE = registerCookableFood(2, 0.1f, 5, 0.6f, "kelp_spine");
    public static final Item BLUNDER = registerCookableFood(2, 0.1f, 5, 0.6f, "blunder");
    public static final Item HALIBLOCK = registerCookableFood(2, 0.1f, 5, 0.6f, "haliblock");
    // Deep
    public static final Item TRAWLER_FISH = registerCookableFood(2, 0.1f, 5, 0.6f, "trawler_fish");
    public static final Item DEATH_EEL = registerCookableFood(2, 0.1f, 5, 0.6f, "death_eel");
    public static final Item SQUISH_FISH = registerCookableFood(2, 0.1f, 5, 0.6f, "squish_fish");
    public static final Item ABYSSAL_GLOW_SQUID = registerCookableFood(2, 0.1f, 5, 0.6f, "abyssal_glow_squid");
    public static final Item BABY_OARFISH = registerCookableFood(2, 0.1f, 5, 0.6f, "baby_oarfish");
    // Cold
    public static final Item RITUAL_FISH = registerCookableFood(2, 0.1f, 5, 0.6f, "ritual_fish");
    public static final Item SPIKE = registerCookableFood(2, 0.1f, 5, 0.6f, "spike");
    public static final Item ICE_CUBE_FISH = registerFood(2, 0.1f, "ice_cube_fish");
    // Tropical
    public static final Item JESTER_FISH = registerCookableFood(2, 0.1f, 5, 0.6f, "jester_fish");
    public static final Item MANTA_RAY = registerCookableFood(2, 0.1f, 5, 0.6f, "manta_ray");
    public static final Item LIONFISH = registerCookableFood(2, 0.1f, 5, 0.6f, "lionfish");
    // Boiling
    public static final Item BASSALT = registerCookableFood(2, 0.1f, 5, 0.6f, "bassalt");
    public static final Item CRIMSON_CROAKER = registerCookableFood(2, 0.1f, 5, 0.6f, "crimson_croaker");
    public static final Item WARPED_EEL = registerCookableFood(2, 0.1f, 5, 0.6f, "warped_eel");
    public static final Item TUNA_MELT = registerFood(2, 0.1f, "tuna_melt");

    public static void initialize() {
        PolymerItemGroupUtils.registerPolymerItemGroup(Barracuda.id("group"), ItemGroup.create(ItemGroup.Row.BOTTOM, -1)
                .icon(COPPER_REEL::getDefaultStack)
                .displayName(Text.translatable("itemgroup.reel"))
                .entries(((context, entries) -> {
                    //// Blocks
                    entries.add(ROD_TABLE);
                    //// Rods
                    entries.add(OAK_ROD);
                    entries.add(SPRUCE_ROD);
                    entries.add(ACACIA_ROD);
                    entries.add(BIRCH_ROD);
                    entries.add(DARK_OAK_ROD);
                    entries.add(JUNGLE_ROD);
                    entries.add(CHERRY_ROD);
                    entries.add(MANGROVE_ROD);
                    entries.add(BAMBOO_ROD);
                    entries.add(CRIMSON_ROD);
                    entries.add(WARPED_ROD);
                    //// Reels
                    entries.add(WOODEN_REEL);
                    entries.add(BAMBOO_REEL);
                    entries.add(COPPER_REEL);
                    //// Hooks
                    entries.add(IRON_HOOK);
                    entries.add(SPIKED_HOOK);
                    entries.add(WEIGHTED_HOOK);
                    //// Other
                    entries.add(FISH_BONES);
                    entries.add(BONEWORK_MECHANISM);
                    //// Fish
                    // Shallow
                    entries.add(KETTLE_FISH);
                    entries.add(GILDED_FISH);
                    entries.add(ROSE_EYE);
                    // Ocean
                    entries.add(KELP_SPINE);
                    entries.add(BLUNDER);
                    entries.add(HALIBLOCK);
                    // Deep
                    entries.add(TRAWLER_FISH);
                    entries.add(DEATH_EEL);
                    entries.add(SQUISH_FISH);
                    entries.add(ABYSSAL_GLOW_SQUID);
                    entries.add(BABY_OARFISH);
                    // Cold
                    entries.add(RITUAL_FISH);
                    entries.add(SPIKE);
                    entries.add(ICE_CUBE_FISH);
                    // Tropical
                    entries.add(JESTER_FISH);
                    entries.add(MANTA_RAY);
                    entries.add(LIONFISH);
                    // Boiling
                    entries.add(BASSALT);
                    entries.add(CRIMSON_CROAKER);
                    entries.add(WARPED_EEL);
                    entries.add(TUNA_MELT);
                    //// Pre-assembled rods
                    entries.add(ModularFishingRodItem.getStackWithComponents(OAK_ROD, WOODEN_REEL, IRON_HOOK), ItemGroup.StackVisibility.PARENT_TAB_ONLY);
                    entries.add(ModularFishingRodItem.getStackWithComponents(BIRCH_ROD, WOODEN_REEL, SPIKED_HOOK), ItemGroup.StackVisibility.PARENT_TAB_ONLY);
                    entries.add(ModularFishingRodItem.getStackWithComponents(MANGROVE_ROD, BAMBOO_REEL, IRON_HOOK), ItemGroup.StackVisibility.PARENT_TAB_ONLY);
                    entries.add(ModularFishingRodItem.getStackWithComponents(SPRUCE_ROD, COPPER_REEL, WEIGHTED_HOOK), ItemGroup.StackVisibility.PARENT_TAB_ONLY);
**/
    public static final IronPlate IRON_PLATE = register("iron_plate",
            IronPlate::new, settings -> settings.maxCount(16));

    private static <T extends Item> T registerItem(String name, T item) {
        return Registry.register(Registries.ITEM, Barracuda.id(name), item);
    }

    public static Item register(String name) {
        return registerItem(name, new Item(new Item.Settings()));
    }

    public static <T extends Item> T register(String name, Function<Item.Settings, T> constructor, Function<Item.Settings, Item.Settings> settingsApplier) {
        return registerItem(name, constructor.apply(
                settingsApplier.apply(new Item.Settings())));
    }


    public static void registerModItems() {
        Barracuda.LOGGER.info("Registering Mod Items for " + Barracuda.MOD_ID);
    }
}

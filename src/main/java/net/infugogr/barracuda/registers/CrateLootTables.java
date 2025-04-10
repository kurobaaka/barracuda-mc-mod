package net.infugogr.barracuda.registers;

import net.minecraft.item.Item;
import net.infugogr.barracuda.block.ModBlocks;

import java.util.Arrays;
import java.util.List;

public class CrateLootTables {
    /**
     * Create loot tables here to be referenced by different classes.
     * <p>
     * Loot tables are basic and cannot weigh the odds of any individual item
     * so to achieve weighed tables it needs to be diluted with duplicate entries.
     */

    //Register crate loot tiers here
    public static List<Item>TierOneCrates = Arrays.asList(
            ModBlocks.BASIC_CRATE.asItem(),
            ModBlocks.PLANTS_CRATE.asItem()
    );

    public static List<Item>TierTwoCrates = Arrays.asList(
            ModBlocks.BASIC_CRATE.asItem(),
            ModBlocks.PLANTS_CRATE.asItem(),
            ModBlocks.RESOURCE_CRATE.asItem(),
            ModBlocks.MOB_CRATE.asItem()
    );

    public static List<Item>TierThreeCrates = Arrays.asList(
            ModBlocks.RESOURCE_CRATE.asItem(),
            ModBlocks.MOB_CRATE.asItem()
    );

    public static List<Item>TierFourCrates = Arrays.asList(
            ModBlocks.RESOURCE_CRATE.asItem(),
            ModBlocks.MOB_CRATE.asItem(),
            ModBlocks.RARE_RESOURCE_CRATE.asItem()
    );
}

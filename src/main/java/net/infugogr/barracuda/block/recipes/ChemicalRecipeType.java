package net.infugogr.barracuda.block.recipes;

public enum ChemicalRecipeType {
    ITEM,
    ITEM_ITEM,
    ITEM_FLUID,
    ITEM_ITEM_FLUID;

    public static ChemicalRecipeType fromString(String s) {
        return switch (s.toLowerCase().replace(" ", "").replace(",", "_")) {
            case "item" -> ITEM;
            case "item_item" -> ITEM_ITEM;
            case "item_fluid" -> ITEM_FLUID;
            case "item_item_fluid" -> ITEM_ITEM_FLUID;
            default -> throw new IllegalArgumentException("Unknown recipe type: " + s);
        };
    }
}

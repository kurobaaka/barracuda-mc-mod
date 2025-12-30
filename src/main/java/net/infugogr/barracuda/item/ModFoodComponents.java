package net.infugogr.barracuda.item;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.FoodComponent;

public class ModFoodComponents {

    public static final FoodComponent RAW_FISH = new FoodComponent.Builder().hunger(2).saturationModifier(0.1f).statusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 600, 0), 0.8F).build();
    public static final FoodComponent COOKED_FISH = new FoodComponent.Builder().hunger(5).saturationModifier(0.3f).build();

    // Рыба
    public static final FoodComponent FISH_EEL = new FoodComponent.Builder().hunger(2).saturationModifier(0.2f).build();
    public static final FoodComponent FISH_BLUEFISH = new FoodComponent.Builder().hunger(2).saturationModifier(0.2f).build();
    public static final FoodComponent FISH_BREAM = new FoodComponent.Builder().hunger(2).saturationModifier(0.2f).build();
    public static final FoodComponent FISH_TILAPIA = new FoodComponent.Builder().hunger(2).saturationModifier(0.2f).build();
    public static final FoodComponent FISH_FLOUNDER = new FoodComponent.Builder().hunger(2).saturationModifier(0.2f).build();
    public static final FoodComponent FISH_GLASS_CATFISH = new FoodComponent.Builder().hunger(2).saturationModifier(0.2f).build();
    public static final FoodComponent FISH_DOLPHINFISH = new FoodComponent.Builder().hunger(3).saturationModifier(0.3f).build();
    public static final FoodComponent FISH_PIKE = new FoodComponent.Builder().hunger(3).saturationModifier(0.3f).build();
    public static final FoodComponent FISH_HERRING = new FoodComponent.Builder().hunger(2).saturationModifier(0.2f).build();
    public static final FoodComponent FISH_CARP = new FoodComponent.Builder().hunger(3).saturationModifier(0.3f).build();
    public static final FoodComponent FISH_CATFISH = new FoodComponent.Builder().hunger(3).saturationModifier(0.3f).build();
    public static final FoodComponent FISH_SHORT_COD = new FoodComponent.Builder().hunger(2).saturationModifier(0.2f).build();
    public static final FoodComponent FISH_SALMON = new FoodComponent.Builder().hunger(4).saturationModifier(0.4f).build();
    public static final FoodComponent FISH_OCTOPUS = new FoodComponent.Builder().hunger(3).saturationModifier(0.3f).build();
    public static final FoodComponent FISH_SANDFISH = new FoodComponent.Builder().hunger(2).saturationModifier(0.2f).build();
    public static final FoodComponent FISH_ANCHOVY = new FoodComponent.Builder().hunger(1).saturationModifier(0.1f).build();
    public static final FoodComponent FISH_SARDINE = new FoodComponent.Builder().hunger(1).saturationModifier(0.1f).build();
    public static final FoodComponent FISH_BLACK_SEA_BASS = new FoodComponent.Builder().hunger(4).saturationModifier(0.4f).build();
    public static final FoodComponent FISH_SEA_CUCUMBER = new FoodComponent.Builder().hunger(1).saturationModifier(0.1f).build();
    public static final FoodComponent FISH_ROCKFISH = new FoodComponent.Builder().hunger(3).saturationModifier(0.3f).build();
    public static final FoodComponent FISH_STURGEON = new FoodComponent.Builder().hunger(5).saturationModifier(0.5f).build();
    public static final FoodComponent FISH_TUNA = new FoodComponent.Builder().hunger(5).saturationModifier(0.5f).build();
    public static final FoodComponent FISH_SQUID = new FoodComponent.Builder().hunger(3).saturationModifier(0.3f).build();
}
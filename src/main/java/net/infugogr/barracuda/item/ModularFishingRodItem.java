//package net.infugogr.barracuda.item;
//
//import net.infugogr.barracuda.Barracuda;
//import net.infugogr.barracuda.ReelItems;
//import net.infugogr.barracuda.util.access.PlayerEntityAccess;
//import net.infugogr.barracuda.entity.MinigameFishingBobberEntity;
//import net.infugogr.barracuda.entity.ModularFishingBobberEntity;
//import eu.pb4.polymer.core.api.item.PolymerItem;
//import eu.pb4.polymer.resourcepack.api.PolymerModelData;
//import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
//import net.minecraft.client.item.TooltipContext;
//import net.minecraft.entity.Entity;
//import net.minecraft.entity.player.PlayerEntity;
//import net.minecraft.item.Item;
//import net.minecraft.item.ItemStack;
//import net.minecraft.item.Items;
//import net.minecraft.nbt.NbtCompound;
//import net.minecraft.server.network.ServerPlayerEntity;
//import net.minecraft.sound.SoundCategory;
//import net.minecraft.sound.SoundEvents;
//import net.minecraft.text.MutableText;
//import net.minecraft.text.Text;
//import net.minecraft.util.Formatting;
//import net.minecraft.util.Hand;
//import net.minecraft.util.Identifier;
//import net.minecraft.util.TypedActionResult;
//import net.minecraft.world.World;
//import net.minecraft.world.event.GameEvent;
//import org.jetbrains.annotations.Nullable;
//
//import java.util.HashMap;
//import java.util.List;
//
//public class ModularFishingRodItem extends Item implements PolymerItem {
//    public static final String ROD_KEY = "Rod";
//    public static final String REEL_KEY = "Reel";
//    public static final String HOOK_KEY = "Hook";
//    public static final String CAST_KEY = "Cast";
//    public static final String CREATOR_KEY = "Creator";
//
//    public HashMap<String, PolymerModelData> modelData = new HashMap<>();
//
//    public ModularFishingRodItem(Item.Settings settings) {
//        super(settings);
//    }
//
//    @Override
//    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
//        ItemStack itemStack = user.getStackInHand(hand);
//        NbtCompound nbt = itemStack.getOrCreateNbt();
//        // You must have a rod, reel and hook to cast your rod
//        if (nbt.getString(ROD_KEY).isEmpty() || nbt.getString(REEL_KEY).isEmpty() || nbt.getString(HOOK_KEY).isEmpty())
//            return TypedActionResult.pass(itemStack);
//        // Check if a minigame bobber currently exists
//        if (((PlayerEntityAccess) user).getMinigameBobber() != null) {
//            MinigameFishingBobberEntity bobber = ((PlayerEntityAccess) user).getMinigameBobber();
//            bobber.useRod(itemStack);
//            return TypedActionResult.success(itemStack);
//        }
//        // Check if a regular bobber exists
//        if (user.fishHook != null) {
//            world.playSound(
//                    null,
//                    user.getX(),
//                    user.getY(),
//                    user.getZ(),
//                    SoundEvents.ENTITY_FISHING_BOBBER_RETRIEVE,
//                    SoundCategory.NEUTRAL,
//                    1.0F,
//                    0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F)
//            );
//            user.emitGameEvent(GameEvent.ITEM_INTERACT_FINISH);
//            user.fishHook.use(itemStack);
//            nbt.putBoolean(CAST_KEY, false);
//        } else {
//            world.playSound(
//                    null,
//                    user.getX(),
//                    user.getY(),
//                    user.getZ(),
//                    SoundEvents.ENTITY_FISHING_BOBBER_THROW,
//                    SoundCategory.NEUTRAL,
//                    0.5F,
//                    0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F)
//            );
//            user.emitGameEvent(GameEvent.ITEM_INTERACT_START);
//            world.spawnEntity(new ModularFishingBobberEntity(user, world));
//            nbt.putBoolean(CAST_KEY, true);
//        }
//        return TypedActionResult.success(itemStack);
//    }
//
//    @Override
//    public void appendTooltip(ItemStack itemStack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
//        if (!itemStack.hasNbt())
//            return;
//        NbtCompound nbt = itemStack.getOrCreateNbt();
//        // List components in tooltip
//        if (!nbt.getString(ModularFishingRodItem.ROD_KEY).isEmpty()) {
//            Identifier rodId = new Identifier(nbt.getString(ModularFishingRodItem.ROD_KEY));
//            MutableText rodText = Text.translatable(Barracuda.getItemFromRod(rodId).getTranslationKey());
//            tooltip.add(Text.translatable("rod.rod", rodText).formatted(Formatting.GRAY));
//        }
//        if (!nbt.getString(ModularFishingRodItem.REEL_KEY).isEmpty()) {
//            Identifier reelId = new Identifier(nbt.getString(ModularFishingRodItem.REEL_KEY));
//            MutableText reelText = Text.translatable(Barracuda.getItemFromReel(reelId).getTranslationKey());
//            tooltip.add(Text.translatable("rod.reel", reelText).formatted(Formatting.GRAY));
//        }
//        if (!nbt.getString(ModularFishingRodItem.HOOK_KEY).isEmpty()) {
//            Identifier hookId = new Identifier(nbt.getString(ModularFishingRodItem.HOOK_KEY));
//            MutableText hookText = Text.translatable(Barracuda.getItemFromHook(hookId).getTranslationKey());
//            tooltip.add(Text.translatable("rod.hook", hookText).formatted(Formatting.GRAY));
//        }
//        // Show creator in tooltip
//        String creator = nbt.getString(CREATOR_KEY);
//        if (!creator.isEmpty())
//            tooltip.add(Text.translatable("rod.byCreator", creator).formatted(Formatting.GRAY));
//    }
//
//    @Override
//    public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
//        return Items.FISHING_ROD;
//    }
//
//    @Override
//    public int getPolymerCustomModelData(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
//        if (!itemStack.hasNbt())
//            return -1;
//        NbtCompound nbt = itemStack.getOrCreateNbt();
//        boolean cast = nbt.getBoolean(CAST_KEY);
//        // Rod key should always be present, hopefully...
//        Identifier rodId = new Identifier(nbt.getString(ROD_KEY));
//        Identifier reelId = null;
//        Identifier hookId = null;
//        // Reel and hook keys may not be present or may be empty
//        String reelString = nbt.getString(REEL_KEY);
//        if (!reelString.isEmpty()) {
//            reelId = new Identifier(reelString);
//            // If the reel exists then there might be a hook
//            String hookString = nbt.getString(HOOK_KEY);
//            if (!hookString.isEmpty() && !cast)
//                hookId = new Identifier(hookString);
//        }
//        String id = Barracuda.convertComponentsIdsToRodId(rodId, reelId, hookId);
//        if (cast)
//            id += "_cast";
//        if (!modelData.containsKey(id))
//            return -1;
//        return modelData.get(id).value();
//    }
//
//    public static boolean attemptToResetCastState(ItemStack stack) {
//        if (!stack.isOf(ReelItems.MODULAR_FISHING_ROD_ITEM))
//            return false;
//        if (!stack.getOrCreateNbt().getBoolean(ModularFishingRodItem.CAST_KEY))
//            return false;
//        stack.getOrCreateNbt().putBoolean(ModularFishingRodItem.CAST_KEY, false);
//        return true;
//    }
//
//    public static ItemStack getStackWithComponents(Item rodItem, Item reelItem, Item hookItem) {
//        ItemStack rodStack = new ItemStack(ReelItems.MODULAR_FISHING_ROD_ITEM);
//        // Copy components to rod NBT
//        NbtCompound nbt = rodStack.getOrCreateNbt();
//        nbt.putString(ModularFishingRodItem.ROD_KEY, Barracuda.getRodFromItem(rodItem).toString());
//        nbt.putString(ModularFishingRodItem.REEL_KEY, Barracuda.getReelFromItem(reelItem).toString());
//        nbt.putString(ModularFishingRodItem.HOOK_KEY, Barracuda.getHookFromItem(hookItem).toString());
//        return rodStack;
//    }
//
//    public void registerModels() {
//        modelData.clear();
//        for (Identifier rodId : Barracuda.ROD_TYPES.keySet()) {
//            registerModel(rodId, null, null);
//            for (Identifier reelId : Barracuda.REEL_TYPES.keySet()) {
//                registerModel(rodId, reelId, null);
//                registerModel(rodId, reelId, null, true);
//                for (Identifier hookId : Barracuda.HOOK_TYPES.keySet()) {
//                    registerModel(rodId, reelId, hookId);
//                }
//            }
//        }
//    }
//
//    public void registerModel(Identifier rodId, Identifier reelId, Identifier hookId) {
//        registerModel(rodId, reelId, hookId, false);
//    }
//
//    public void registerModel(Identifier rodId, Identifier reelId, Identifier hookId, boolean castVariant) {
//        String id = Barracuda.convertComponentsIdsToRodId(rodId, reelId, hookId);
//        if (castVariant)
//            id += "_cast";
//        modelData.put(id, PolymerResourcePackUtils.requestModel(Items.FISHING_ROD, Barracuda.id("item/%s".formatted(id))));
//    }
//}

package net.infugogr.barracuda.item;


import net.infugogr.barracuda.fluid.ModFluids;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidDrainable;
import net.minecraft.block.FluidFillable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;


public class CapsuleItem extends BucketItem{
    public Fluid fluid;

    public CapsuleItem(Fluid fluid, Settings settings) {
        super(fluid, settings);
        this.fluid = fluid;
    }
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        BlockHitResult blockHitResult = raycast(world, user, this.fluid == Fluids.EMPTY ? RaycastContext.FluidHandling.SOURCE_ONLY : RaycastContext.FluidHandling.NONE);
        if (blockHitResult.getType() == HitResult.Type.MISS) {
            return TypedActionResult.pass(itemStack);
        } else if (blockHitResult.getType() != HitResult.Type.BLOCK) {
            return TypedActionResult.pass(itemStack);
        } else {
            BlockPos blockPos = blockHitResult.getBlockPos();
            Direction direction = blockHitResult.getSide();
            BlockPos blockPos2 = blockPos.offset(direction);
            if (world.canPlayerModifyAt(user, blockPos) && user.canPlaceOn(blockPos2, direction, itemStack)) {
                if (this.fluid == Fluids.EMPTY) {
                    BlockState blockState = world.getBlockState(blockPos);
                    Block itemStack2 = blockState.getBlock();
                    if (itemStack2 instanceof FluidDrainable fluidDrainable) {
                        ItemStack itemStack21 = fluidDrainable.tryDrainFluid(user, world, blockPos, blockState);
                        if (!itemStack21.isEmpty()) {
                            user.incrementStat(Stats.USED.getOrCreateStat(this));
                            fluidDrainable.getBucketFillSound().ifPresent((sound) -> user.playSound(sound, 1.0F, 1.0F));
                            world.emitGameEvent(user, GameEvent.FLUID_PICKUP, blockPos);
                            if(itemStack21.getItem() == Items.WATER_BUCKET){
                                itemStack21 = ModItems.WATER_CAPSULE.getDefaultStack();
                            }
                            if(itemStack21.getItem() == ModItems.CRUDE_OIL_BUCKET){
                                itemStack21 = ModItems.CRUDE_OIL_CAPSULE.getDefaultStack();
                            }
                            if(itemStack21.getItem() == ModItems.HEAVY_OIL_BUCKET){
                                itemStack21 = ModItems.HEAVY_OIL_CAPSULE.getDefaultStack();
                            }
                            if(itemStack21.getItem() == ModItems.DIESEL_BUCKET){
                                itemStack21 = ModItems.DIESEL_CAPSULE.getDefaultStack();
                            }
                            if(itemStack21.getItem() == Items.LAVA_BUCKET){
                                itemStack21 = ModItems.LAVA_CAPSULE.getDefaultStack();
                            }
                            ItemStack itemStack3 = ItemUsage.exchangeStack(itemStack, user, itemStack21);
                            if (!world.isClient) {
                                Criteria.FILLED_BUCKET.trigger((ServerPlayerEntity)user, itemStack21);
                            }

                            return TypedActionResult.success(itemStack3, world.isClient());
                        }
                    }

                    return TypedActionResult.fail(itemStack);
                } else {
                    if (this.fluid == ModFluids.GAS.still()){
                        return TypedActionResult.pass(itemStack);
                    }
                    BlockState blockState = world.getBlockState(blockPos);
                    BlockPos blockPos3 = blockState.getBlock() instanceof FluidFillable && this.fluid == Fluids.WATER ? blockPos : blockPos2;
                    if (this.placeFluid(user, world, blockPos3, blockHitResult)) {
                        this.onEmptied(user, world, itemStack, blockPos3);
                        if (user instanceof ServerPlayerEntity) {
                            Criteria.PLACED_BLOCK.trigger((ServerPlayerEntity)user, blockPos3, itemStack);
                        }

                        user.incrementStat(Stats.USED.getOrCreateStat(this));
                        return TypedActionResult.success(getEmptiedStack(itemStack, user), world.isClient());
                    } else {
                        return TypedActionResult.fail(itemStack);
                    }
                }
            } else {
                return TypedActionResult.fail(itemStack);
            }
        }
    }

    public static ItemStack getEmptiedStack(ItemStack stack, PlayerEntity player) {
        return ItemUsage.exchangeStack(stack, player, ModItems.EMPTY_CAPSULE.getDefaultStack());
    }

    public void onEmptied(@Nullable PlayerEntity player, World world, ItemStack stack, BlockPos pos) {
    }

    @Override
    public boolean placeFluid(@Nullable PlayerEntity player, World world, BlockPos pos, @Nullable BlockHitResult hitResult) {
        BlockState blockState2 = world.getBlockState(pos);
        if (!(this.fluid instanceof FlowableFluid flowableFluid)) {
            return false;
        } else {
            Block block;
            boolean bl;
            boolean var10000;
            label82: {
                block = blockState2.getBlock();
                bl = blockState2.canBucketPlace(this.fluid);
                if (!blockState2.isAir() && !bl) {
                    label80: {
                        if (block instanceof FluidFillable fluidFillable) {
                            if (fluidFillable.canFillWithFluid(player, world, pos, blockState2, this.fluid)) {
                                break label80;
                            }
                        }

                        var10000 = false;
                        break label82;
                    }
                }

                var10000 = true;
            }

            boolean bl2 = var10000;
            if (!bl2) {
                return hitResult != null && this.placeFluid(player, world, hitResult.getBlockPos().offset(hitResult.getSide()), null);
            } else if (world.getDimension().ultrawarm() && this.fluid.isIn(FluidTags.WATER)) {
                int i = pos.getX();
                int j = pos.getY();
                int k = pos.getZ();
                world.playSound(player, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (world.random.nextFloat() - world.random.nextFloat()) * 0.8F);

                for(int l = 0; l < 8; ++l) {
                    world.addParticle(ParticleTypes.LARGE_SMOKE, (double)i + Math.random(), (double)j + Math.random(), (double)k + Math.random(), 0.0F, 0.0F, 0.0F);
                }

                return true;
            } else {
                if (block instanceof FluidFillable fluidFillable) {
                    if (this.fluid == Fluids.WATER) {
                        fluidFillable.tryFillWithFluid(world, pos, blockState2, flowableFluid.getStill(false));
                        this.playEmptyingSound(player, world, pos);
                        return true;
                    }
                }

                if (!world.isClient && bl && !blockState2.isLiquid()) {
                    world.breakBlock(pos, true);
                }

                if (!world.setBlockState(pos, this.fluid.getDefaultState().getBlockState(), 11) && !blockState2.getFluidState().isStill()) {
                    return false;
                } else {
                    this.playEmptyingSound(player, world, pos);
                    return true;
                }
            }
        }
    }

    protected void playEmptyingSound(@Nullable PlayerEntity player, WorldAccess world, BlockPos pos) {
        SoundEvent soundEvent = this.fluid.isIn(FluidTags.LAVA) ? SoundEvents.ITEM_BUCKET_EMPTY_LAVA : SoundEvents.ITEM_BUCKET_EMPTY;
        world.playSound(player, pos, soundEvent, SoundCategory.BLOCKS, 1.0F, 1.0F);
        world.emitGameEvent(player, GameEvent.FLUID_PLACE, pos);
    }

    public Fluid getFluid() {
        return this.fluid;
    }
    public ItemStack getFilledVariant(Fluid fluid) {
        if (fluid == Fluids.WATER){
            return ModItems.WATER_CAPSULE.getDefaultStack();
        }
        if (fluid == Fluids.LAVA){
            return ModItems.LAVA_CAPSULE.getDefaultStack();
        }
        if (fluid == ModFluids.CRUDE_OIL.still()){
            return ModItems.CRUDE_OIL_CAPSULE.getDefaultStack();
        }
        if (fluid == ModFluids.GAS.still()){
            return ModItems.GAS_CAPSULE.getDefaultStack();
        }
        if (fluid == ModFluids.DIESEL.still()){
            return ModItems.DIESEL_CAPSULE.getDefaultStack();
        }
        if (fluid == ModFluids.HEAVY_OIL.still()){
            return ModItems.HEAVY_OIL_CAPSULE.getDefaultStack();
        }
        return ModItems.EMPTY_CAPSULE.getDefaultStack();
    }
}

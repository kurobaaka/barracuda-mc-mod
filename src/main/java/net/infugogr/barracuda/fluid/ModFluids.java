package net.infugogr.barracuda.fluid;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.infugogr.barracuda.Barracuda;
import net.infugogr.barracuda.block.ModBlocks;
import net.infugogr.barracuda.item.ModItems;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.*;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import java.util.concurrent.atomic.AtomicReference;

public class ModFluids {
    public static final FluidRegistryObject CRUDE_OIL = registerFluid("crude_oil");
    public static final FluidRegistryObject HEAVY_OIL = registerFluid("heavy_oil");
    public static final FluidRegistryObject DIESEL = registerFluid("diesel");
    public static final FluidRegistryObject GAS = registerFluid("gas");

    public static FluidRegistryObject registerFluid(String name) {
        final AtomicReference<BarracudaFluid.Still> still = new AtomicReference<>();
        final AtomicReference<BarracudaFluid.Flowing> flowing = new AtomicReference<>();
        final AtomicReference<BucketItem> bucket = new AtomicReference<>();
        final AtomicReference<FluidBlock> block = new AtomicReference<>();

        still.set(register(name, new BarracudaFluid.Still(still::get, flowing::get, bucket::get, block::get)));
        flowing.set(register("flowing_" + name, new BarracudaFluid.Flowing(still::get, flowing::get, bucket::get, block::get)));
        bucket.set(ModItems.register(name + "_bucket", settings -> new BucketItem(still.get(), settings), settings -> settings.maxCount(1).recipeRemainder(Items.BUCKET)));
        block.set(ModBlocks.registerFluidBlock(name, new FluidBlock(still.get(), FabricBlockSettings.copyOf(Blocks.WATER))));

        return new FluidRegistryObject(still.get(), flowing.get(), bucket.get(), block.get());
    }

    private static <T extends Fluid> T registerFluidVariant(String id, T value) {
        return Registry.register(Registries.FLUID, id, value);
    }

    static {
        for (Fluid fluid : Registries.FLUID) {
            for (FluidState fluidState : fluid.getStateManager().getStates()) {
                Fluid.STATE_IDS.add(fluidState);
            }
        }
    }

    public static <T extends Fluid> T register(String name, T fluid) {
        return Registry.register(Registries.FLUID, Barracuda.id(name), fluid);
    }

    public static void register() {}
}

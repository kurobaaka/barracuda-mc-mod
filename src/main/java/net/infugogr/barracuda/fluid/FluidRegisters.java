package net.infugogr.barracuda.fluid;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributeHandler;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.infugogr.barracuda.util.ModTags;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class FluidRegisters {
    public static void register() {
        // Fluid Properties
        var commonFluidAttributes = new FluidVariantAttributeHandler() {
            @Override
            public int getViscosity(FluidVariant variant, @Nullable World world) {
                return 7500;
            }
        };

        FluidVariantAttributes.register(ModFluids.CRUDE_OIL.still(), commonFluidAttributes);
        FluidVariantAttributes.register(ModFluids.CRUDE_OIL.flowing(), commonFluidAttributes);

        FluidVariantAttributes.register(ModFluids.HEAVY_OIL.still(), commonFluidAttributes);
        FluidVariantAttributes.register(ModFluids.HEAVY_OIL.flowing(), commonFluidAttributes);

        FluidVariantAttributes.register(ModFluids.DIESEL.still(), commonFluidAttributes);
        FluidVariantAttributes.register(ModFluids.DIESEL.flowing(), commonFluidAttributes);

        FluidVariantAttributes.register(ModFluids.GAS.still(), commonFluidAttributes);
        FluidVariantAttributes.register(ModFluids.GAS.flowing(), commonFluidAttributes);

        // Fluid Data
        var commonFluidData = new FluidData.Builder(ModTags.Fluids.CRUDE_OIL)
                .preventsBlockSpreading()
                .canSwim()
                .fluidMovementSpeed((entity, speed) -> 0.01F)
                .applyWaterMovement()
                .applyBuoyancy(itemEntity -> itemEntity.setVelocity(itemEntity.getVelocity().add(0.0D, 0.01D, 0.0D)))
                .canCauseDrowning()
                .shouldWitchDrinkWaterBreathing()
                .affectsBlockBreakSpeed()
                .bubbleParticle(ParticleTypes.ASH)
                .splashParticle(ParticleTypes.HEART)
                .build();

        var commonFluidData2 = new FluidData.Builder(ModTags.Fluids.HEAVY_OIL)
                .preventsBlockSpreading()
                .canSwim()
                .fluidMovementSpeed((entity, speed) -> 0.01F)
                .applyWaterMovement()
                .applyBuoyancy(itemEntity -> itemEntity.setVelocity(itemEntity.getVelocity().add(0.0D, 0.01D, 0.0D)))
                .canCauseDrowning()
                .shouldWitchDrinkWaterBreathing()
                .affectsBlockBreakSpeed()
                .bubbleParticle(ParticleTypes.ASH)
                .splashParticle(ParticleTypes.HEART)
                .build();

        var commonFluidData3 = new FluidData.Builder(ModTags.Fluids.DIESEL)
                .preventsBlockSpreading()
                .canSwim()
                .fluidMovementSpeed((entity, speed) -> 0.01F)
                .applyWaterMovement()
                .applyBuoyancy(itemEntity -> itemEntity.setVelocity(itemEntity.getVelocity().add(0.0D, 0.01D, 0.0D)))
                .canCauseDrowning()
                .shouldWitchDrinkWaterBreathing()
                .affectsBlockBreakSpeed()
                .bubbleParticle(ParticleTypes.ASH)
                .splashParticle(ParticleTypes.HEART)
                .build();

        var commonFluidData4 = new FluidData.Builder(ModTags.Fluids.GAS)
                .preventsBlockSpreading()
                .canSwim()
                .fluidMovementSpeed((entity, speed) -> 0.01F)
                .applyWaterMovement()
                .applyBuoyancy(itemEntity -> itemEntity.setVelocity(itemEntity.getVelocity().add(0.0D, 0.01D, 0.0D)))
                .canCauseDrowning()
                .shouldWitchDrinkWaterBreathing()
                .affectsBlockBreakSpeed()
                .bubbleParticle(ParticleTypes.ASH)
                .splashParticle(ParticleTypes.HEART)
                .build();

        FluidData.registerFluidData(ModFluids.CRUDE_OIL.still(), commonFluidData);
        FluidData.registerFluidData(ModFluids.CRUDE_OIL.flowing(), commonFluidData);

        FluidData.registerFluidData(ModFluids.HEAVY_OIL.still(), commonFluidData2);
        FluidData.registerFluidData(ModFluids.HEAVY_OIL.flowing(), commonFluidData2);

        FluidData.registerFluidData(ModFluids.DIESEL.still(), commonFluidData3);
        FluidData.registerFluidData(ModFluids.DIESEL.flowing(), commonFluidData3);

        FluidData.registerFluidData(ModFluids.GAS.still(), commonFluidData4);
        FluidData.registerFluidData(ModFluids.GAS.flowing(), commonFluidData4);
    }
}

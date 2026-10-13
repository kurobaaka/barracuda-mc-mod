package net.infugogr.barracuda.mixin;

import net.infugogr.barracuda.fluid.ModFluids;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.registry.tag.TagKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WorldRenderer.class)
public abstract class CrudeOilRainMixin {
    @Redirect(
            method = "tickRainSplashing",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/fluid/FluidState;isIn(Lnet/minecraft/registry/tag/TagKey;)Z")
    )
    private boolean barracuda$avoidLavaRainParticles(FluidState fluidState, TagKey<Fluid> tag) {
        if (tag.equals(FluidTags.LAVA)
                && fluidState.getFluid().matchesType(ModFluids.CRUDE_OIL.still())) {
            return false;
        }
        return fluidState.isIn(tag);
    }
}

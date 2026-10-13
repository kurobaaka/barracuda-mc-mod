package net.infugogr.barracuda.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.infugogr.barracuda.fluid.ModFluids;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BackgroundRenderer.class)
public abstract class CrudeOilFogMixin {
    @Inject(method = "applyFog", at = @At("TAIL"))
    private static void barracuda$hideViewInCrudeOil(Camera camera, BackgroundRenderer.FogType fogType,
                                                      float viewDistance, boolean thickFog, float fogStart,
                                                      CallbackInfo ci) {
        if (camera.getFocusedEntity() == null) {
            return;
        }

        World world = camera.getFocusedEntity().getWorld();
        BlockPos cameraPos = camera.getBlockPos();
        FluidState fluidState = world.getFluidState(cameraPos);
        if (!fluidState.getFluid().matchesType(ModFluids.CRUDE_OIL.still())) {
            return;
        }

        RenderSystem.setShaderFogColor(0.0F, 0.0F, 0.0F);
    }
}

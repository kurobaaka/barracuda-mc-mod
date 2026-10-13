package net.infugogr.barracuda.mixin;

import net.infugogr.barracuda.fluid.ModFluids;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class CrudeOilDrowningMixin {
    @Inject(method = "isSubmergedIn", at = @At("RETURN"), cancellable = true)
    private void barracuda$useWaterDrowningInCrudeOil(TagKey<Fluid> tag, CallbackInfoReturnable<Boolean> cir) {
        Entity entity = (Entity) (Object) this;
        if (!(entity instanceof LivingEntity) || !tag.equals(FluidTags.WATER)) {
            return;
        }

        Vec3d eyePos = entity.getEyePos();
        if (entity.getWorld().getFluidState(BlockPos.ofFloored(eyePos)).getFluid()
                .matchesType(ModFluids.CRUDE_OIL.still())) {
            cir.setReturnValue(true);
        }
    }
}

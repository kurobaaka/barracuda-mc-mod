package net.infugogr.barracuda.mixin;

import net.infugogr.barracuda.fluid.CrudeOilUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class CrudeOilEntityMixin {
    @Inject(method = "tickMovement", at = @At("HEAD"))
    private void barracuda$preventRisingInCrudeOil(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (!CrudeOilUtil.isTouching(entity)) {
            return;
        }
        Vec3d velocity = entity.getVelocity();
        if (velocity.y > 0.0D) {
            entity.setVelocity(velocity.x, 0.0D, velocity.z);
        }
    }

    @Inject(method = "swimUpward", at = @At("HEAD"), cancellable = true)
    private void barracuda$preventJumpInCrudeOil(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (CrudeOilUtil.isTouching(entity)) {
            ci.cancel();
        }
    }
}

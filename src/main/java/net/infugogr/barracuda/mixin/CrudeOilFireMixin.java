package net.infugogr.barracuda.mixin;

import net.infugogr.barracuda.fluid.CrudeOilUtil;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class CrudeOilFireMixin {
    @Inject(method = "setOnFireFromLava", at = @At("HEAD"), cancellable = true)
    private void barracuda$preventLavaDamageInCrudeOil(CallbackInfo ci) {
        if (CrudeOilUtil.isTouching((Entity) (Object) this)) {
            ci.cancel();
        }
    }
}

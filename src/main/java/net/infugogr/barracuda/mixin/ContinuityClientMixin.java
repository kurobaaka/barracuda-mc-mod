package net.infugogr.barracuda.mixin;

import me.pepperbell.continuity.client.ContinuityClient;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.infugogr.barracuda.Barracuda.id;

@Mixin(value = ContinuityClient.class, remap = false)
public class ContinuityClientMixin {
    @Inject(method = "<init>", at = @At("HEAD"))
    private static void injectAtCtor(CallbackInfo ci) {
        FabricLoader.getInstance().getModContainer("barracuda").ifPresent(container -> {
            ResourceManagerHelper.registerBuiltinResourcePack(
                    id("connected"),
                    container,
                    Text.translatable("resourcePack.barracuda.connected.name"),
                    ResourcePackActivationType.DEFAULT_ENABLED
            );
        });
    }
}

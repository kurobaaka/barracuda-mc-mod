package net.infugogr.barracuda.mixin;

import net.minecraft.client.color.item.ItemColors;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ItemColors.class)
public class ItemColorsMixin {
	/*@Inject(method = "create", at = @At("RETURN"))
	private static void injectLiveCapsuleColors(BlockColors blockColors, CallbackInfoReturnable<ItemColors> cir) {
		ItemColors itemColors = cir.getReturnValue();

		for (LiveCapsule liveCapsule : LiveCapsule.getAll()) {
			itemColors.register((stack, tintIndex) -> liveCapsule.getColor(tintIndex), liveCapsule);
		}
	}*/
}
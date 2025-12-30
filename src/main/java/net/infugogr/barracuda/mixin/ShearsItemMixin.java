package net.infugogr.barracuda.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.item.ShearsItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShearsItem.class)
public abstract class ShearsItemMixin {

    /**
     * Этот метод добавляет в класс ShearsItem метод getRecipeRemainder.
     * Он возвращает тот же предмет, но с повреждением на 1 больше (прочность уменьшается на 1).
     */
    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(CallbackInfo ci) {
        // Этот блок инициализации нужен, чтобы убедиться, что миксин подмешивается.
    }

    // Здесь реализуем наш метод
    public ItemStack getRecipeRemainder(ItemStack stack) {
        // Создаем копию предмета
        ItemStack result = stack.copy();
        int damage = result.getDamage();
        int maxDamage = result.getMaxDamage();

        // Уменьшаем прочность на 1 (ожидается увеличение повреждения)
        if (damage < maxDamage) {
            result.setDamage(damage + 1);
        }
        return result;
    }
}

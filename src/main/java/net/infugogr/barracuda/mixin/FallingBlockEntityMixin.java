package net.infugogr.barracuda.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.registry.tag.BlockTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(FallingBlockEntity.class)
public class FallingBlockEntityMixin {
    @Shadow
    private BlockState block;

    @ModifyConstant(method = "handleFallDamage", constant = @Constant(floatValue = 1.0F))
    private float includeFirstBlockInAnvilFallDamage(float offset) {
        return block.isIn(BlockTags.ANVIL) ? 0.0F : offset;
    }

    @ModifyConstant(
        method = "handleFallDamage",
        constant = @Constant(floatValue = 0.05F, ordinal = 0)
    )
    private float removeAnvilBaseDamageChance(float chance) {
        return block.isIn(BlockTags.ANVIL) ? 0.0F : chance;
    }

    @ModifyConstant(
        method = "handleFallDamage",
        constant = @Constant(floatValue = 0.05F, ordinal = 1)
    )
    private float setAnvilDamageChancePerBlock(float chance) {
        return block.isIn(BlockTags.ANVIL) ? 0.01F : chance;
    }
}

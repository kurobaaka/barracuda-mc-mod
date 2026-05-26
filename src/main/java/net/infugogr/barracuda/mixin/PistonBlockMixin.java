package net.infugogr.barracuda.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PistonBlock.class)
public class PistonBlockMixin {

    @Inject(
        method = "isMovable",
        at = @At("RETURN"),
        cancellable = true
    )
    private static void allowAnvilMovement(
            BlockState state,
            World world,
            BlockPos pos,
            Direction direction,
            boolean canBreak,
            Direction pistonDir,
            CallbackInfoReturnable<Boolean> cir
    ) {

        if (cir.getReturnValue()) return;

        if (state.isOf(Blocks.ANVIL)
                || state.isOf(Blocks.CHIPPED_ANVIL)
                || state.isOf(Blocks.DAMAGED_ANVIL)) {

            cir.setReturnValue(true);
        }
    }
}
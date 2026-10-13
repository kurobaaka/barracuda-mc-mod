package net.infugogr.barracuda.fluid;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;

public final class CrudeOilUtil {
    private CrudeOilUtil() {
    }

    public static boolean isTouching(Entity entity) {
        Box box = entity.getBoundingBox();
        BlockPos.Mutable pos = new BlockPos.Mutable();

        for (int x = MathHelper.floor(box.minX); x <= MathHelper.floor(box.maxX - 1.0E-7D); x++) {
            for (int y = MathHelper.floor(box.minY); y <= MathHelper.floor(box.maxY - 1.0E-7D); y++) {
                for (int z = MathHelper.floor(box.minZ); z <= MathHelper.floor(box.maxZ - 1.0E-7D); z++) {
                    if (entity.getWorld().getFluidState(pos.set(x, y, z)).getFluid()
                            .matchesType(ModFluids.CRUDE_OIL.still())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}

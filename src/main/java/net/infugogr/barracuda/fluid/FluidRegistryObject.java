package net.infugogr.barracuda.fluid;

import net.minecraft.block.FluidBlock;
import net.minecraft.item.BucketItem;

public record FluidRegistryObject(BarracudaFluid.Still still, BarracudaFluid.Flowing flowing, BucketItem bucket,
                                  FluidBlock block) {
}

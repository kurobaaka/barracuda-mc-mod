package net.infugogr.barracuda.fluid;

import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.infugogr.barracuda.Barracuda;

public class RenderFluidHandler {
    public static void register() {
        FluidRenderHandlerRegistry.INSTANCE.register(ModFluids.CRUDE_OIL.still(), ModFluids.CRUDE_OIL.flowing(),
                new SimpleFluidRenderHandler(Barracuda.id("block/crude_oil_still"), Barracuda.id("block/crude_oil_flow")));
    }
}
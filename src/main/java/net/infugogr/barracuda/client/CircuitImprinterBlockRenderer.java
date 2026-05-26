package net.infugogr.barracuda.client;

import net.infugogr.barracuda.block.entity.CircuitImprinterBlockEntity;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class CircuitImprinterBlockRenderer extends GeoBlockRenderer<CircuitImprinterBlockEntity> {
    public CircuitImprinterBlockRenderer(BlockEntityRendererFactory.Context context) {
        super(new CircuitImprinterBlockModel());
    }
}

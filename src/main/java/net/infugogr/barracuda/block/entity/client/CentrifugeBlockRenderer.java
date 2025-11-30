package net.infugogr.barracuda.block.entity.client;

import net.infugogr.barracuda.block.entity.CentrifugeBlockEntity;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class CentrifugeBlockRenderer extends GeoBlockRenderer<CentrifugeBlockEntity> {
    public CentrifugeBlockRenderer(BlockEntityRendererFactory.Context context) {
        super(new CentrifugeBlockModel());
    }
}

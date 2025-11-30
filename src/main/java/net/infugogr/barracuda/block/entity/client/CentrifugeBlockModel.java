package net.infugogr.barracuda.block.entity.client;

import net.infugogr.barracuda.Barracuda;
import net.infugogr.barracuda.block.entity.CentrifugeBlockEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class CentrifugeBlockModel extends GeoModel<CentrifugeBlockEntity> {
    @Override
    public Identifier getModelResource(CentrifugeBlockEntity animatable) {
        return Barracuda.id("geo/centrifuge.geo.json");
    }

    @Override
    public Identifier getTextureResource(CentrifugeBlockEntity animatable) {
        return Barracuda.id("textures/block/centrifuge.png");
    }

    @Override
    public Identifier getAnimationResource(CentrifugeBlockEntity animatable) {
        return Barracuda.id("animations/centrifuge.animation.json");
    }
}

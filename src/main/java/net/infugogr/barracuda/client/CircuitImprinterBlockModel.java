package net.infugogr.barracuda.client;

import net.infugogr.barracuda.Barracuda;
import net.infugogr.barracuda.block.entity.CircuitImprinterBlockEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class CircuitImprinterBlockModel extends GeoModel<CircuitImprinterBlockEntity> {
    @Override
    public Identifier getModelResource(CircuitImprinterBlockEntity animatable) {
        return Barracuda.id("geo/circuit_imprinter.geo.json");
    }

    @Override
    public Identifier getTextureResource(CircuitImprinterBlockEntity animatable) {
        return Barracuda.id("textures/block/circuit_imprinter.png");
    }

    @Override
    public Identifier getAnimationResource(CircuitImprinterBlockEntity animatable) {
        return Barracuda.id("animations/circuit_imprinter.animation.json");
    }
}

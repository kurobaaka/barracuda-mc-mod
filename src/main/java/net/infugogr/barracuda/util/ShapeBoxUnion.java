package net.infugogr.barracuda.util;

import java.util.List;

public class ShapeBoxUnion extends ShapeBox {

    public final List<ShapeBox> children;

    public ShapeBoxUnion(List<ShapeBox> boxes) {
        super(0,0,0,0,0,0); // неиспользуемые поля
        this.children = boxes;
    }
}
package net.infugogr.barracuda.util;

import net.minecraft.block.Block;
import net.minecraft.util.shape.VoxelShape;

import java.util.Arrays;

public class ShapeBox {

    public final int x1, y1, z1, x2, y2, z2;

    public ShapeBox(int x1, int y1, int z1, int x2, int y2, int z2) {
        this.x1 = x1; this.y1 = y1; this.z1 = z1;
        this.x2 = x2; this.y2 = y2; this.z2 = z2;
    }

    public static ShapeBox single(int x1, int y1, int z1, int x2, int y2, int z2) {
        return new ShapeBox(x1, y1, z1, x2, y2, z2);
    }

    public static ShapeBox union(ShapeBox... boxes) {
        // просто храним несколько коробок
        // union будет вычисляться в getOutlineShape()
        return new ShapeBoxUnion(Arrays.asList(boxes));
    }

    public VoxelShape toShape() {
        return Block.createCuboidShape(x1, y1, z1, x2, y2, z2);
    }
}

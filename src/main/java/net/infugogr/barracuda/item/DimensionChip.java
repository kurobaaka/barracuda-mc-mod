package net.infugogr.barracuda.item;

import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;

public class DimensionChip extends Item {
    private final RegistryKey<World> dimension;

    public DimensionChip(Settings settings, RegistryKey<World> dimension) {
        super(settings);
        this.dimension = dimension;
    }

    public RegistryKey<World> getDimension() {
        return this.dimension;
    }
}

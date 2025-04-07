package net.infugogr.barracuda.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.infugogr.barracuda.Barracuda;
import net.infugogr.barracuda.entity.custom.*;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {
    public static final EntityType<BassFishEntity> BASS_FISH = Registry.register(Registries.ENTITY_TYPE,
            new Identifier(Barracuda.MOD_ID, "bass_fish"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, BassFishEntity::new)
                    .dimensions(EntityDimensions.fixed(1f, 1f)).build());

    public static final EntityType<AzureSerpentEntity> AZURE_SERPENT = Registry.register(Registries.ENTITY_TYPE,
            new Identifier(Barracuda.MOD_ID, "azure_serpent"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, AzureSerpentEntity::new)
                    .dimensions(EntityDimensions.fixed(1f, 1f)).build());

    public static final EntityType<BarracudaEntity> BARRACUDA = Registry.register(Registries.ENTITY_TYPE,
            new Identifier(Barracuda.MOD_ID, "barracuda"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, BarracudaEntity::new)
                    .dimensions(EntityDimensions.fixed(1.5f, 1.75f)).build());

    public static final EntityType<AzureReaperEntity> AZUER_REAPER = Registry.register(Registries.ENTITY_TYPE,
            new Identifier(Barracuda.MOD_ID, "azure_reaper"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, AzureReaperEntity::new)
                    .dimensions(EntityDimensions.fixed(1.5f, 1.75f)).build());

    public static final EntityType<ModGiantEntity> GIANT = Registry.register(Registries.ENTITY_TYPE,
            new Identifier(Barracuda.MOD_ID, "giant"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, ModGiantEntity::new)
                    .dimensions(EntityDimensions.fixed(1.5f, 1.75f)).build());
    }


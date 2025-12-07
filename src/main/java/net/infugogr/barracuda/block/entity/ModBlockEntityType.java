package net.infugogr.barracuda.block.entity;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.infugogr.barracuda.Barracuda;
import net.infugogr.barracuda.block.CircuitImprinterBlock;
import net.infugogr.barracuda.block.ElectricSmelterBlock;
import net.infugogr.barracuda.block.ModBlocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntityType {
    public static final BlockEntityType<FuelGeneratorBlockEntity> FUEL_GENERATOR = register("fuel_generator",
            BlockEntityType.Builder.create(FuelGeneratorBlockEntity::new, ModBlocks.FUEL_GENERATOR)
                    .build());

    public static final BlockEntityType<LVcableBlockEntity> LVCABLE = register("lvcable",
            BlockEntityType.Builder.create(LVcableBlockEntity::new, ModBlocks.LVCABLE)
                    .build());

    public static final BlockEntityType<HVcableBlockEntity> HVCABLE = register("hvcable",
            BlockEntityType.Builder.create(HVcableBlockEntity::new, ModBlocks.HVCABLE)
                    .build());


    public static final BlockEntityType<MachineFrameBlockEntity> MACHINE_FRAME = register("machine_frame",
            BlockEntityType.Builder.create(MachineFrameBlockEntity::new, ModBlocks.MACHINE_FRAME)
                    .build());

    public static final BlockEntityType<SMESblockEntity> SMES = register("smes",
            BlockEntityType.Builder.create(SMESblockEntity::new, ModBlocks.SMES)
                    .build());

    public static final BlockEntityType<TeleporterBlockEntity> TELEPORTER = register("teleporter",
            BlockEntityType.Builder.create(TeleporterBlockEntity::new, ModBlocks.TELEPORTER)
                    .build());

    public static final BlockEntityType<OilRefineryBlockEntity> OIL_REFINERY = register("oil_refinery",
            BlockEntityType.Builder.create(OilRefineryBlockEntity::new, ModBlocks.OIL_REFINERY)
                    .build());

    public static final BlockEntityType<CrusherBlockEntity> CRUSHER = register("crusher",
            BlockEntityType.Builder.create(CrusherBlockEntity::new, ModBlocks.CRUSHER)
                    .build());

    public static final BlockEntityType<PounderBlockEntity> POUNDER = register("pounder",
            BlockEntityType.Builder.create(PounderBlockEntity::new, ModBlocks.POUNDER)
                    .build());

    public static final BlockEntityType<ElectricSmelterBlockEntity> ELECTRIC_SMELTER = register("electric_smelter",
            BlockEntityType.Builder.create(ElectricSmelterBlockEntity::new, ModBlocks.ELECTRIC_SMELTER)
                    .build());

    public static final BlockEntityType<CentrifugeBlockEntity> CENTRIFUGE = register("centrifuge",
            BlockEntityType.Builder.create(CentrifugeBlockEntity::new, ModBlocks.CENTRIFUGE)
                    .build());

    public static final BlockEntityType<WallLampBlockEntity> WALL_LAMP = register("wall_lamp",
            BlockEntityType.Builder.create(WallLampBlockEntity::new, ModBlocks.WALL_LAMP)
                    .build());

    public static final BlockEntityType<UraniumGeneratorBlockEntity> URANIUM_GENERATOR = register("uranium_generator",
            BlockEntityType.Builder.create(UraniumGeneratorBlockEntity::new, ModBlocks.URANIUM_GENERATOR)
                    .build());


    public static final BlockEntityType<FishingNetBlockEntity> FISHING_NET = register("fishing_net",
            BlockEntityType.Builder.create(FishingNetBlockEntity::new, ModBlocks.FISHING_NET)
                    .build());

    public static final BlockEntityType<ClosetBlockEntity> CLOSET = register("closet",
            BlockEntityType.Builder.create(ClosetBlockEntity::new, ModBlocks.CLOSET)
                    .build());

    public static final BlockEntityType<ShuttleWallBlockEntity> SHUTTLE_WALL = register("shuttle_wall",
            BlockEntityType.Builder.create(ShuttleWallBlockEntity::new, ModBlocks.SHUTTLE_WALL)
                    .build());

    public static final BlockEntityType<CircuitImprinterBlockEntity> CIRCUIT_IMPRINTER = register("circuit_imprinter",
            BlockEntityType.Builder.create(CircuitImprinterBlockEntity::new, ModBlocks.CIRCUIT_IMPRINTER)
                    .build());

    public static final BlockEntityType<ChemicalPlantBlockEntity> CHEMICAL_PLANT = register("chemical_plant",
            BlockEntityType.Builder.create(ChemicalPlantBlockEntity::new, ModBlocks.CHEMICAL_PLANT)
                    .build());

    public static final BlockEntityType<MultiBlockEntity> MULTIBLOCK = register("multiblock",
            BlockEntityType.Builder.create(MultiBlockEntity::new, ModBlocks.MULTIBLOCK)
                    .build());
                    
    public static <T extends BlockEntity> BlockEntityType<T> register(String name, BlockEntityType<T> type) {
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, Barracuda.id(name), type);
    }

    public static void registerModBlockEntityType() {
        Barracuda.LOGGER.info("Registering Mod Block Entity Types for " + Barracuda.MOD_ID);
    }
}


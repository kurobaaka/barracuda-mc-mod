package net.infugogr.barracuda;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.infugogr.barracuda.block.ModBlocks;
import net.infugogr.barracuda.block.entity.*;
import net.infugogr.barracuda.block.recipes.CentrifugeRecipeManager;
import net.infugogr.barracuda.block.recipes.ChemicalPlantRecipeManager;
import net.infugogr.barracuda.entity.ModEntities;
import net.infugogr.barracuda.entity.custom.AzureSerpentEntity;
import net.infugogr.barracuda.entity.custom.BarracudaEntity;
import net.infugogr.barracuda.entity.custom.BassFishEntity;
import net.infugogr.barracuda.entity.custom.ModGiantEntity;
import net.infugogr.barracuda.entity.effect.ModStatusEffects;
import net.infugogr.barracuda.fluid.FluidRegisters;
import net.infugogr.barracuda.fluid.ModFluids;
import net.infugogr.barracuda.item.ModItemGroups;
import net.infugogr.barracuda.item.ModItems;
import net.infugogr.barracuda.screenhandler.ModScreenHandlerType;
import net.infugogr.barracuda.sound.ModSounds;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.reborn.energy.api.EnergyStorage;

public class Barracuda implements ModInitializer { 
	public static final String MOD_ID = "barracuda";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static Text containerTitle(String name) {
		return Text.translatable("container." + MOD_ID + "." + name);
	}

	public void onInitialize() {
		LOGGER.info("Loading...");
		// Load registry classes
		ModItems.registerModItems();
		ModItemGroups.registerItemGroups();
		ModBlocks.registerModBlocks();
		ModStatusEffects.registerEffects();
		ModScreenHandlerType.registerModScreenHandlerType();
		ModBlockEntityType.registerModBlockEntityType();
		ModSounds.registerSounds();
		ModFluids.register();
        FluidRegisters.register();
        CentrifugeRecipeManager.register();
        ChemicalPlantRecipeManager.register();
		

		// Item Lookup
		EnergyStorage.SIDED.registerForBlockEntity(WallLampBlockEntity::getEnergyProvider, ModBlockEntityType.WALL_LAMP);

		EnergyStorage.SIDED.registerForBlockEntity(FuelGeneratorBlockEntity::getEnergyProvider, ModBlockEntityType.FUEL_GENERATOR);
		ItemStorage.SIDED.registerForBlockEntity(FuelGeneratorBlockEntity::getInventoryProvider, ModBlockEntityType.FUEL_GENERATOR);

        EnergyStorage.SIDED.registerForBlockEntity(CentrifugeBlockEntity::getEnergyProvider, ModBlockEntityType.CENTRIFUGE);
        ItemStorage.SIDED.registerForBlockEntity(CentrifugeBlockEntity::getInventoryProvider, ModBlockEntityType.CENTRIFUGE);

		EnergyStorage.SIDED.registerForBlockEntity(UraniumGeneratorBlockEntity::getEnergyProvider, ModBlockEntityType.URANIUM_GENERATOR);
		ItemStorage.SIDED.registerForBlockEntity(UraniumGeneratorBlockEntity::getInventoryProvider, ModBlockEntityType.URANIUM_GENERATOR);

		EnergyStorage.SIDED.registerForBlockEntity(CrusherBlockEntity::getEnergyProvider, ModBlockEntityType.CRUSHER);
		ItemStorage.SIDED.registerForBlockEntity(CrusherBlockEntity::getInventoryProvider, ModBlockEntityType.CRUSHER);

		EnergyStorage.SIDED.registerForBlockEntity(ElectricSmelterBlockEntity::getEnergyProvider, ModBlockEntityType.ELECTRIC_SMELTER);
		ItemStorage.SIDED.registerForBlockEntity(ElectricSmelterBlockEntity::getInventoryProvider, ModBlockEntityType.ELECTRIC_SMELTER);

		ItemStorage.SIDED.registerForBlockEntity(FishingNetBlockEntity::getInventoryProvider, ModBlockEntityType.FISHING_NET);

		EnergyStorage.SIDED.registerForBlockEntity(SMESblockEntity::getEnergyProvider, ModBlockEntityType.SMES);
		ItemStorage.SIDED.registerForBlockEntity(SMESblockEntity::getInventoryProvider, ModBlockEntityType.SMES);

		EnergyStorage.SIDED.registerForBlockEntity(OilRefineryBlockEntity::getEnergyProvider, ModBlockEntityType.OIL_REFINERY);
		ItemStorage.SIDED.registerForBlockEntity(OilRefineryBlockEntity::getInventoryProvider, ModBlockEntityType.OIL_REFINERY);
		FluidStorage.SIDED.registerForBlockEntity(OilRefineryBlockEntity::getFluidProvider, ModBlockEntityType.OIL_REFINERY);

        EnergyStorage.SIDED.registerForBlockEntity(ChemicalPlantBlockEntity::getEnergyProvider, ModBlockEntityType.CHEMICAL_PLANT);
        ItemStorage.SIDED.registerForBlockEntity(ChemicalPlantBlockEntity::getInventoryProvider, ModBlockEntityType.CHEMICAL_PLANT);
        FluidStorage.SIDED.registerForBlockEntity(ChemicalPlantBlockEntity::getFluidProvider, ModBlockEntityType.CHEMICAL_PLANT);

		EnergyStorage.SIDED.registerForBlockEntity(TeleporterBlockEntity::getEnergyProvider, ModBlockEntityType.TELEPORTER);
		ItemStorage.SIDED.registerForBlockEntity(TeleporterBlockEntity::getInventoryProvider, ModBlockEntityType.TELEPORTER);

        EnergyStorage.SIDED.registerForBlockEntity(CircuitImprinterBlockEntity::getEnergyProvider, ModBlockEntityType.CIRCUIT_IMPRINTER);
        ItemStorage.SIDED.registerForBlockEntity(CircuitImprinterBlockEntity::getInventoryProvider, ModBlockEntityType.CIRCUIT_IMPRINTER);


		EnergyStorage.SIDED.registerForBlockEntity(ShuttleWallBlockEntity::getEnergyProvider, ModBlockEntityType.SHUTTLE_WALL);
		EnergyStorage.SIDED.registerForBlockEntity(LVcableBlockEntity::getEnergyProvider, ModBlockEntityType.LVCABLE);
		EnergyStorage.SIDED.registerForBlockEntity(HVcableBlockEntity::getEnergyProvider, ModBlockEntityType.HVCABLE);
		ItemStorage.SIDED.registerForBlockEntity(ClosetBlockEntity::getInventoryProvider, ModBlockEntityType.CLOSET);

		FabricDefaultAttributeRegistry.register(ModEntities.GIANT, ModGiantEntity.createGiantAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.BASS_FISH, BassFishEntity.createBassFishAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.AZURE_SERPENT, AzureSerpentEntity.createAzureSerpentAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.BARRACUDA, BarracudaEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.AZUER_REAPER, BarracudaEntity.setAttributes());

		LOGGER.info("Loaded!");
	}
	public static Identifier id(String path) {
		return new Identifier(MOD_ID, path);
	}
}
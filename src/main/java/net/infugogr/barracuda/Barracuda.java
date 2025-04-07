package net.infugogr.barracuda;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.infugogr.barracuda.block.ModBlocks;
import net.infugogr.barracuda.block.entity.*;
import net.infugogr.barracuda.entity.ModEntities;
import net.infugogr.barracuda.entity.custom.AzureSerpentEntity;
import net.infugogr.barracuda.entity.custom.BarracudaEntity;
import net.infugogr.barracuda.entity.custom.BassFishEntity;
import net.infugogr.barracuda.entity.custom.ModGiantEntity;
import net.infugogr.barracuda.entity.effect.ModStatusEffects;
import net.infugogr.barracuda.item.ModItemGroups;
import net.infugogr.barracuda.item.ModItems;
import net.infugogr.barracuda.screenhandler.ModScreenHandlerType;
import net.infugogr.barracuda.sound.ModSounds;
import net.kyrptonaught.customportalapi.api.CustomPortalBuilder;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.reborn.energy.api.EnergyStorage;
// import net.infugogr.barracuda.registers.*;

public class Barracuda implements ModInitializer { 
	public static final String MOD_ID = "barracuda";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	// private static final ConfigRegister configReg = new ConfigRegister();
   

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
		// configReg.load();
		

		// Item Lookup
		EnergyStorage.SIDED.registerForBlockEntity(FuelGeneratorBlockEntity::getEnergyProvider, ModBlockEntityType.FUEL_GENERATOR);
		ItemStorage.SIDED.registerForBlockEntity(FuelGeneratorBlockEntity::getInventoryProvider, ModBlockEntityType.FUEL_GENERATOR);

		ItemStorage.SIDED.registerForBlockEntity(FishingNetBlockEntity::getInventoryProvider, ModBlockEntityType.FISHING_NET);

		EnergyStorage.SIDED.registerForBlockEntity(SMESblockEntity::getEnergyProvider, ModBlockEntityType.SMES);
		ItemStorage.SIDED.registerForBlockEntity(SMESblockEntity::getInventoryProvider, ModBlockEntityType.SMES);

		//EnergyStorage.SIDED.registerForBlockEntity(TeleporterBlockEntity::getEnergyProvider, ModBlockEntityType.TELEPORTER);
		//ItemStorage.SIDED.registerForBlockEntity(TeleporterBlockEntity::getInventoryProvider, ModBlockEntityType.TELEPORTER);

		EnergyStorage.SIDED.registerForBlockEntity(LVcableBlockEntity::getEnergyProvider, ModBlockEntityType.LVCABLE);

		EnergyStorage.SIDED.registerForBlockEntity(HVcableBlockEntity::getEnergyProvider, ModBlockEntityType.HVCABLE);

		FabricDefaultAttributeRegistry.register(ModEntities.GIANT, ModGiantEntity.createGiantAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.BASS_FISH, BassFishEntity.createBassFishAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.AZURE_SERPENT, AzureSerpentEntity.createAzureSerpentAttributes());


		// geco mobs
		FabricDefaultAttributeRegistry.register(ModEntities.BARRACUDA, BarracudaEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.AZUER_REAPER, BarracudaEntity.setAttributes());

		CustomPortalBuilder.beginPortal()
				.frameBlock(ModBlocks.CHARGED_REDSTONEIUM_BLOCK)
				.lightWithItem(ModItems.CAPACITOR)
				.destDimID(Barracuda.id("beta92"))
				.tintColor(0xc76efa)
				.registerPortal();

		LOGGER.info("Loaded!");
	}

	// public static ConfigRegister getConfigRegister(){
    //     return configReg;
    // }

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}
}
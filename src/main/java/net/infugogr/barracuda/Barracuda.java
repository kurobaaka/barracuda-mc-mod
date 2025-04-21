package net.infugogr.barracuda;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributeHandler;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.infugogr.barracuda.block.ModBlocks;
import net.infugogr.barracuda.block.entity.*;
import net.infugogr.barracuda.entity.ModEntities;
import net.infugogr.barracuda.entity.custom.AzureSerpentEntity;
import net.infugogr.barracuda.entity.custom.BarracudaEntity;
import net.infugogr.barracuda.entity.custom.BassFishEntity;
import net.infugogr.barracuda.entity.custom.ModGiantEntity;
import net.infugogr.barracuda.entity.effect.ModStatusEffects;
import net.infugogr.barracuda.fluid.FluidData;
import net.infugogr.barracuda.fluid.ModFluids;
import net.infugogr.barracuda.item.ModItemGroups;
import net.infugogr.barracuda.item.ModItems;
import net.infugogr.barracuda.screenhandler.ModScreenHandlerType;
import net.infugogr.barracuda.sound.ModSounds;
import net.infugogr.barracuda.util.ModTags;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.reborn.energy.api.EnergyStorage;

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
		ModFluids.register();
		// configReg.load();
		

		// Item Lookup
		EnergyStorage.SIDED.registerForBlockEntity(FuelGeneratorBlockEntity::getEnergyProvider, ModBlockEntityType.FUEL_GENERATOR);
		ItemStorage.SIDED.registerForBlockEntity(FuelGeneratorBlockEntity::getInventoryProvider, ModBlockEntityType.FUEL_GENERATOR);

		EnergyStorage.SIDED.registerForBlockEntity(CrusherBlockEntity::getEnergyProvider, ModBlockEntityType.CRUSHER);
		ItemStorage.SIDED.registerForBlockEntity(CrusherBlockEntity::getInventoryProvider, ModBlockEntityType.CRUSHER);

		ItemStorage.SIDED.registerForBlockEntity(FishingNetBlockEntity::getInventoryProvider, ModBlockEntityType.FISHING_NET);

		EnergyStorage.SIDED.registerForBlockEntity(SMESblockEntity::getEnergyProvider, ModBlockEntityType.SMES);
		ItemStorage.SIDED.registerForBlockEntity(SMESblockEntity::getInventoryProvider, ModBlockEntityType.SMES);

		EnergyStorage.SIDED.registerForBlockEntity(OilRefineryBlockEntity::getEnergyProvider, ModBlockEntityType.OIL_REFINERY);
		ItemStorage.SIDED.registerForBlockEntity(OilRefineryBlockEntity::getInventoryProvider, ModBlockEntityType.OIL_REFINERY);
		FluidStorage.SIDED.registerForBlockEntity(OilRefineryBlockEntity::getFluidProvider, ModBlockEntityType.OIL_REFINERY);


		//EnergyStorage.SIDED.registerForBlockEntity(TeleporterBlockEntity::getEnergyProvider, ModBlockEntityType.TELEPORTER);
		//ItemStorage.SIDED.registerForBlockEntity(TeleporterBlockEntity::getInventoryProvider, ModBlockEntityType.TELEPORTER);

		EnergyStorage.SIDED.registerForBlockEntity(LVcableBlockEntity::getEnergyProvider, ModBlockEntityType.LVCABLE);
		EnergyStorage.SIDED.registerForBlockEntity(HVcableBlockEntity::getEnergyProvider, ModBlockEntityType.HVCABLE);

		FabricDefaultAttributeRegistry.register(ModEntities.GIANT, ModGiantEntity.createGiantAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.BASS_FISH, BassFishEntity.createBassFishAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.AZURE_SERPENT, AzureSerpentEntity.createAzureSerpentAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.BARRACUDA, BarracudaEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.AZUER_REAPER, BarracudaEntity.setAttributes());

		// Fluid Properties
		var commonFluidAttributes = new FluidVariantAttributeHandler() {
			@Override
			public int getViscosity(FluidVariant variant, @Nullable World world) {
				return 7500;
			}
		};

		FluidVariantAttributes.register(ModFluids.CRUDE_OIL.still(), commonFluidAttributes);
		FluidVariantAttributes.register(ModFluids.CRUDE_OIL.flowing(), commonFluidAttributes);

		// Fluid Data
		var commonFluidData = new FluidData.Builder(ModTags.Fluids.CRUDE_OIL)
				.preventsBlockSpreading()
				.canSwim()
				.fluidMovementSpeed((entity, speed) -> 0.01F)
				.applyWaterMovement()
				.applyBuoyancy(itemEntity -> itemEntity.setVelocity(itemEntity.getVelocity().add(0.0D, 0.01D, 0.0D)))
				.canCauseDrowning()
				.shouldWitchDrinkWaterBreathing()
				.affectsBlockBreakSpeed()
				.bubbleParticle(ParticleTypes.ASH)
				.splashParticle(ParticleTypes.HEART)
				.build();

		FluidData.registerFluidData(ModFluids.CRUDE_OIL.still(), commonFluidData);
		FluidData.registerFluidData(ModFluids.CRUDE_OIL.flowing(), commonFluidData);

		LOGGER.info("Loaded!");
	}
	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}
}
package net.infugogr.barracuda;


import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.infugogr.barracuda.block.ModBlocks;
import net.infugogr.barracuda.block.entity.ModBlockEntityType;
import net.infugogr.barracuda.block.entity.client.*;
import net.infugogr.barracuda.entity.ModEntities;
import net.infugogr.barracuda.entity.client.*;
import net.infugogr.barracuda.fluid.RenderFluidHandler;
import net.infugogr.barracuda.screenhandler.*;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.util.Identifier;

public class BarracudaClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        // Registering Screens
        HandledScreens.register(ModScreenHandlerType.FUEL_GENERATOR, FuelGeneratorScreen::new);
        HandledScreens.register(ModScreenHandlerType.URANIUM_GENERATOR, UraniumGeneratorScreen::new);
        HandledScreens.register(ModScreenHandlerType.SMES, SMESScreen::new);
        HandledScreens.register(ModScreenHandlerType.FISHING_NET, FishingNetScreen::new);
        HandledScreens.register(ModScreenHandlerType.OIL_REFINERY, OilRefineryScreen::new);
        HandledScreens.register(ModScreenHandlerType.CHEMICAL_PLANT, ChemicalPlantScreen::new);
        HandledScreens.register(ModScreenHandlerType.CRUSHER, CrusherScreen::new);
        HandledScreens.register(ModScreenHandlerType.ELECTRIC_SMELTER, ElectricSmelterScreen::new);
        HandledScreens.register(ModScreenHandlerType.TELEPORTER, TeleporterScreen::new);
        HandledScreens.register(ModScreenHandlerType.CENTRIFUGE, CentrifugeScreen::new);
        HandledScreens.register(ModScreenHandlerType.CLOSET, ClosetScreen::new);
        HandledScreens.register(ModScreenHandlerType.CIRCUIT_IMPRINTER, CircuitImprinterScreen::new);


        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.POUNDER, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.FISHING_NET, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.OIL_REFINERY, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.CHEMICAL_PLANT, RenderLayer.getCutout());

        // Block Entity Renderers
        BlockEntityRendererFactories.register(ModBlockEntityType.CIRCUIT_IMPRINTER, CircuitImprinterBlockRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntityType.CENTRIFUGE, CentrifugeBlockRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntityType.CLOSET, ClosetBlockRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(ClosetModel.LAYER_LOCATION, ClosetModel::getTexturedModelData);

        // Mobs Renderers
        EntityRendererRegistry.register(ModEntities.GIANT, GiantRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.GIANT, GiantModel::getTexturedModelData);

        EntityRendererRegistry.register(ModEntities.BASS_FISH, BassFishRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.BASS_FISH, BassFishModel::getTexturedModelData);

        EntityRendererRegistry.register(ModEntities.AZURE_SERPENT, AzureSerpentRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.AZURE_SERPENT, AzureSerpentModel::getTexturedModelData);

        // geo mobs
        EntityRendererRegistry.register(ModEntities.BARRACUDA, BarracudaRenderer::new);
        EntityRendererRegistry.register(ModEntities.AZUER_REAPER, AzureReaperRenderer::new);

        RenderFluidHandler.register();
    }
}
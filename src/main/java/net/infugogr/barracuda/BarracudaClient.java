package net.infugogr.barracuda;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.infugogr.barracuda.block.entity.ModBlockEntityType;
import net.infugogr.barracuda.block.entity.client.TeleporterBlockRenderer;
import net.infugogr.barracuda.entity.ModEntities;
import net.infugogr.barracuda.entity.client.*;
import net.infugogr.barracuda.screenhandler.FishingNetScreen;
import net.infugogr.barracuda.screenhandler.FuelGeneratorScreen;
import net.infugogr.barracuda.screenhandler.ModScreenHandlerType;
import net.infugogr.barracuda.screenhandler.SMESScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

public class BarracudaClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        // Registering Screens
        HandledScreens.register(ModScreenHandlerType.FUEL_GENERATOR, FuelGeneratorScreen::new);
        HandledScreens.register(ModScreenHandlerType.SMES, SMESScreen::new);
        HandledScreens.register(ModScreenHandlerType.FISHING_NET, FishingNetScreen::new);

        EntityRendererRegistry.register(ModEntities.GIANT, GiantRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.GIANT, GiantModel::getTexturedModelData);
        
        EntityRendererRegistry.register(ModEntities.BASS_FISH, BassFishRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.BASS_FISH, BassFishModel::getTexturedModelData);

        EntityRendererRegistry.register(ModEntities.AZURE_SERPENT, AzureSerpentRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.AZURE_SERPENT, AzureSerpentModel::getTexturedModelData);
 
        // geo mobs
        EntityRendererRegistry.register(ModEntities.BARRACUDA, BarracudaRenderer::new);
        EntityRendererRegistry.register(ModEntities.AZUER_REAPER, AzureReaperRenderer::new);

        BlockEntityRendererFactories.register(ModBlockEntityType.TELEPORTER, TeleporterBlockRenderer::new);
    }
}

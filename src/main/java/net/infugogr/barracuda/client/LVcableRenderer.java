package net.infugogr.barracuda.client;

import net.infugogr.barracuda.block.entity.LVcableBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;

public class LVcableRenderer implements BlockEntityRenderer<LVcableBlockEntity> {
    public BakedModel model;
    public LVcableRenderer(BlockEntityRendererFactory.Context context) {
    }

    @Override
    public void render(LVcableBlockEntity entity, float tickDelta,
                       MatrixStack matrices, VertexConsumerProvider vertexConsumers,
                       int light, int overlay) {

        BlockState copied = entity.getMimicState();
        model = MinecraftClient.getInstance()
                .getBakedModelManager()
                .getModel(ModelIdentifier.of(String.valueOf(copied.getBlock().getName()), ""));

        MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(
                copied, matrices, vertexConsumers, light, overlay
        );
    }
}

package net.infugogr.barracuda.block.entity.client;


import net.infugogr.barracuda.block.Closet;
import net.infugogr.barracuda.block.entity.ClosetBlockEntity;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public class ClosetBlockRenderer implements BlockEntityRenderer<ClosetBlockEntity> {

    private final ClosetModel model;

    public ClosetBlockRenderer(BlockEntityRendererFactory.Context context) {
        this.model = new ClosetModel(context.getLayerModelPart(ClosetModel.LAYER_LOCATION));
    }

    @Override
    public void render(ClosetBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        matrices.translate(0.5f, 0.0f, 0.5f);

        int numPlayersOpen = entity.getNumPlayersOpen();
        float lidAngle = entity.lidAngle;

        ModelPart lid = this.model.getLid();
        float defaultLidAngle = lid.yaw;

        double maxAngle = Math.toRadians(110);

        if (numPlayersOpen > 0 && lidAngle < maxAngle) {
            lid.yaw = MathHelper.lerp(tickDelta / 8, lidAngle, (float) maxAngle);
        } else if (numPlayersOpen == 0 && lidAngle > defaultLidAngle) {
            lid.yaw = MathHelper.lerp(tickDelta / 8, lidAngle, defaultLidAngle);
        }

        entity.lidAngle = lid.yaw;

        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(switch (entity.getCachedState().get(Closet.FACING)) {
            case EAST -> 270;
            case SOUTH -> 180;
            case WEST -> 90;
            default -> 0;
        }));

        this.model.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntitySolid(ClosetModel.TEXTURE_LOCATION)), light, overlay, 100,100,100,100);

        lid.yaw = defaultLidAngle;

        matrices.pop();
    }
    public record ItemTransformation(double x, double z, int rotation) {}
}
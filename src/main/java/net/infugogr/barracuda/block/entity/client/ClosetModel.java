// Made with Blockbench 4.12.6
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports

package net.infugogr.barracuda.block.entity.client;

import net.infugogr.barracuda.Barracuda;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class ClosetModel extends Model{
	private final ModelPart main;
	private final ModelPart lid;
	public static final EntityModelLayer LAYER_LOCATION = new EntityModelLayer(Barracuda.id("main"), "closet");
	public static final Identifier TEXTURE_LOCATION = Barracuda.id("textures/models/closet.png");
	public ClosetModel(ModelPart root) {
        super(RenderLayer::getEntitySolid);
        this.main = root.getChild("main");
		this.lid = this.main.getChild("lid");
	}
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData main = modelPartData.addChild("main", ModelPartBuilder.create().uv(0, 0).cuboid(-6.0F, 0.0F, -3.0F, 1.0F, 30.0F, 11.0F, new Dilation(0.0F))
		.uv(48, 0).cuboid(-5.0F, 0.0F, -3.0F, 10.0F, 1.0F, 10.0F, new Dilation(0.0F))
		.uv(24, 0).cuboid(5.0F, 0.0F, -3.0F, 1.0F, 30.0F, 11.0F, new Dilation(0.0F))
		.uv(26, 41).cuboid(-5.0F, 0.0F, 7.0F, 10.0F, 30.0F, 1.0F, new Dilation(0.0F))
		.uv(48, 11).cuboid(-5.0F, 29.0F, -3.0F, 10.0F, 1.0F, 10.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData lid = main.addChild("lid", ModelPartBuilder.create().uv(0, 41).cuboid(0.0F, -15.0F, -1.0F, 12.0F, 30.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(-6.0F, 15.0F, -3.0F));
		return TexturedModelData.of(modelData, 128, 128);
	}
	@Override
	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		main.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
	}

	public ModelPart getLid() {
		return this.lid;
	}
}
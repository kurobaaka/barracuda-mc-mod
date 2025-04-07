package net.infugogr.barracuda.entity.client;

import net.infugogr.barracuda.entity.animation.GiantModelAnimation;
import net.infugogr.barracuda.entity.custom.ModGiantEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.util.math.MathHelper;

public class GiantModel<T extends ModGiantEntity> extends SinglePartEntityModel<T> {
	private final ModelPart bone;
	private final ModelPart left_arm;
	private final ModelPart left_arm_1;
	private final ModelPart right_arm;
	private final ModelPart right_arm_1;
	private final ModelPart left_leg;
	private final ModelPart left_leg_1;
	private final ModelPart right_leg;
	private final ModelPart right_leg_1;
	private final ModelPart head;
	private final ModelPart torso;
	public GiantModel(ModelPart root) {
		this.bone = root.getChild("bone");
		this.left_arm = this.bone.getChild("left arm");
		this.left_arm_1 = this.left_arm.getChild("left arm 1");
		this.right_arm = this.bone.getChild("right arm");
		this.right_arm_1 = this.right_arm.getChild("right arm 1");
		this.left_leg = this.bone.getChild("left leg");
		this.left_leg_1 = this.left_leg.getChild("left leg 1");
		this.right_leg = this.bone.getChild("right leg");
		this.right_leg_1 = this.right_leg.getChild("right leg 1");
		this.head = this.bone.getChild("head");
		this.torso = this.bone.getChild("torso");
	}
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData bone = modelPartData.addChild("bone", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

		ModelPartData left_arm = bone.addChild("left arm", ModelPartBuilder.create(), ModelTransform.pivot(-5.0F, -35.0F, -3.0F));

		ModelPartData left_arm_1 = left_arm.addChild("left arm 1", ModelPartBuilder.create().uv(0, 22).cuboid(-8.0F, -35.0F, -5.0F, 3.0F, 30.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(5.0F, 35.0F, 3.0F));

		ModelPartData right_arm = bone.addChild("right arm", ModelPartBuilder.create(), ModelTransform.pivot(5.0F, -35.0F, -3.0F));

		ModelPartData right_arm_1 = right_arm.addChild("right arm 1", ModelPartBuilder.create().uv(0, 22).mirrored().cuboid(5.0F, -35.0F, -5.0F, 3.0F, 30.0F, 4.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(-5.0F, 35.0F, 3.0F));

		ModelPartData left_leg = bone.addChild("left leg", ModelPartBuilder.create(), ModelTransform.pivot(-2.0F, -23.0F, 0.0F));

		ModelPartData left_leg_1 = left_leg.addChild("left leg 1", ModelPartBuilder.create().uv(48, 22).cuboid(-4.0F, -23.0F, -2.0F, 3.0F, 23.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(2.0F, 23.0F, 0.0F));

		ModelPartData right_leg = bone.addChild("right leg", ModelPartBuilder.create(), ModelTransform.pivot(2.0F, -23.0F, 0.0F));

		ModelPartData right_leg_1 = right_leg.addChild("right leg 1", ModelPartBuilder.create().uv(48, 22).mirrored().cuboid(1.0F, -23.0F, -2.0F, 3.0F, 23.0F, 4.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(-2.0F, 23.0F, 0.0F));

		ModelPartData head = bone.addChild("head", ModelPartBuilder.create().uv(3, 2).cuboid(-3.0F, -9.0F, -7.0F, 6.0F, 10.0F, 6.0F, new Dilation(0.0F))
		.uv(24, 0).cuboid(-1.0F, -2.25F, -9.0F, 2.0F, 4.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -36.0F, 0.0F, 0.1309F, 0.0F, 0.0F));

		ModelPartData torso = bone.addChild("torso", ModelPartBuilder.create().uv(15, 20).cuboid(-5.0F, -14.0F, -3.0F, 10.0F, 14.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -23.0F, 0.0F, 0.2618F, 0.0F, 0.0F));
		return TexturedModelData.of(modelData, 64, 64);
	}

	@Override
	public void setAngles(ModGiantEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.getPart().traverse().forEach(ModelPart::resetTransform);
		this.setHeadAngles(netHeadYaw, headPitch);

		this.animateMovement(GiantModelAnimation.GIANT_WALK, limbSwing, limbSwingAmount, 2f, 2.5f);
		this.updateAnimation(entity.attackAnimationState, GiantModelAnimation.GIANT_ATTACK, ageInTicks, 1f);
	}

	private void setHeadAngles(float headYaw, float headPitch) {
		headYaw = MathHelper.clamp(headYaw, -30.0F, 30.0F);
		headPitch = MathHelper.clamp(headPitch, -25.0F, 45.0F);

		this.head.yaw = headYaw * 0.017453292F;
		this.head.pitch = headPitch * 0.017453292F;
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		bone.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
	}

	@Override
	public ModelPart getPart() {
		return bone;
	}
}
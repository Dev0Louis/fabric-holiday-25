package holiday.client.render;

import holiday.entity.HeartEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;

// Made with Blockbench 5.0.5
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports
public class HeartEntityModel extends EntityModel<HeartEntityRenderState> {

	private final ModelPart bb_main;

	public HeartEntityModel(ModelPart root) {
        super(root);
        this.bb_main = root.getChild("bb_main");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData bb_main = modelPartData.addChild("bb_main", ModelPartBuilder.create().uv(0, 23).cuboid(-1.0F, -1.0F, -2.0F, 2.0F, 1.0F, 4.0F, new Dilation(0.0F))
		.uv(0, 18).cuboid(-2.0F, -2.0F, -2.0F, 4.0F, 1.0F, 4.0F, new Dilation(0.0F))
		.uv(0, 13).cuboid(-3.0F, -3.0F, -2.0F, 6.0F, 1.0F, 4.0F, new Dilation(0.0F))
		.uv(0, 8).cuboid(-4.0F, -4.0F, -2.0F, 8.0F, 1.0F, 4.0F, new Dilation(0.0F))
		.uv(0, 0).cuboid(-5.0F, -8.0F, -2.0F, 10.0F, 4.0F, 4.0F, new Dilation(0.0F))
		.uv(16, 18).cuboid(-4.0F, -9.0F, -2.0F, 3.0F, 1.0F, 4.0F, new Dilation(0.0F))
		.uv(20, 13).cuboid(1.0F, -9.0F, -2.0F, 3.0F, 1.0F, 4.0F, new Dilation(0.0F))
		.uv(12, 23).cuboid(-3.0F, -10.0F, -2.0F, 1.0F, 1.0F, 4.0F, new Dilation(0.0F))
		.uv(22, 23).cuboid(2.0F, -10.0F, -2.0F, 1.0F, 1.0F, 4.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 0.0F, 0.0F));
		return TexturedModelData.of(modelData, 64, 64);
	}

    /*
	@Override
	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, int color) {
		bb_main.render(matrices, vertexConsumer, light, overlay, color);
	}
     */

    @Override
    public void setAngles(HeartEntityRenderState state) {
        super.setAngles(state);
        this.bb_main.yaw = state.yaw * (float) (Math.PI / 180.0);
        this.bb_main.pitch = state.pitch * (float) (Math.PI / 180.0);
    }
}

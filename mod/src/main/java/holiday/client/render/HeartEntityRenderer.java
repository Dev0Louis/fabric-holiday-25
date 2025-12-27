package holiday.client.render;

import holiday.ClientEntrypoint;
import holiday.CommonEntrypoint;
import holiday.entity.HeartEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class HeartEntityRenderer extends EntityRenderer<HeartEntity, HeartEntityRenderState> {

    // TODO: create texture
    private static final Identifier TEXTURE = CommonEntrypoint.identifier("textures/entity/heart/heart.png");
    private final HeartEntityModel model;


    public HeartEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.model = new HeartEntityModel(context.getPart(ClientEntrypoint.HEART_LAYER));
    }

    @Override
    public void render(HeartEntityRenderState renderState, MatrixStack matrices, OrderedRenderCommandQueue queue, CameraRenderState cameraState) {
        matrices.push();
        matrices.scale(-1.0F, -1.0F, 1.0F);
        queue.submitModel(
            this.model,
            renderState,
            matrices,
            this.model.getLayer(TEXTURE),
            renderState.light,
            OverlayTexture.DEFAULT_UV,
            renderState.outlineColor,
            null
        );
        matrices.pop();
        super.render(renderState, matrices, queue, cameraState);
    }

    @Override
    public HeartEntityRenderState createRenderState() {
        return new HeartEntityRenderState();
    }

    @Override
    public void updateRenderState(HeartEntity entity, HeartEntityRenderState state, float tickProgress) {
        super.updateRenderState(entity, state, tickProgress);
        state.yaw = entity.getLerpedYaw(tickProgress);
        state.pitch = entity.getLerpedPitch(tickProgress);
    }

    @Override
    protected int getBlockLight(HeartEntity entity, BlockPos pos) {
        return 15;
    }
}

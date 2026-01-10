package holiday.client.render;

import holiday.ClientEntrypoint;
import holiday.CommonEntrypoint;
import holiday.client.render.model.WitherCrownEntityModel;
import holiday.idkwheretoputthis.PlayerEntityRenderStateExtension;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class WitherCrownFeatureRenderer extends FeatureRenderer<PlayerEntityRenderState, PlayerEntityModel> {
    // TODO: create texture
    private static final Identifier TEXTURE = CommonEntrypoint.identifier("textures/entity/wither/wither_crown.png");
    private final BipedEntityModel<PlayerEntityRenderState> model;

    public WitherCrownFeatureRenderer(FeatureRendererContext<PlayerEntityRenderState, PlayerEntityModel> context, LoadedEntityModels entityModels) {
        super(context);
        this.model = new WitherCrownEntityModel(entityModels.getModelPart(ClientEntrypoint.WITHER_CROWN_LAYER));
    }

    @Override
    public void render(MatrixStack matrices, OrderedRenderCommandQueue queue, int light, PlayerEntityRenderState state, float limbAngle, float limbDistance) {
        PlayerEntityRenderStateExtension stateExtension = (PlayerEntityRenderStateExtension) state;

        if (stateExtension.fabric_holiday_25$isWearingWitherCrown() && !state.invisible) {
            int overlay = LivingEntityRenderer.getOverlay(state, 0.0F);
            queue.submitModel(
                this.model,
                state,
                matrices,
                //RenderLayers.entitySolid(playerEntityRenderState.skinTextures.body().texturePath()),
                this.model.getLayer(TEXTURE),
                light,
                overlay,
                state.outlineColor,
                null
            );
        }
    }
}

package holiday.mixin;

import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.WitherEntityModel;
import net.minecraft.client.render.entity.state.WitherEntityRenderState;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Made with Blockbench 5.0.5
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports
@Mixin(WitherEntityModel.class)
public class WitherEntityModelMixin extends EntityModel<WitherEntityRenderState> {
    private WitherEntityModelMixin(ModelPart root) {
        super(root);
    }

    @Inject(method = "getTexturedModelData", at = @At("HEAD"), cancellable = true)
    private static void getTexturedModelDataInject(Dilation dilation, CallbackInfoReturnable<TexturedModelData> cir) {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();

        // shoulders
        modelPartData.addChild("shoulders", ModelPartBuilder.create().uv(0, 20).cuboid(-10.0F, 3.9F, -0.5F, 20.0F, 3.0F, 3.0F, dilation), ModelTransform.NONE);

        float f = 0.20420352F;

        // ribcage
        modelPartData.addChild("ribcage", ModelPartBuilder.create()
                .uv(0, 26).cuboid(0.0F, 0.0F, 0.0F, 3.0F, 10.0F, 3.0F, dilation)
                .uv(24, 26).cuboid(-4.0F, 1.5F, 0.5F, 11.0F, 2.0F, 2.0F, dilation)
                .uv(24, 26).cuboid(-4.0F, 4.0F, 0.5F, 11.0F, 2.0F, 2.0F, dilation)
                .uv(24, 26).cuboid(-4.0F, 6.5F, 0.5F, 11.0F, 2.0F, 2.0F, dilation),
            ModelTransform.of(-2.0F, 6.9F, -0.5F, f, 0.0F, 0.0F)
        );

        // tail
        modelPartData.addChild(
            "tail",
            ModelPartBuilder.create().uv(12, 26).cuboid(0.0F, 0.0F, 0.0F, 3.0F, 6.0F, 3.0F, dilation),
            ModelTransform.of(-2.0F, 16.9F + MathHelper.cos(f) * 10.0F, -0.5F + MathHelper.sin(f) * 10.0F, 0.83252203F, 0.0F, 0.0F)
        );

        // heads
		modelPartData.addChild("center_head", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 12.0F, 8.0F, dilation), ModelTransform.NONE);
		modelPartData.addChild("right_head", ModelPartBuilder.create().uv(0, 0).cuboid(-5.0F, -9.0F, -5.0F, 8.0F, 12.0F, 8.0F, dilation.add(-1.0F)), ModelTransform.origin(10.0F, 4.0F, 0.0F));
        modelPartData.addChild("left_head", ModelPartBuilder.create().uv(0, 0).cuboid(-5.0F, -9.0F, -5.0F, 8.0F, 12.0F, 8.0F, dilation.add(-1.0F)), ModelTransform.origin(-8.0F, 4.0F, 0.0F));

        cir.setReturnValue(TexturedModelData.of(modelData, 64, 64));
	}
}

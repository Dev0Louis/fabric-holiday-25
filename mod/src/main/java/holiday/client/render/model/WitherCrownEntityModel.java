package holiday.client.render.model;

import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.model.PlayerEntityModel;

public class WitherCrownEntityModel extends PlayerEntityModel {
    public WitherCrownEntityModel(ModelPart root) {
        super(root, false);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = PlayerEntityModel.getTexturedModelData(Dilation.NONE, false);
        ModelPartData modelPartData = modelData.getRoot().resetChildrenParts();
        ModelPartData modelPartData2 = modelPartData.getChild(EntityModelPartNames.HEAD);

        ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(0, -3).cuboid(-4.0F, -16.0F, -4.0F, 0.0F, 8.0F, 8.0F, new Dilation(0.0F));

        modelPartData2.addChild("hat", modelPartBuilder
            .uv(0, -3).cuboid(4.0F, -16.0F, -4.0F, 0.0F, 8.0F, 8.0F, new Dilation(0.0F))
            .uv(0, 5).cuboid(-4.0F, -16.0F, -4.0F, 8.0F, 8.0F, 0.0F, new Dilation(0.0F))
            .uv(0, 5).cuboid(-4.0F, -16.0F, 4.0F, 8.0F, 8.0F, 0.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 0.0F, 0.0F)); // y = 24.0F

        return TexturedModelData.of(modelData, 26, 13);
    }
}

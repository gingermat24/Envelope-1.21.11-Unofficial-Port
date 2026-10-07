package io.github.mortuusars.envelope.client.model;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.Mth;

public class CourierBatModel extends EntityModel<CourierBatModel.RenderState> {
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart leftWing;
    private final ModelPart rightWing;
    private final ModelPart leftWingTip;
    private final ModelPart rightWingTip;

    public CourierBatModel(ModelPart root) {
        super(root);
        head = root.getChild("head");
        body = root.getChild("body");
        leftWing = root.getChild("left_wing");
        rightWing = root.getChild("right_wing");
        leftWingTip = leftWing.getChild("left_wing_tip");
        rightWingTip = rightWing.getChild("right_wing_tip");
    }

    public static LayerDefinition createLayerDefinition() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("head", CubeListBuilder.create()
                    .texOffs(0, 0).addBox(-3.0F, -3.0F, -3.0F, 6.0F, 3.0F, 3.0F)
                    .texOffs(0, 6).addBox(-3.0F, -6.0F, -2.0F, 2.0F, 3.0F, 1.0F)
                    .texOffs(6, 6).addBox(1.0F, -6.0F, -2.0F, 2.0F, 3.0F, 1.0F),
              PartPose.offset(0.0F, 15.0F, -2.0F));
        root.addOrReplaceChild("body", CubeListBuilder.create()
                    .texOffs(0, 10).addBox(-3.0F, -2.0F, -2.0F, 6.0F, 4.0F, 4.0F)
                    .texOffs(20, 0).addBox(-2.0F, 2.0F, 0.0F, 4.0F, 3.0F, 1.0F),
              PartPose.offset(0.0F, 17.0F, 0.0F));

        PartDefinition leftWing = root.addOrReplaceChild("left_wing", CubeListBuilder.create()
                    .texOffs(20, 8).addBox(0.0F, -1.0F, -1.0F, 5.0F, 2.0F, 1.0F),
              PartPose.offset(3.0F, 16.0F, 0.0F));
        leftWing.addOrReplaceChild("left_wing_tip", CubeListBuilder.create()
                    .texOffs(32, 8).addBox(0.0F, -1.0F, -1.0F, 5.0F, 2.0F, 1.0F),
              PartPose.offset(5.0F, 0.0F, 0.0F));

        PartDefinition rightWing = root.addOrReplaceChild("right_wing", CubeListBuilder.create()
                    .texOffs(20, 8).mirror().addBox(-5.0F, -1.0F, -1.0F, 5.0F, 2.0F, 1.0F),
              PartPose.offset(-3.0F, 16.0F, 0.0F));
        rightWing.addOrReplaceChild("right_wing_tip", CubeListBuilder.create()
                    .texOffs(32, 8).mirror().addBox(-5.0F, -1.0F, -1.0F, 5.0F, 2.0F, 1.0F),
              PartPose.offset(-5.0F, 0.0F, 0.0F));

        return LayerDefinition.create(mesh, 64, 32);
    }

    @Override
    public void setupAnim(RenderState state) {
        super.setupAnim(state);
        head.xRot = state.xRot * Mth.DEG_TO_RAD;
        head.yRot = state.yRot * Mth.DEG_TO_RAD;

        float flap = Mth.sin(state.ageInTicks * 1.6F) * 0.7F;
        leftWing.yRot = flap;
        rightWing.yRot = -flap;
        leftWingTip.yRot = flap * 0.65F;
        rightWingTip.yRot = -flap * 0.65F;
        body.xRot = 0.15F + Mth.sin(state.ageInTicks * 0.5F) * 0.04F;
    }

    public static class RenderState extends LivingEntityRenderState {
        public boolean hasMail;
    }
}

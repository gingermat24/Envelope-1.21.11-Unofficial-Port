package io.github.mortuusars.envelope.client.model;

import io.github.mortuusars.envelope.Envelope;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public class BatBackpackModel extends EntityModel<CourierBatModel.RenderState> {
    public static final Identifier TEXTURE = Envelope.resource("textures/entity/bat/misc/bat_backpack.png");
    private final ModelPart body;

    public BatBackpackModel(ModelPart root) {
        super(root);
        body = root.getChild("body");
    }

    public static LayerDefinition createLayerDefinition() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create(),
              PartPose.offset(0.0F, 17.0F, 0.0F));
        body.addOrReplaceChild("backpack", CubeListBuilder.create()
                    .texOffs(0, 0).addBox(-3.0F, -1.0F, 1.0F, 6.0F, 7.0F, 5.0F)
                    .texOffs(0, 12).addBox(-0.5F, -3.0F, -1.0F, 1.0F, 6.0F, 2.0F,
                          new net.minecraft.client.model.geom.builders.CubeDeformation(0.0F, 0.0F, 0.01F)),
              PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, Mth.DEG_TO_RAD * 32.5F));
        return LayerDefinition.create(mesh, 32, 32);
    }

    @Override
    public void setupAnim(CourierBatModel.RenderState state) {
        super.setupAnim(state);
        body.xRot = 0.15F + Mth.sin(state.ageInTicks * 0.5F) * 0.04F;
    }
}

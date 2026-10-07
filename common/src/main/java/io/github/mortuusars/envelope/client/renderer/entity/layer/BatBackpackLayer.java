package io.github.mortuusars.envelope.client.renderer.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.mortuusars.envelope.Envelope;
import io.github.mortuusars.envelope.client.model.BatBackpackModel;
import io.github.mortuusars.envelope.client.model.CourierBatModel;
import io.github.mortuusars.envelope.world.entity.CourierBat;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;

public class BatBackpackLayer extends RenderLayer<CourierBatModel.RenderState, CourierBatModel> {
    public static final ModelLayerLocation MODEL_LAYER = new ModelLayerLocation(Envelope.resource("bat_backpack"), "main");

    private final BatBackpackModel model;

    public BatBackpackLayer(RenderLayerParent<CourierBatModel.RenderState, CourierBatModel> renderer,
                            EntityModelSet modelSet) {
        super(renderer);
        model = new BatBackpackModel(modelSet.bakeLayer(MODEL_LAYER));
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector collector, int packedLight,
                       CourierBatModel.RenderState state, float yRot, float xRot) {
        if (state.hasMail) {
            model.setupAnim(state);
            coloredCutoutModelCopyLayerRender(model, BatBackpackModel.TEXTURE, poseStack, collector,
                  packedLight, state, -1, 0);
        }
    }
}

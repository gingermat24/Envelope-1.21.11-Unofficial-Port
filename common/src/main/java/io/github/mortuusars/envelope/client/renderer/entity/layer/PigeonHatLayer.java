package io.github.mortuusars.envelope.client.renderer.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.mortuusars.envelope.Envelope;
import io.github.mortuusars.envelope.client.model.PigeonModel;
import io.github.mortuusars.envelope.world.entity.Pigeon;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.Identifier;
import io.github.mortuusars.envelope.client.model.PigeonModel.RenderState;

public class PigeonHatLayer extends RenderLayer<RenderState, PigeonModel> {
    public static final ModelLayerLocation MODEL_LAYER = new ModelLayerLocation(Envelope.resource("pigeon_hat"), "main");

    public static final Identifier TEXTURE = Envelope.resource("textures/entity/pigeon/misc/pigeon_hat.png");

    protected final PigeonModel model;

    public PigeonHatLayer(RenderLayerParent<RenderState, PigeonModel> renderer, EntityModelSet modelSet) {
        super(renderer);
        model = new PigeonModel(modelSet.bakeLayer(MODEL_LAYER));
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector collector, int packedLight, RenderState state,
                       float yRot, float xRot) {
        if (!state.isBaby && state.hasMailmanHat) {
            model.setupAnim(state);
            coloredCutoutModelCopyLayerRender(model, TEXTURE, poseStack, collector, packedLight, state, -1, 0);
        }
    }
}

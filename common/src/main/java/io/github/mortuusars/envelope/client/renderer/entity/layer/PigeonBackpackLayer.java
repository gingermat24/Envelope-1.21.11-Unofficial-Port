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
import io.github.mortuusars.envelope.client.model.PigeonModel.RenderState;
import net.minecraft.resources.Identifier;

public class PigeonBackpackLayer extends RenderLayer<RenderState, PigeonModel> {
    public static final ModelLayerLocation MODEL_LAYER = new ModelLayerLocation(Envelope.resource("pigeon_backpack"), "main");

    public static final Identifier TEXTURE = Envelope.resource("textures/entity/pigeon/misc/pigeon_backpack.png");

    protected final PigeonModel model;

    public PigeonBackpackLayer(RenderLayerParent<RenderState, PigeonModel> renderer, EntityModelSet modelSet) {
        super(renderer);
        model = new PigeonModel(modelSet.bakeLayer(MODEL_LAYER));
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector collector, int packedLight, RenderState state,
                       float yRot, float xRot) {
        if (!state.isBaby && state.hasMail) {
            model.setupAnim(state);
            coloredCutoutModelCopyLayerRender(model, TEXTURE, poseStack, collector, packedLight, state, -1, 0);
        }
    }
}

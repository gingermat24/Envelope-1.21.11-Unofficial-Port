package io.github.mortuusars.envelope.client.renderer.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.mortuusars.envelope.Envelope;
import io.github.mortuusars.envelope.client.model.CharredPigeonModel;
import io.github.mortuusars.envelope.world.entity.CharredPigeon;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.Identifier;
import io.github.mortuusars.envelope.client.model.CharredPigeonModel.RenderState;

public class CharredPigeonBackpackLayer extends RenderLayer<RenderState, CharredPigeonModel> {
    public static final ModelLayerLocation MODEL_LAYER = new ModelLayerLocation(Envelope.resource("charred_pigeon_backpack"), "main");

    public static final Identifier TEXTURE = Envelope.resource("textures/entity/charred_pigeon/charred_pigeon_backpack.png");

    protected final CharredPigeonModel model;

    public CharredPigeonBackpackLayer(RenderLayerParent<RenderState, CharredPigeonModel> renderer, EntityModelSet modelSet) {
        super(renderer);
        model = new CharredPigeonModel(modelSet.bakeLayer(MODEL_LAYER));
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

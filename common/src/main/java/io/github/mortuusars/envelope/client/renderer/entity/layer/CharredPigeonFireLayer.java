package io.github.mortuusars.envelope.client.renderer.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.mortuusars.envelope.Envelope;
import io.github.mortuusars.envelope.client.model.CharredPigeonModel;
import io.github.mortuusars.envelope.world.entity.CharredPigeon;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import io.github.mortuusars.envelope.client.model.CharredPigeonModel.RenderState;

public class CharredPigeonFireLayer extends RenderLayer<RenderState, CharredPigeonModel> {
    public static final Identifier TEXTURE = Envelope.resource("textures/entity/charred_pigeon/charred_pigeon_fire.png");

    public CharredPigeonFireLayer(RenderLayerParent<RenderState, CharredPigeonModel> renderer) {
        super(renderer);
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector collector, int packedLight, RenderState state,
                       float yRot, float xRot) {
        getParentModel().setupAnim(state);
        collector.submitModel(getParentModel(), state, poseStack, RenderTypes.entityTranslucentEmissive(TEXTURE),
              packedLight, LivingEntityRenderer.getOverlayCoords(state, 0), -1, null, state.outlineColor, null);
    }
}

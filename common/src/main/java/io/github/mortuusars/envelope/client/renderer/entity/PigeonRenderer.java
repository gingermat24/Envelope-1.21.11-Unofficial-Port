package io.github.mortuusars.envelope.client.renderer.entity;

import io.github.mortuusars.envelope.Envelope;
import io.github.mortuusars.envelope.client.model.PigeonModel;
import io.github.mortuusars.envelope.client.renderer.entity.layer.PigeonBackpackLayer;
import io.github.mortuusars.envelope.client.renderer.entity.layer.PigeonHatLayer;
import io.github.mortuusars.envelope.world.entity.Pigeon;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.AgeableMobRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class PigeonRenderer extends AgeableMobRenderer<Pigeon, PigeonModel.RenderState, PigeonModel> {
    public static final ModelLayerLocation MODEL_LAYER = new ModelLayerLocation(Envelope.resource("pigeon"), "main");
    public static final ModelLayerLocation BABY_MODEL_LAYER = new ModelLayerLocation(Envelope.resource("pigeon_baby"), "main");

    public PigeonRenderer(EntityRendererProvider.Context context) {
        super(context,
              new PigeonModel(context.bakeLayer(MODEL_LAYER)),
              new PigeonModel(context.bakeLayer(BABY_MODEL_LAYER)),
              0.35f);
        addLayer(new PigeonBackpackLayer(this, context.getModelSet()));
        addLayer(new PigeonHatLayer(this, context.getModelSet()));
    }

    @Override
    public @NotNull Identifier getTextureLocation(PigeonModel.RenderState state) {
        return state.texture;
    }

    @Override
    public PigeonModel.RenderState createRenderState() {
        return new PigeonModel.RenderState();
    }

    @Override
    public void extractRenderState(Pigeon entity, PigeonModel.RenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.isFlying = entity.isFlying();
        state.isSitting = entity.isSitting();
        state.hasMail = entity.hasMail();
        state.hasMailmanHat = entity.hasMailmanHat();
        state.eatingTicks = entity.getEatingTicks();
        state.partialTick = partialTick;
        state.ageInTicks = entity.tickCount + partialTick;
        state.customName = entity.getCustomName() == null ? "" : entity.getCustomName().getString();
        float flap = Mth.lerp(partialTick, entity.oFlap, entity.flap);
        float flapSpeed = Mth.lerp(partialTick, entity.oFlapSpeed, entity.flapSpeed);
        state.flapBob = (Mth.sin(flap) + 1.0F) * flapSpeed;
        state.texture = entity.getVariant().value().texture();
    }
}
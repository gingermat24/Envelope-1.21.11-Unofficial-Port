package io.github.mortuusars.envelope.client.renderer.entity;

import io.github.mortuusars.envelope.Envelope;
import io.github.mortuusars.envelope.client.model.CourierBatModel;
import io.github.mortuusars.envelope.client.renderer.entity.layer.BatBackpackLayer;
import io.github.mortuusars.envelope.world.entity.CourierBat;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public class CourierBatRenderer extends MobRenderer<CourierBat, CourierBatModel.RenderState, CourierBatModel> {
    public static final ModelLayerLocation MODEL_LAYER = new ModelLayerLocation(Envelope.resource("courier_bat"), "main");

    public CourierBatRenderer(EntityRendererProvider.Context context) {
        super(context, new CourierBatModel(context.bakeLayer(MODEL_LAYER)), 0.25F);
        addLayer(new BatBackpackLayer(this, context.getModelSet()));
    }

    @Override
    public @NotNull Identifier getTextureLocation(CourierBatModel.RenderState state) {
        return Identifier.fromNamespaceAndPath("minecraft", "textures/entity/bat.png");
    }

    @Override
    public CourierBatModel.RenderState createRenderState() {
        return new CourierBatModel.RenderState();
    }

    @Override
    public void extractRenderState(CourierBat entity, CourierBatModel.RenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.hasMail = entity.hasMail();
        state.ageInTicks = entity.tickCount + partialTick;
    }
}

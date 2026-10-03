package io.github.mortuusars.envelope.mixin.bugger;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.mortuusars.envelope.client.renderer.entity.state.EntityRenderStateAccess;
import io.github.mortuusars.envelope.util.bugger.BuggerEntityOverhead;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity> {
    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void envelope$storeSourceEntity(T entity, EntityRenderState renderState, float partialTick, CallbackInfo ci) {
        ((EntityRenderStateAccess) renderState).envelope$setEntity(entity);
    }

    @Inject(method = "submit", at = @At("TAIL"))
    private void envelope$submitOverhead(EntityRenderState renderState, PoseStack poseStack,
                                         SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState,
                                         CallbackInfo ci) {
        Entity entity = ((EntityRenderStateAccess) renderState).envelope$getEntity();
        if (entity != null) {
            BuggerEntityOverhead.submitEntityInfo(entity, renderState, poseStack, submitNodeCollector, cameraRenderState);
        }
    }
}

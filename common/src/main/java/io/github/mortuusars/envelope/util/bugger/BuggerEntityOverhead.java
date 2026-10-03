package io.github.mortuusars.envelope.util.bugger;

import io.github.mortuusars.envelope.client.util.Minecrft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityAttachment;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BuggerEntityOverhead {
    private static final ArrayList<Component> LINES = new ArrayList<>();
    private static final ArrayList<EntityDataDisplay> DATA = new ArrayList<>();

    public static void addData(EntityDataDisplay data) {
        DATA.add(data);
    }

    public static void submitEntityInfo(Entity entity, EntityRenderState renderState, PoseStack poseStack,
                                        SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        if (!BuggerDebugScreen.active()) return;
        if (renderState.distanceToCameraSq > 4096.0) return;

        float partialTick = renderState.ageInTicks - entity.tickCount;
        @Nullable Vec3 attachment = entity.getAttachments().getNullable(
              EntityAttachment.NAME_TAG, 0, entity.getViewYRot(partialTick));
        if (attachment == null) return;

        List<Component> lines = getOverheadEntityInfoLines(entity);
        if (lines.isEmpty()) return;

        poseStack.pushPose();
        poseStack.translate(0.0, 0.5, 0.0);
        int lineHeight = Minecrft.get().font.lineHeight + 1;
        for (int index = 0; index < lines.size(); index++) {
            int verticalOffset = (index - lines.size()) * lineHeight;
            submitNodeCollector.submitNameTag(poseStack, attachment, verticalOffset, lines.get(index), true,
                  renderState.lightCoords, renderState.distanceToCameraSq, cameraRenderState);
        }
        poseStack.popPose();
    }

    private static List<Component> getOverheadEntityInfoLines(Entity entity) {
        LINES.clear();
        DATA.forEach(data -> data.addLines(entity, LINES));
        return LINES;
    }

    public interface EntityDataDisplay {
        void addLines(Entity entity, ArrayList<Component> lines);

        default MutableComponent line(String text) {
            return Component.literal(text);
        }
    }
}

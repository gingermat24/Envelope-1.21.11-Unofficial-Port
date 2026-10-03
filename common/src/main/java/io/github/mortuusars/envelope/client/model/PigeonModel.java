package io.github.mortuusars.envelope.client.model;

import io.github.mortuusars.envelope.util.EasingFunction;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class PigeonModel extends EntityModel<PigeonModel.RenderState> {
    private final ModelPart root;

    private final ModelPart head;
    private final ModelPart beak;
    private final ModelPart hat;

    private final ModelPart body;
    private final ModelPart backpackStraps;
    private final ModelPart backpackBag;

    private final ModelPart leftLeg;
    private final ModelPart rightLeg;
    private final ModelPart tail;
    private final ModelPart leftWing;
    private final ModelPart rightWing;

    public PigeonModel(ModelPart root) {
        super(root);
        this.root = root;

        head = root.getChild("head");
        beak = head.getChild("beak");
        hat = head.getChild("hat");

        body = root.getChild("body");
        backpackStraps = body.getChild("backpack_straps");
        backpackBag = body.getChild("backpack_bag");

        leftLeg = root.getChild("left_leg");
        rightLeg = root.getChild("right_leg");
        tail = root.getChild("tail");
        leftWing = root.getChild("left_wing");
        rightWing = root.getChild("right_wing");
    }

    public static LayerDefinition createLayerDefinition() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition part = mesh.getRoot();

        PartDefinition head = part.addOrReplaceChild("head",
              CubeListBuilder.create()
                    .texOffs(0, 14)
                    .addBox(-3.0F, -6.0F, -3.0F, 6.0F, 6.0F, 6.0F, CubeDeformation.NONE),
              PartPose.offset(0.0F, 18f, -3.0F));

        PartDefinition beak = head.addOrReplaceChild("beak",
              CubeListBuilder.create()
                    .texOffs(0, 14)
                    .addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.01F))
                    .texOffs(0, 16)
                    .addBox(0.0F, 0.0F, -2.0F, 0.0F, 1.0F, 1.0F, CubeDeformation.NONE),
              PartPose.offset(0.0F, -3.0F, -3.0F));

        PartDefinition hat = head.addOrReplaceChild("hat",
              CubeListBuilder.create()
                    .texOffs(1, 46)
                    .addBox(-3.5F, -2.0F, -3.5F, 7.0F, 2.0F, 7.0F, CubeDeformation.NONE)
                    .texOffs(0, 55)
                    .addBox(-3.5F, 0.0F, -4.5F, 7.0F, 1.0F, 8.0F, CubeDeformation.NONE),
              PartPose.offsetAndRotation(0.0F, -5.75F, 0.0F, -0.1745F, 0.0F, 0.0F));

        PartDefinition body = part.addOrReplaceChild("body",
              CubeListBuilder.create()
                    .texOffs(0, 0)
                    .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 6.0F, 8.0F, CubeDeformation.NONE),
              PartPose.offset(0.0F, 24.05F, 0.0F));

        PartDefinition backpackStraps = body.addOrReplaceChild("backpack_straps",
              CubeListBuilder.create()
                    .texOffs(32, 50)
                    .addBox(-4.0F, -3.0F, -4.0F, 8.0F, 6.0F, 8.0F, new CubeDeformation(0, 0.01F, 0.01F)),
              PartPose.offset(0.0F, -5.0F, 0.0F));

        PartDefinition backpackBag = body.addOrReplaceChild("backpack_bag",
              CubeListBuilder.create()
                    .texOffs(32, 38)
                    .addBox(-5.0F, -3.0F, -3.0F, 10.0F, 6.0F, 6.0F, CubeDeformation.NONE),
              PartPose.offsetAndRotation(0.0F, -9.0F, 2.0F, 0.7854F, 0.0F, 0.0F));


        PartDefinition leftLeg = part.addOrReplaceChild("left_leg", CubeListBuilder.create()
                    .texOffs(9, 26)
                    .addBox(0.0F, 0.0F, 0.0F, 1.0F, 2.0F, 0.0F, CubeDeformation.NONE)
                    .texOffs(6, 28)
                    .addBox(-1.0F, 2.0F, -3.0F, 3.0F, 0.0F, 3.0F, CubeDeformation.NONE),
              PartPose.offset(1.0F, 22.0F, 1.0F));

        PartDefinition rightLeg = part.addOrReplaceChild("right_leg", CubeListBuilder.create()
                    .texOffs(7, 26)
                    .addBox(-1.0F, 0.0F, 0.0F, 1.0F, 2.0F, 0.0F, CubeDeformation.NONE)
                    .texOffs(0, 28)
                    .addBox(-2.0F, 2.0F, -3.0F, 3.0F, 0.0F, 3.0F, CubeDeformation.NONE),
              PartPose.offset(-1.0F, 22.0F, 1.0F));

        PartDefinition tail = part.addOrReplaceChild("tail",
              CubeListBuilder.create()
                    .texOffs(24, 0)
                    .addBox(-3.0F, 0.0F, -1.0F, 6.0F, 5.0F, 1.0F, CubeDeformation.NONE),
              PartPose.offsetAndRotation(0.0F, 19.0F, 4.0F, 0.7854F, 0.0F, 0.0F));

        PartDefinition leftWing = part.addOrReplaceChild("left_wing",
              CubeListBuilder.create()
                    .texOffs(46, 3).mirror()
                    .addBox(-1.0F, 0.0F, -3.0F, 1.0F, 4.0F, 6.0F, CubeDeformation.NONE).mirror(false),
              PartPose.offsetAndRotation(4.0F, 17.0F, 0.0F, 0.0F, 0.0F, -0.3927F));

        PartDefinition rightWing = part.addOrReplaceChild("right_wing",
              CubeListBuilder.create()
                    .texOffs(32, 3)
                    .addBox(0.0F, 0.0F, -3.0F, 1.0F, 4.0F, 6.0F, CubeDeformation.NONE),
              PartPose.offsetAndRotation(-4.0F, 17.0F, 0.0F, 0.0F, 0.0F, 0.3927F));

        return LayerDefinition.create(mesh, 64, 64);
    }

    public void setupAnim(RenderState state) {
        super.setupAnim(state);

        head.xRot = state.xRot * (float) (Math.PI / 180.0);
        head.yRot = state.yRot * (float) (Math.PI / 180.0);

        switch (getState(state)) {
            case FLYING -> {
                leftWing.zRot = -(state.flapBob + 0.3f);
                rightWing.zRot = state.flapBob + 0.3f;

                leftLeg.xRot = 0.75f * state.walkAnimationSpeed;
                leftLeg.yRot = -0.15f;
                rightLeg.xRot = 0.75f * state.walkAnimationSpeed;
                rightLeg.yRot = 0.15f;

                // Rock back and forth
                float anim = (state.ageInTicks % 60) / 60;
                anim *= 2;
                if (anim > 1) {
                    anim = 2 - anim;
                }
                anim = ((float) EasingFunction.EASE_IN_OUT_QUAD.ease(anim));
                body.xRot = anim * 0.05f;
                head.xRot += -anim * 0.025f;
                head.z -= 0.5F * (anim);

                leftWing.xRot += (anim * 0.1f);
                leftWing.y -= 0.3F * (anim);
                leftWing.z -= 0.5F * (anim);

                rightWing.xRot += (anim * 0.1f);
                rightWing.y -= 0.3F * (anim);
                rightWing.z -= 0.5F * (anim);

                tail.xRot += (state.walkAnimationSpeed * 0.5F) + (anim * 0.1f);
                tail.y -= 0.3F * (anim);
                tail.z -= 0.4F * (anim);
            }
            case STANDING -> {
                leftWing.zRot = -0.3927F;
                rightWing.zRot = 0.3927F;
                leftLeg.xRot = Mth.cos(state.walkAnimationPos * 0.6662F + (float) Math.PI) * 1.4F * state.walkAnimationSpeed;
                rightLeg.xRot = Mth.cos(state.walkAnimationPos * 0.6662F) * 1.4F * state.walkAnimationSpeed;
            }
            case SITTING -> {
                body.y += 1.9F;
                head.y += state.isBaby ? 1.9F : 2.9F;

                leftWing.zRot = -0.2F;
                rightWing.zRot = 0.2F;
                leftWing.y += 2;
                rightWing.y += 2;

                tail.xRot = 0.75f;
            }
        }

        int eatingTicks = state.eatingTicks;
        if (eatingTicks > 0) {
            float anim = Mth.clamp((eatingTicks + state.partialTick) / 10f, 0.0f, 1.0f);
            anim *= 2;
            if (anim > 1) {
                anim = 2 - anim;
            }

            head.xRot += anim;
        }

        String customName = state.customName;
        if (customName.equalsIgnoreCase("drumstick") || customName.equalsIgnoreCase("matisslee")) {
            body.y += 0.5f;
            body.z -= 0.5f;
            head.y += 0.5f;
            head.z -= 0.75f;
            head.xScale = 1.1f;
            body.xScale = 1.15f;
            body.yScale = 1.1f;
            body.zScale = 1.15f;
            beak.xScale = 1.2f;
            beak.zScale = 0.75f;
        }
    }

    public State getState(RenderState state) {
        if (state.isSitting) return State.SITTING;
        return state.isFlying ? State.FLYING : State.STANDING;
    }

    public Iterable<ModelPart> headParts() {
        return java.util.List.of(head);
    }

    public Iterable<ModelPart> bodyParts() {
        return java.util.List.of(body, rightLeg, leftLeg, rightWing, leftWing, tail);
    }

    public static class RenderState extends LivingEntityRenderState {
        public boolean isFlying;
        public boolean isSitting;
        public int eatingTicks;
        public float flapBob;
        public float partialTick;
        public String customName = "";
        public net.minecraft.resources.Identifier texture;
        public boolean hasMail;
        public boolean hasMailmanHat;
    }

    public enum State {
        FLYING,
        STANDING,
        SITTING
    }
}

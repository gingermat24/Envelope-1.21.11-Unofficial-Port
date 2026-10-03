package io.github.mortuusars.envelope.mixin.bugger;

import io.github.mortuusars.envelope.client.renderer.entity.state.EntityRenderStateAccess;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(EntityRenderState.class)
public abstract class EntityRenderStateMixin implements EntityRenderStateAccess {
    @Unique
    private Entity envelope$entity;

    @Override
    @Unique
    public Entity envelope$getEntity() {
        return envelope$entity;
    }

    @Override
    @Unique
    public void envelope$setEntity(Entity entity) {
        envelope$entity = entity;
    }
}

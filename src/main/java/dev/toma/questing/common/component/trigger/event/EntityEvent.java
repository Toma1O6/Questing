package dev.toma.questing.common.component.trigger.event;

import net.minecraft.entity.Entity;

public class EntityEvent {

    private final Entity entity;

    public EntityEvent(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }
}

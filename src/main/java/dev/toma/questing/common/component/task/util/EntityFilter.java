package dev.toma.questing.common.component.task.util;

import net.minecraft.entity.Entity;

public interface EntityFilter {

    EntityFilterType<?> getType();

    boolean acceptEntity(Entity entity);
}

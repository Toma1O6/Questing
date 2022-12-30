package dev.toma.questing.common.task.util;

import net.minecraft.entity.Entity;

public interface EntityFilter {

    EntityFilterType<?> getType();

    boolean acceptEntity(Entity entity);
}

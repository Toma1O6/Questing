package dev.toma.questing.common.trigger.event;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;

public class DeathEvent {

    private final DamageSource source;
    private final LivingEntity entity;

    public DeathEvent(DamageSource source, LivingEntity entity) {
        this.source = source;
        this.entity = entity;
    }

    public DamageSource getSource() {
        return source;
    }

    public LivingEntity getEntity() {
        return entity;
    }
}

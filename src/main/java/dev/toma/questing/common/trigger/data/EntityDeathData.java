package dev.toma.questing.common.trigger.data;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;

public class EntityDeathData {

    private final DamageSource source;
    private final LivingEntity entity;

    public EntityDeathData(DamageSource source, LivingEntity entity) {
        this.entity = entity;
        this.source = source;
    }

    public DamageSource getSource() {
        return source;
    }

    public LivingEntity getEntity() {
        return entity;
    }
}

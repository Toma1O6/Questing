package dev.toma.questing.common.trigger.data;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;

public final class AttackEntityData extends EntityDeathData {

    private final float damageAmount;

    public AttackEntityData(DamageSource damageSource, LivingEntity entity, float damageAmount) {
        super(damageSource, entity);
        this.damageAmount = damageAmount;
    }

    public float getDamageAmount() {
        return damageAmount;
    }
}

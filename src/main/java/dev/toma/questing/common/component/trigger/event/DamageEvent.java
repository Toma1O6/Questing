package dev.toma.questing.common.component.trigger.event;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;

public class DamageEvent extends DeathEvent {

    private final float amount;

    public DamageEvent(DamageSource source, LivingEntity entity, float amount) {
        super(source, entity);
        this.amount = amount;
    }

    public float getAmount() {
        return amount;
    }
}

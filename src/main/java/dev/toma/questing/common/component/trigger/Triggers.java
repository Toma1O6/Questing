package dev.toma.questing.common.component.trigger;

import dev.toma.questing.Questing;
import dev.toma.questing.common.component.trigger.data.AttackEntityData;
import dev.toma.questing.common.component.trigger.data.EntityDeathData;
import dev.toma.questing.common.component.trigger.event.DamageEvent;
import dev.toma.questing.common.component.trigger.event.DeathEvent;
import dev.toma.questing.common.component.trigger.event.EntityEvent;
import net.minecraft.util.ResourceLocation;

public final class Triggers {

    public static final Trigger<EntityDeathData> ENTITY_DIED = Trigger.create(id("entity_death"));
    public static final Trigger<AttackEntityData> ENTITY_ATTACKED = Trigger.create(id("entity_attack"));

    private static ResourceLocation id(String path) {
        return new ResourceLocation(Questing.MODID, path);
    }

    static {
        ENTITY_DIED.setEventMapping(Events.DEATH_EVENT, data -> new DeathEvent(data.getSource(), data.getEntity()));
        ENTITY_DIED.setEventMapping(Events.ENTITY_EVENT, data -> new EntityEvent(data.getEntity()));
        ENTITY_ATTACKED.setEventMapping(Events.DAMAGE_EVENT, data -> new DamageEvent(data.getSource(), data.getEntity(), data.getDamageAmount()));
        ENTITY_ATTACKED.setEventMapping(Events.ENTITY_EVENT, data -> new EntityEvent(data.getEntity()));
    }
}

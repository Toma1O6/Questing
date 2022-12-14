package dev.toma.questing.common.component.trigger;

import dev.toma.questing.Questing;
import dev.toma.questing.common.component.trigger.event.DeathEvent;
import dev.toma.questing.common.component.trigger.event.TriggerEvent;
import dev.toma.questing.common.component.trigger.event.DamageEvent;
import dev.toma.questing.common.component.trigger.event.EntityEvent;
import net.minecraft.util.ResourceLocation;

public final class Events {

    public static final EventType<TriggerEvent> EVENT = EventType.create(id("trigger_event"));
    public static final EventType<EntityEvent> ENTITY_EVENT = EventType.create(id("entity"));
    public static final EventType<DeathEvent> DEATH_EVENT = EventType.create(id("death"));
    public static final EventType<DamageEvent> DAMAGE_EVENT = EventType.create(id("damage"));

    private static ResourceLocation id(String path) {
        return new ResourceLocation(Questing.MODID, path);
    }
}

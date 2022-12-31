package dev.toma.questing.common.component.trigger;

import dev.toma.questing.common.component.trigger.event.TriggerEvent;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public final class Trigger<T> {

    private final ResourceLocation identifier;
    private final Map<EventType<?>, Function<T, ?>> eventMappings = new IdentityHashMap<>();

    private Trigger(ResourceLocation identifier) {
        this.identifier = identifier;
    }

    public static <T> Trigger<T> create(ResourceLocation identifier) {
        Trigger<T> trigger = new Trigger<>(identifier);
        trigger.setEventMapping(Events.EVENT, data -> TriggerEvent.INSTANCE);
        return trigger;
    }

    public <V> void setEventMapping(EventType<V> eventType, @Nullable Function<T, V> eventDataMapper) {
        if (eventDataMapper != null) {
            this.eventMappings.put(eventType, eventDataMapper);
        } else {
            this.eventMappings.remove(eventType);
        }

    }

    public Map<EventType<?>, Function<T, ?>> getEventMappings() {
        return eventMappings;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Trigger<?> trigger = (Trigger<?>) o;
        return identifier.equals(trigger.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }

    @Override
    public String toString() {
        return "Trigger{" +
                "identifier=" + identifier +
                '}';
    }
}

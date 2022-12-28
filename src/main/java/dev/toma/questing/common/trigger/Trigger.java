package dev.toma.questing.common.trigger;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public final class Trigger<T> {

    private static int triggerIndexPool;
    private final int triggerIndex;
    private final Map<EventType<?>, Function<T, ?>> eventMappings = new IdentityHashMap<>();

    private Trigger(int triggerIndex) {
        this.triggerIndex = triggerIndex;
    }

    public static <T> Trigger<T> createTrigger() {
        return new Trigger<>(triggerIndexPool++);
    }

    public <V> void setEventMapping(EventType<V> eventType, Function<T, V> eventDataMapper) {
        this.eventMappings.put(eventType, eventDataMapper);
    }

    public Map<EventType<?>, Function<T, ?>> getEventMappings() {
        return eventMappings;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Trigger<?> trigger = (Trigger<?>) o;
        return triggerIndex == trigger.triggerIndex;
    }

    @Override
    public int hashCode() {
        return Objects.hash(triggerIndex);
    }
}

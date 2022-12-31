package dev.toma.questing.common.component.trigger;

import net.minecraft.util.ResourceLocation;

import java.util.Objects;

public final class EventType<T> {

    private final ResourceLocation identifier;

    private EventType(ResourceLocation identifier) {
        this.identifier = identifier;
    }

    public static <T> EventType<T> create(ResourceLocation identifier) {
        return new EventType<>(identifier);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventType<?> eventType = (EventType<?>) o;
        return identifier.equals(eventType.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }

    @Override
    public String toString() {
        return "EventType{" +
                "identifier=" + identifier +
                '}';
    }
}

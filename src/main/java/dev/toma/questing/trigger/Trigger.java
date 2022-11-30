package dev.toma.questing.trigger;

import java.util.Objects;

public final class Trigger<T extends TriggerData> {

    private static int triggerIndexPool;
    private final int triggerIndex;

    private Trigger(int triggerIndex) {
        this.triggerIndex = triggerIndex;
    }

    public static <T extends TriggerData> Trigger<T> createTrigger() {
        return new Trigger<>(triggerIndexPool++);
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

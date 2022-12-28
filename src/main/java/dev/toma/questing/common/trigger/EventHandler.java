package dev.toma.questing.common.trigger;

@FunctionalInterface
public interface EventHandler<T> {

    void handleEvent(T eventData);
}

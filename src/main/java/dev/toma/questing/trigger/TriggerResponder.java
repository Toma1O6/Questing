package dev.toma.questing.trigger;

@FunctionalInterface
public interface TriggerResponder<T> {

    TriggerResponse onTrigger(Trigger<T> trigger, T data);
}

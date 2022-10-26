package dev.toma.questing.trigger;

@FunctionalInterface
public interface ITriggerResponder<T> {

    TriggerResponse onTrigger(Trigger<T> trigger, T data);
}

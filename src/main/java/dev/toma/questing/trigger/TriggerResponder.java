package dev.toma.questing.trigger;

@FunctionalInterface
public interface TriggerResponder<T extends TriggerData> {

    TriggerResponse onTrigger(Trigger<T> trigger, T data);
}

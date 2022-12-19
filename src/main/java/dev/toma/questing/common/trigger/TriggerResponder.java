package dev.toma.questing.common.trigger;

@FunctionalInterface
public interface TriggerResponder<T extends TriggerData> {

    TriggerResponse onTrigger(Trigger<T> trigger, T data);
}

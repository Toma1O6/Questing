package dev.toma.questing.common.trigger;

@FunctionalInterface
public interface TriggerResponder<T> {

    ResponseType onTrigger(T data);
}

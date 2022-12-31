package dev.toma.questing.common.component.trigger;

@FunctionalInterface
public interface TriggerResponder<T> {

    ResponseType onTrigger(T data);
}

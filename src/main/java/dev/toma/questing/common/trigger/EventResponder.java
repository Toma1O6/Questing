package dev.toma.questing.common.trigger;

@FunctionalInterface
public interface EventResponder<T> {

    ResponseType respond(T eventData);
}

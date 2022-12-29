package dev.toma.questing.common.condition;

import dev.toma.questing.common.trigger.*;

@FunctionalInterface
public interface ConditionRegisterHandler {

    <T> void registerWithHandler(EventType<T> eventType, EventResponder<T> responder, EventHandler<T> handler);

    default <T> void register(EventType<T> eventType, EventResponder<T> responder) {
        registerWithHandler(eventType, responder, (data, quest) -> {});
    }
}

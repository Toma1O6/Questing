package dev.toma.questing.common.component.condition;

import dev.toma.questing.common.component.trigger.EventHandler;
import dev.toma.questing.common.component.trigger.EventResponder;
import dev.toma.questing.common.component.trigger.EventType;

@FunctionalInterface
public interface ConditionRegisterHandler {

    <T> void registerWithHandler(EventType<T> eventType, EventResponder<T> responder, EventHandler<T> handler);

    default <T> void register(EventType<T> eventType, EventResponder<T> responder) {
        registerWithHandler(eventType, responder, (data, quest) -> {});
    }
}

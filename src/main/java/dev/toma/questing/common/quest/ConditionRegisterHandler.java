package dev.toma.questing.common.quest;

import dev.toma.questing.common.component.trigger.ActionHandler;
import dev.toma.questing.common.component.trigger.ActionResponder;
import dev.toma.questing.common.component.trigger.EventType;

@FunctionalInterface
public interface ConditionRegisterHandler {

    <T> void registerWithHandler(EventType<T> eventType, ActionResponder<T> responder, ActionHandler<T> handler);

    default <T> void register(EventType<T> eventType, ActionResponder<T> responder) {
        registerWithHandler(eventType, responder, ActionHandler.none());
    }
}

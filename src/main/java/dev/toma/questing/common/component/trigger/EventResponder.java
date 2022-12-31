package dev.toma.questing.common.component.trigger;

import dev.toma.questing.common.quest.Quest;

@FunctionalInterface
public interface EventResponder<T> {

    ResponseType respond(T eventData, Quest quest);
}

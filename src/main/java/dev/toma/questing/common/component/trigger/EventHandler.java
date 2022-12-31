package dev.toma.questing.common.component.trigger;

import dev.toma.questing.common.quest.Quest;

@FunctionalInterface
public interface EventHandler<T> {

    void handleEvent(T eventData, Quest quest);
}

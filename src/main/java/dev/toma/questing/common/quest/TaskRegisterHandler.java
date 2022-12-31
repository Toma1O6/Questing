package dev.toma.questing.common.quest;

import dev.toma.questing.common.component.trigger.ActionHandler;
import dev.toma.questing.common.component.trigger.ActionResponder;
import dev.toma.questing.common.component.trigger.Trigger;

@FunctionalInterface
public interface TaskRegisterHandler {

    <T> void registerTask(Trigger<T> trigger, ActionResponder<T> responder, ActionHandler<T> handler);
}

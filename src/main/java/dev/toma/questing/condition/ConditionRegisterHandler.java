package dev.toma.questing.condition;

import dev.toma.questing.trigger.Trigger;
import dev.toma.questing.trigger.TriggerData;
import dev.toma.questing.trigger.TriggerResponder;

@FunctionalInterface
public interface ConditionRegisterHandler {

    <T extends TriggerData> void registerHandler(Trigger<T> trigger, TriggerResponder<T> responder, ConditionCriterion<T> conditionCriterion);

    default <T extends TriggerData> void registerHandler(Trigger<T> trigger, TriggerResponder<T> responder) {
        registerHandler(trigger, responder, data -> true);
    }
}

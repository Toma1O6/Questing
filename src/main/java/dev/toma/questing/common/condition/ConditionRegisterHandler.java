package dev.toma.questing.common.condition;

import dev.toma.questing.common.trigger.Trigger;
import dev.toma.questing.common.trigger.TriggerData;
import dev.toma.questing.common.trigger.TriggerResponder;

@FunctionalInterface
public interface ConditionRegisterHandler {

    <T extends TriggerData> void registerHandler(Trigger<T> trigger, TriggerResponder<T> responder);
}

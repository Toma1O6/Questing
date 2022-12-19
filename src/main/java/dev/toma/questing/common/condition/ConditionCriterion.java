package dev.toma.questing.common.condition;

import dev.toma.questing.common.trigger.TriggerData;

@FunctionalInterface
public interface ConditionCriterion<T extends TriggerData> {

    boolean shouldCheck(T data);

    default ConditionCriterion<T> and(ConditionCriterion<? super T> other) {
        return data -> this.shouldCheck(data) && other.shouldCheck(data);
    }

    default ConditionCriterion<T> or(ConditionCriterion<? super T> other) {
        return data -> this.shouldCheck(data) || other.shouldCheck(data);
    }
}

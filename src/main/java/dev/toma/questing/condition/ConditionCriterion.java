package dev.toma.questing.condition;

import dev.toma.questing.trigger.TriggerData;

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

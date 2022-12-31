package dev.toma.questing.common.component.condition.provider;

import dev.toma.questing.common.component.condition.instance.Condition;
import dev.toma.questing.common.component.condition.provider.ConditionProvider;
import dev.toma.questing.common.component.trigger.ResponseType;

public abstract class AbstractDefaultConditionProvider<C extends Condition> implements ConditionProvider<C> {

    private final ResponseType defaultResponseType;

    public AbstractDefaultConditionProvider(ResponseType defaultResponseType) {
        this.defaultResponseType = defaultResponseType;
    }

    public ResponseType getDefaultFailureResponse() {
        return defaultResponseType;
    }
}

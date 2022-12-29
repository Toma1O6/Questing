package dev.toma.questing.common.condition;

import dev.toma.questing.common.trigger.ResponseType;

public abstract class AbstractDefaultCondition implements Condition {

    private final ResponseType defaultResponseType;

    public AbstractDefaultCondition(ResponseType defaultResponseType) {
        this.defaultResponseType = defaultResponseType;
    }

    public ResponseType getDefaultFailureResponse() {
        return defaultResponseType;
    }
}

package dev.toma.questing.common.component.task.provider;

import dev.toma.questing.common.component.condition.provider.ConditionProvider;
import dev.toma.questing.common.component.task.instance.Task;
import dev.toma.questing.common.component.trigger.ResponseType;

import java.util.List;

public abstract class AbstractTaskProvider<T extends Task> implements TaskProvider<T> {

    private final ResponseType defaultResponse;
    private final List<ConditionProvider<?>> conditions;
    private final boolean optional;

    public AbstractTaskProvider(ResponseType defaultResponse, List<ConditionProvider<?>> conditions, boolean optional) {
        this.defaultResponse = defaultResponse;
        this.conditions = conditions;
        this.optional = optional;
    }

    @Override
    public List<ConditionProvider<?>> getConditions() {
        return conditions;
    }

    @Override
    public boolean isOptional() {
        return optional;
    }

    public ResponseType getDefaultResponse() {
        return defaultResponse;
    }
}

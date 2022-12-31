package dev.toma.questing.common.component.task.provider;

import dev.toma.questing.common.component.condition.provider.ConditionProvider;
import dev.toma.questing.common.component.task.TaskType;
import dev.toma.questing.common.component.task.instance.Task;
import dev.toma.questing.common.quest.instance.Quest;

import java.util.List;

public interface TaskProvider<T extends Task> {

    List<ConditionProvider<?>> getConditions();

    boolean isOptional();

    TaskType<T, ?> getType();

    T createTaskInstance(Quest quest);
}

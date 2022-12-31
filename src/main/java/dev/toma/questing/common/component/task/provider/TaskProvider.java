package dev.toma.questing.common.component.task.provider;

import dev.toma.questing.common.component.task.TaskType;
import dev.toma.questing.common.component.task.instance.Task;

public interface TaskProvider<T extends Task> {

    TaskType<T, ?> getType();

    T createTaskInstance();
}

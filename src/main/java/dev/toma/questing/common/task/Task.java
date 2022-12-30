package dev.toma.questing.common.task;

public interface Task<T extends TaskInstance> {

    TaskType<T, ?> getType();

    T createTaskInstance();
}

package dev.toma.questing.common.component.task.instance;

import dev.toma.questing.common.component.task.provider.TaskProvider;

public interface Task {

    TaskProvider<?> getProvider();
}

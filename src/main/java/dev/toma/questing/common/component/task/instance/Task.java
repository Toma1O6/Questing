package dev.toma.questing.common.component.task.instance;

import dev.toma.questing.common.component.condition.instance.Condition;
import dev.toma.questing.common.component.task.provider.TaskProvider;
import dev.toma.questing.common.quest.ProgressStatus;
import dev.toma.questing.common.quest.TaskRegisterHandler;

import java.util.List;

public interface Task {

    void registerTriggerHandlers(TaskRegisterHandler registerHandler);

    void setStatus(ProgressStatus status);

    ProgressStatus getStatus();

    List<Condition> getTaskConditions();

    TaskProvider<?> getProvider();
}

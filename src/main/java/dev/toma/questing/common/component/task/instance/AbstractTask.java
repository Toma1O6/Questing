package dev.toma.questing.common.component.task.instance;

import dev.toma.questing.common.component.condition.instance.Condition;
import dev.toma.questing.common.quest.ProgressStatus;

import java.util.List;

public abstract class AbstractTask implements Task {

    private final List<Condition> conditions;
    private ProgressStatus status;

    public AbstractTask(ProgressStatus status, List<Condition> conditions) {
        this.status = status;
        this.conditions = conditions;
    }

    public AbstractTask(List<Condition> conditions) {
        this(ProgressStatus.ACTIVE, conditions);
    }

    @Override
    public List<Condition> getTaskConditions() {
        return this.conditions;
    }

    @Override
    public ProgressStatus getStatus() {
        return status;
    }

    @Override
    public void setStatus(ProgressStatus status) {
        this.status = status;
    }
}

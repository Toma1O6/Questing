package dev.toma.questing.common.quest.provider;

import dev.toma.questing.common.component.condition.provider.ConditionProvider;
import dev.toma.questing.common.component.distributor.RewardDistributor;
import dev.toma.questing.common.component.task.provider.TaskProvider;
import dev.toma.questing.common.quest.instance.Quest;
import net.minecraft.util.ResourceLocation;

import java.util.List;

public abstract class AbstractQuestProvider<Q extends Quest> implements QuestProvider<Q> {

    private final ResourceLocation identifier;
    private final List<ConditionProvider<?>> conditions;
    private final List<TaskProvider<?>> tasks;
    private final RewardDistributor distributor;

    public AbstractQuestProvider(ResourceLocation identifier, List<ConditionProvider<?>> conditions, List<TaskProvider<?>> tasks, RewardDistributor distributor) {
        this.identifier = identifier;
        this.conditions = conditions;
        this.tasks = tasks;
        this.distributor = distributor;
    }

    @Override
    public ResourceLocation getIdentifier() {
        return identifier;
    }

    @Override
    public List<ConditionProvider<?>> getConditions() {
        return conditions;
    }

    @Override
    public List<TaskProvider<?>> getTasks() {
        return tasks;
    }

    @Override
    public RewardDistributor getRewardDistributor() {
        return distributor;
    }
}

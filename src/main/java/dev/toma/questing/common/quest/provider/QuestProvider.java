package dev.toma.questing.common.quest.provider;

import dev.toma.questing.common.component.condition.provider.ConditionProvider;
import dev.toma.questing.common.component.distributor.RewardDistributor;
import dev.toma.questing.common.component.task.provider.TaskProvider;
import dev.toma.questing.common.quest.QuestType;
import dev.toma.questing.common.quest.instance.Quest;

import java.util.List;

public interface QuestProvider<Q extends Quest> {

    List<ConditionProvider<?>> getConditions();

    List<TaskProvider<?>> getTasks();

    RewardDistributor getRewardDistributor();

    QuestType<Q, ?> getType();

    Q createQuest();
}

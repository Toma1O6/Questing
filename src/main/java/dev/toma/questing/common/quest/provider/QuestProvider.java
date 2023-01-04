package dev.toma.questing.common.quest.provider;

import dev.toma.questing.common.component.condition.provider.ConditionProvider;
import dev.toma.questing.common.component.distributor.RewardDistributor;
import dev.toma.questing.common.component.task.provider.TaskProvider;
import dev.toma.questing.common.quest.QuestType;
import dev.toma.questing.common.quest.instance.Quest;
import dev.toma.questing.utils.IdentifierHolder;
import net.minecraft.util.ResourceLocation;

import java.util.List;

public interface QuestProvider<Q extends Quest> extends IdentifierHolder {

    List<ConditionProvider<?>> getConditions();

    List<TaskProvider<?>> getTasks();

    RewardDistributor getRewardDistributor();

    QuestType<Q, ?> getType();

    Q createQuest();
}

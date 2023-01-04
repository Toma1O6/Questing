package dev.toma.questing.common.quest.provider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.toma.questing.common.component.condition.ConditionType;
import dev.toma.questing.common.component.condition.provider.ConditionProvider;
import dev.toma.questing.common.component.distributor.NoRewardDistributor;
import dev.toma.questing.common.component.distributor.RewardDistributionType;
import dev.toma.questing.common.component.distributor.RewardDistributor;
import dev.toma.questing.common.component.task.TaskType;
import dev.toma.questing.common.component.task.provider.TaskProvider;
import dev.toma.questing.common.init.QuestingRegistries;
import dev.toma.questing.common.quest.QuestType;
import dev.toma.questing.common.quest.instance.SimpleQuest;
import net.minecraft.util.ResourceLocation;

import java.util.Collections;
import java.util.List;

public class SimpleQuestProvider extends AbstractQuestProvider<SimpleQuest> {

    public static final Codec<SimpleQuestProvider> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(AbstractQuestProvider::getIdentifier),
            ConditionType.PROVIDER_CODEC.listOf().optionalFieldOf("conditions", Collections.emptyList()).forGetter(AbstractQuestProvider::getConditions),
            TaskType.CODEC.listOf().fieldOf("tasks").forGetter(AbstractQuestProvider::getTasks),
            RewardDistributionType.CODEC.optionalFieldOf("rewards", NoRewardDistributor.NONE).forGetter(AbstractQuestProvider::getRewardDistributor)
    ).apply(instance, SimpleQuestProvider::new));

    public SimpleQuestProvider(ResourceLocation identifier, List<ConditionProvider<?>> conditions, List<TaskProvider<?>> tasks, RewardDistributor distributor) {
        super(identifier, conditions, tasks, distributor);
    }

    @Override
    public SimpleQuest createQuest() {
        return new SimpleQuest(this);
    }

    @Override
    public QuestType<SimpleQuest, ?> getType() {
        return QuestingRegistries.QUEST;
    }
}

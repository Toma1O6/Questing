package dev.toma.questing.common.quest.provider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.toma.questing.common.component.area.AreaType;
import dev.toma.questing.common.component.area.provider.AreaProvider;
import dev.toma.questing.common.component.condition.ConditionType;
import dev.toma.questing.common.component.condition.provider.ConditionProvider;
import dev.toma.questing.common.component.distributor.NoRewardDistributor;
import dev.toma.questing.common.component.distributor.RewardDistributionType;
import dev.toma.questing.common.component.distributor.RewardDistributor;
import dev.toma.questing.common.component.task.TaskType;
import dev.toma.questing.common.component.task.provider.TaskProvider;
import dev.toma.questing.common.init.QuestingRegistries;
import dev.toma.questing.common.quest.QuestType;
import dev.toma.questing.common.quest.instance.SimpleAreaQuest;
import net.minecraft.util.ResourceLocation;

import java.util.Collections;
import java.util.List;

public class SimpleAreaQuestProvider extends AbstractAreaQuestProvider<SimpleAreaQuest> {

    public static final Codec<SimpleAreaQuestProvider> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(AbstractQuestProvider::getIdentifier),
            ConditionType.PROVIDER_CODEC.listOf().optionalFieldOf("conditions", Collections.emptyList()).forGetter(AbstractQuestProvider::getConditions),
            TaskType.CODEC.listOf().fieldOf("tasks").forGetter(AbstractQuestProvider::getTasks),
            RewardDistributionType.CODEC.optionalFieldOf("rewards", NoRewardDistributor.NONE).forGetter(AbstractQuestProvider::getRewardDistributor),
            AreaType.PROVIDER_CODEC.fieldOf("area").forGetter(AbstractAreaQuestProvider::getAreaProvider)
    ).apply(instance, SimpleAreaQuestProvider::new));

    public SimpleAreaQuestProvider(ResourceLocation identifier, List<ConditionProvider<?>> conditions, List<TaskProvider<?>> tasks, RewardDistributor distributor, AreaProvider<?> areaProvider) {
        super(identifier, conditions, tasks, distributor, areaProvider);
    }

    @Override
    public SimpleAreaQuest createQuest() {
        return new SimpleAreaQuest(this);
    }

    @Override
    public QuestType<SimpleAreaQuest, ?> getType() {
        return QuestingRegistries.AREA_QUEST;
    }
}

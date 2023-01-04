package dev.toma.questing.common.quest.provider;

import dev.toma.questing.common.component.area.provider.AreaProvider;
import dev.toma.questing.common.component.condition.provider.ConditionProvider;
import dev.toma.questing.common.component.distributor.RewardDistributor;
import dev.toma.questing.common.component.task.provider.TaskProvider;
import dev.toma.questing.common.quest.instance.AreaQuest;
import net.minecraft.util.ResourceLocation;

import java.util.List;

public abstract class AbstractAreaQuestProvider<Q extends AreaQuest> extends AbstractQuestProvider<Q> implements AreaQuestProvider<Q> {

    private final AreaProvider<?> areaProvider;

    public AbstractAreaQuestProvider(ResourceLocation identifier, List<ConditionProvider<?>> conditions, List<TaskProvider<?>> tasks, RewardDistributor distributor, AreaProvider<?> areaProvider) {
        super(identifier, conditions, tasks, distributor);
        this.areaProvider = areaProvider;
    }

    @Override
    public AreaProvider<?> getAreaProvider() {
        return areaProvider;
    }
}

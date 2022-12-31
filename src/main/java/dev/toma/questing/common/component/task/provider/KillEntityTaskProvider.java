package dev.toma.questing.common.component.task.provider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.toma.questing.common.component.condition.ConditionType;
import dev.toma.questing.common.component.condition.provider.ConditionProvider;
import dev.toma.questing.common.component.task.TaskType;
import dev.toma.questing.common.component.task.instance.KillEntityTask;
import dev.toma.questing.common.component.task.util.AnyEntityFilter;
import dev.toma.questing.common.component.task.util.EntityFilter;
import dev.toma.questing.common.component.task.util.EntityFilterType;
import dev.toma.questing.common.component.trigger.ResponseType;
import dev.toma.questing.common.init.QuestingRegistries;
import dev.toma.questing.common.quest.instance.Quest;
import dev.toma.questing.utils.Codecs;

import java.util.Collections;
import java.util.List;

public class KillEntityTaskProvider extends AbstractTaskProvider<KillEntityTask> {

    public static final Codec<KillEntityTaskProvider> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codecs.enumCodecComap(ResponseType.class, ResponseType::fromString, Enum::name, String::toUpperCase)
                    .optionalFieldOf("onFail", ResponseType.PASS).forGetter(AbstractTaskProvider::getDefaultResponse),
            ConditionType.PROVIDER_CODEC.listOf().optionalFieldOf("conditions", Collections.emptyList()).forGetter(TaskProvider::getConditions),
            Codec.BOOL.optionalFieldOf("optional", false).forGetter(TaskProvider::isOptional),
            EntityFilterType.CODEC.optionalFieldOf("filter", AnyEntityFilter.ANY_ENTITY).forGetter(t -> t.filter),
            Codec.INT.fieldOf("count").forGetter(t -> t.requiredKillCount)
    ).apply(instance, KillEntityTaskProvider::new));
    private final EntityFilter filter;
    private final int requiredKillCount;

    public KillEntityTaskProvider(ResponseType responseType, List<ConditionProvider<?>> conditions, boolean optional, EntityFilter filter, int requiredKillCount) {
        super(responseType, conditions, optional);
        this.filter = filter;
        this.requiredKillCount = requiredKillCount;
    }

    @Override
    public TaskType<KillEntityTask, ?> getType() {
        return QuestingRegistries.KILL_ENTITY_TASK;
    }

    @Override
    public KillEntityTask createTaskInstance(Quest quest) {
        return new KillEntityTask(this, quest);
    }

    public EntityFilter getFilter() {
        return filter;
    }

    public int getRequiredKillCount() {
        return requiredKillCount;
    }
}

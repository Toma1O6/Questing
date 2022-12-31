package dev.toma.questing.common.component.task.provider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.toma.questing.common.component.task.TaskType;
import dev.toma.questing.common.component.task.instance.KillEntityTask;
import dev.toma.questing.common.component.task.util.AnyEntityFilter;
import dev.toma.questing.common.component.task.util.EntityFilter;
import dev.toma.questing.common.component.task.util.EntityFilterType;
import dev.toma.questing.common.init.QuestingRegistries;

public class KillEntityTaskProvider implements TaskProvider<KillEntityTask> {

    public static final Codec<KillEntityTaskProvider> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            EntityFilterType.CODEC.optionalFieldOf("filter", AnyEntityFilter.ANY_ENTITY).forGetter(t -> t.filter),
            Codec.INT.fieldOf("count").forGetter(t -> t.requiredKillCount)
    ).apply(instance, KillEntityTaskProvider::new));
    private final EntityFilter filter;
    private final int requiredKillCount;

    public KillEntityTaskProvider(EntityFilter filter, int requiredKillCount) {
        this.filter = filter;
        this.requiredKillCount = requiredKillCount;
    }

    @Override
    public TaskType<KillEntityTask, ?> getType() {
        return QuestingRegistries.KILL_ENTITY_TASK;
    }

    @Override
    public KillEntityTask createTaskInstance() {
        return new KillEntityTask(this);
    }

}

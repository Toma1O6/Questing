package dev.toma.questing.common.task;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.toma.questing.common.init.QuestingRegistries;
import dev.toma.questing.common.task.util.AnyEntityFilter;
import dev.toma.questing.common.task.util.EntityFilter;
import dev.toma.questing.common.task.util.EntityFilterType;

public class KillEntityTask implements Task<KillEntityTaskInstance> {

    public static final Codec<KillEntityTask> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            EntityFilterType.CODEC.optionalFieldOf("filter", AnyEntityFilter.ANY_ENTITY).forGetter(t -> t.filter),
            Codec.INT.fieldOf("count").forGetter(t -> t.requiredKillCount)
    ).apply(instance, KillEntityTask::new));
    private final EntityFilter filter;
    private final int requiredKillCount;

    public KillEntityTask(EntityFilter filter, int requiredKillCount) {
        this.filter = filter;
        this.requiredKillCount = requiredKillCount;
    }

    @Override
    public TaskType<KillEntityTaskInstance, ?> getType() {
        return QuestingRegistries.KILL_ENTITY_TASK;
    }

    @Override
    public KillEntityTaskInstance createTaskInstance() {
        return new KillEntityTaskInstance(this);
    }

}

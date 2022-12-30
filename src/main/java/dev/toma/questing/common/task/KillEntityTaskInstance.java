package dev.toma.questing.common.task;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public final class KillEntityTaskInstance implements TaskInstance {

    public static final Codec<KillEntityTaskInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            KillEntityTask.CODEC.fieldOf("task").forGetter(taskInstance -> taskInstance.task),
            Codec.INT.fieldOf("kills").forGetter(taskInstance -> taskInstance.killCount)
    ).apply(instance, KillEntityTaskInstance::new));
    private final KillEntityTask task;
    private int killCount;

    public KillEntityTaskInstance(KillEntityTask task) {
        this(task, 0);
    }

    public KillEntityTaskInstance(KillEntityTask task, int killCount) {
        this.task = task;
        this.killCount = killCount;
    }

    @Override
    public KillEntityTask getTask() {
        return task;
    }
}

package dev.toma.questing.common.component.task.instance;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.toma.questing.common.component.task.provider.KillEntityTaskProvider;

public final class KillEntityTask implements Task {

    public static final Codec<KillEntityTask> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            KillEntityTaskProvider.CODEC.fieldOf("provider").forGetter(task -> task.provider),
            Codec.INT.fieldOf("kills").forGetter(taskInstance -> taskInstance.killCount)
    ).apply(instance, KillEntityTask::new));
    private final KillEntityTaskProvider provider;
    private int killCount;

    public KillEntityTask(KillEntityTaskProvider provider) {
        this(provider, 0);
    }

    public KillEntityTask(KillEntityTaskProvider provider, int killCount) {
        this.provider = provider;
        this.killCount = killCount;
    }

    @Override
    public KillEntityTaskProvider getProvider() {
        return provider;
    }

    public int getKillCount() {
        return killCount;
    }
}

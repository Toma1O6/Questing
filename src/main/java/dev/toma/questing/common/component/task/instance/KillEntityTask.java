package dev.toma.questing.common.component.task.instance;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.toma.questing.common.component.condition.ConditionType;
import dev.toma.questing.common.component.condition.instance.Condition;
import dev.toma.questing.common.component.task.provider.KillEntityTaskProvider;
import dev.toma.questing.common.component.task.util.EntityFilter;
import dev.toma.questing.common.component.trigger.ResponseType;
import dev.toma.questing.common.component.trigger.Triggers;
import dev.toma.questing.common.quest.ProgressStatus;
import dev.toma.questing.common.quest.TaskRegisterHandler;
import dev.toma.questing.common.quest.instance.Quest;
import dev.toma.questing.utils.Codecs;
import dev.toma.questing.utils.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;

import java.util.List;

public final class KillEntityTask extends AbstractTask {

    public static final Codec<KillEntityTask> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            KillEntityTaskProvider.CODEC.fieldOf("provider").forGetter(task -> task.provider),
            ConditionType.CONDITION_CODEC.listOf().fieldOf("conditions").forGetter(Task::getTaskConditions),
            Codecs.enumCodec(ProgressStatus.class).fieldOf("status").forGetter(Task::getStatus),
            Codec.INT.fieldOf("kills").forGetter(taskInstance -> taskInstance.killCount)
    ).apply(instance, KillEntityTask::new));
    private final KillEntityTaskProvider provider;
    private int killCount;

    public KillEntityTask(KillEntityTaskProvider provider, Quest quest) {
        this(provider, Utils.getConditions(provider.getConditions(), quest), ProgressStatus.ACTIVE, 0);
    }

    public KillEntityTask(KillEntityTaskProvider provider, List<Condition> conditions, ProgressStatus status, int killCount) {
        super(status, conditions);
        this.provider = provider;
        this.killCount = killCount;
    }

    @Override
    public void registerTriggerHandlers(TaskRegisterHandler registerHandler) {
        registerHandler.registerTask(Triggers.ENTITY_DIED, (data, level, quest) -> {
            DamageSource source = data.getSource();
            Entity origin = source.getEntity();
            if (Utils.checkIfEntityIsPartyMember(origin, quest.getParty())) {
                Entity victim = data.getEntity();
                EntityFilter filter = this.provider.getFilter();
                return filter.acceptEntity(victim) ? ResponseType.OK : this.provider.getDefaultResponse();
            }
            return ResponseType.SKIP;
        }, (data, level, quest) -> {
            ++this.killCount;
            if (this.killCount >= this.provider.getRequiredKillCount()) {
                this.setStatus(ProgressStatus.COMPLETED);
            }
        });
    }

    @Override
    public KillEntityTaskProvider getProvider() {
        return provider;
    }

    public int getKillCount() {
        return killCount;
    }
}

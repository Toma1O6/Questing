package dev.toma.questing.common.quest.instance;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.toma.questing.common.component.condition.ConditionType;
import dev.toma.questing.common.component.condition.instance.Condition;
import dev.toma.questing.common.component.distributor.RewardDistributor;
import dev.toma.questing.common.component.reward.RewardType;
import dev.toma.questing.common.component.reward.instance.Reward;
import dev.toma.questing.common.component.reward.provider.RewardProvider;
import dev.toma.questing.common.component.task.TaskType;
import dev.toma.questing.common.component.task.instance.Task;
import dev.toma.questing.common.component.trigger.ActionHandler;
import dev.toma.questing.common.component.trigger.ActionResponder;
import dev.toma.questing.common.component.trigger.Trigger;
import dev.toma.questing.common.engine.QuestEngine;
import dev.toma.questing.common.party.Party;
import dev.toma.questing.common.quest.ProgressStatus;
import dev.toma.questing.common.quest.QuestActionContainer;
import dev.toma.questing.common.quest.TaskRegisterHandler;
import dev.toma.questing.utils.Codecs;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public abstract class AbstractQuest implements Quest {

    protected final QuestActionContainer actionContainer;
    protected final QuestData data;
    protected QuestEngine engine;

    public AbstractQuest(QuestData questData) {
        this.actionContainer = new QuestActionContainer();
        this.data = questData;
        this.data.conditionList.forEach(condition -> condition.registerTriggerResponders(this.actionContainer::registerCondition));
        this.data.taskList.forEach(task -> task.registerTriggerHandlers(new TaskRegisterHandler() {
            @Override
            public <T> void registerTask(Trigger<T> trigger, ActionResponder<T> responder, ActionHandler<T> handler) {
                AbstractQuest.this.actionContainer.registerTask(task, trigger, responder, handler, c -> {});
            }
        }));
    }

    public AbstractQuest() {
        this.actionContainer = new QuestActionContainer();
        this.data = new QuestData();
        this.data.status = ProgressStatus.CREATED;
    }

    @Override
    public void onGenerated(Party party, World level, QuestEngine engine) {
        this.setStatus(ProgressStatus.GENERATED);
        this.onReloaded(engine);
        this.data.conditionList = this.getProvider().getConditions().stream()
                .map(provider -> provider.createCondition(this))
                .collect(Collectors.toList());
        this.data.taskList = this.getProvider().getTasks().stream()
                .map(provider -> provider.createTaskInstance(this))
                .collect(Collectors.toList());
        this.data.unclaimedRewards = new HashMap<>();
        RewardDistributor distributor = this.getProvider().getRewardDistributor();
        party.forEachOnlineMemberExcept(null, level, player -> {
            RewardProvider<?> rewardProvider = distributor.getRewardProviderFor(player, party, this);
            if (rewardProvider != null) {
                Reward reward = rewardProvider.createReward(player, this);
                this.data.unclaimedRewards.put(player.getUUID(), reward);
            }
        });
    }

    @Override
    public void onReloaded(QuestEngine engine) {
        this.engine = engine;
    }

    @Override
    public void onAssigned(Party party, World level) {
        this.setStatus(ProgressStatus.ACTIVE);
        this.data.party = party;
        for (Condition condition : this.data.conditionList) {
            condition.onConditionConstructing(party, this, level);
            condition.registerTriggerResponders(this.actionContainer::registerCondition);
        }
        this.registerTaskHandlers(level);
    }

    @Override
    public void complete(World level) {
        this.setStatus(ProgressStatus.COMPLETED);
        this.engine.storeRewards(this.getUnclaimedRewards());
        this.onCompleted(level);
    }

    @Override
    public void fail(World level) {
        this.setStatus(ProgressStatus.FAILED);
        this.getUnclaimedRewards().clear();
        this.onFailed(level);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T, E> void trigger(Trigger<T> trigger, T triggerData, World level) {
        if (this.getStatus() != ProgressStatus.ACTIVE || !this.shouldAcceptTrigger(trigger, triggerData, level)) {
            return;
        }
        QuestActionContainer.TriggerContext<T, E> context = QuestActionContainer.TriggerContext.create(this.actionContainer, trigger, triggerData);
        QuestActionContainer.HandledTriggerContext<T, E> responseCtx = context.handle(triggerData, level, this);
        switch (responseCtx.getResponseType()) {
            case SKIP:
            case PASS:
                return;
            case FAIL:
                this.setStatus(ProgressStatus.FAILED);
                return;
            case OK:
                break;
        }
        List<Task> incompleteTasks = this.getActiveTasks();
        responseCtx.handleSuccess(triggerData, level, this);
        for (Task task : incompleteTasks) {
            ProgressStatus status = task.getStatus();
            if (status.isFinalStatus()) {
                this.onTaskFinished(task);
                if (status == ProgressStatus.FAILED && !task.getProvider().isOptional()) {
                    this.fail(level);
                    return;
                }
            }
        }
        if (this.isCompletable()) {
            this.setStatus(ProgressStatus.COMPLETED);
            for (Task task : this.data.taskList) {
                ProgressStatus taskStatus = task.getStatus();
                if (taskStatus == ProgressStatus.FAILED) {
                    if (task.getProvider().isOptional()) {
                        continue;
                    }
                    this.setStatus(ProgressStatus.FAILED);
                } else if (taskStatus != ProgressStatus.COMPLETED && !task.getProvider().isOptional()) {
                    throw new UnsupportedOperationException("Task is in incorrect state: " + taskStatus);
                }
            }
        }
        if (this.data.status == ProgressStatus.COMPLETED) {
            this.complete(level);
        } else {
            this.fail(level);
        }
    }

    protected void onTaskFinished(Task task) {
    }

    protected void onFailed(World level) {

    }

    protected void onCompleted(World level) {

    }

    protected void onTick(World level) {
    }

    protected List<Task> getActiveTasks() {
        return this.data.taskList.stream()
                .filter(task -> !task.getStatus().isFinalStatus())
                .collect(Collectors.toList());
    }

    protected boolean isCompletable() {
        return this.data.taskList.stream()
                .noneMatch(task -> !task.getProvider().isOptional() && !task.getStatus().isFinalStatus());
    }

    protected <T> boolean shouldAcceptTrigger(Trigger<T> trigger, T triggerData, World level) {
        return true;
    }

    protected void registerTaskHandlers(World level) {
        for (Task task : this.data.taskList) {
            task.registerTriggerHandlers(new TaskRegisterHandler() {
                @Override
                public <T> void registerTask(Trigger<T> trigger, ActionResponder<T> responder, ActionHandler<T> handler) {
                    AbstractQuest.this.actionContainer.registerTask(task, trigger, responder, handler, condition -> condition.onConditionConstructing(AbstractQuest.this.getParty(), AbstractQuest.this, level));
                }
            });
        }
    }

    @Override
    public ProgressStatus getStatus() {
        return this.data.status;
    }

    @Override
    public void setStatus(ProgressStatus status) {
        this.data.status = status;
    }

    @Override
    public Party getParty() {
        return this.data.party;
    }

    public List<Condition> getConditions() {
        return this.data.conditionList;
    }

    public List<Task> getTasks() {
        return this.data.taskList;
    }

    public Map<UUID, Reward> getUnclaimedRewards() {
        return this.data.unclaimedRewards;
    }

    public static final class QuestData {

        public static final Codec<QuestData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codecs.enumCodec(ProgressStatus.class).fieldOf("progress").forGetter(QuestData::getStatus),
                ConditionType.CONDITION_CODEC.listOf().fieldOf("conditions").forGetter(QuestData::getConditionList),
                TaskType.INSTANCE_CODEC.listOf().fieldOf("tasks").forGetter(QuestData::getTaskList),
                Codec.unboundedMap(Codecs.UUID_STRING, RewardType.REWARD_CODEC).fieldOf("rewards").forGetter(QuestData::getUnclaimedRewards),
                Party.CODEC.fieldOf("party").forGetter(QuestData::getParty)
        ).apply(instance, QuestData::new));
        private ProgressStatus status;
        private List<Condition> conditionList;
        private List<Task> taskList;
        private Map<UUID, Reward> unclaimedRewards;
        private Party party;

        public QuestData() {
        }

        public QuestData(ProgressStatus status, List<Condition> conditionList, List<Task> taskList, Map<UUID, Reward> unclaimedRewards, Party party) {
            this.status = status;
            this.conditionList = conditionList;
            this.taskList = taskList;
            this.unclaimedRewards = unclaimedRewards;
            this.party = party;
        }

        public ProgressStatus getStatus() {
            return status;
        }

        public List<Condition> getConditionList() {
            return conditionList;
        }

        public List<Task> getTaskList() {
            return taskList;
        }

        public Map<UUID, Reward> getUnclaimedRewards() {
            return unclaimedRewards;
        }

        public Party getParty() {
            return party;
        }
    }
}

package dev.toma.questing.common.engine;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.toma.questing.Questing;
import dev.toma.questing.common.component.distributor.NoRewardDistributor;
import dev.toma.questing.common.component.reward.RewardType;
import dev.toma.questing.common.component.reward.instance.Reward;
import dev.toma.questing.common.component.task.provider.KillEntityTaskProvider;
import dev.toma.questing.common.component.task.provider.TaskProvider;
import dev.toma.questing.common.component.task.util.AnyEntityFilter;
import dev.toma.questing.common.component.trigger.ResponseType;
import dev.toma.questing.common.loader.QuestLoader;
import dev.toma.questing.common.party.Party;
import dev.toma.questing.common.quest.QuestType;
import dev.toma.questing.common.quest.instance.Quest;
import dev.toma.questing.common.quest.instance.SimpleQuest;
import dev.toma.questing.common.quest.provider.SimpleQuestProvider;
import dev.toma.questing.utils.Codecs;
import dev.toma.questing.utils.Utils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.*;

public class SimpleQuestEngine implements QuestEngine {

    public static final Codec<SimpleQuestEngine> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(Codecs.UUID_STRING, QuestType.INSTANCE_CODEC.listOf()).fieldOf("active").forGetter(e -> e.active),
            Codec.unboundedMap(Codecs.UUID_STRING, RewardType.REWARD_CODEC.listOf()).fieldOf("rewards").forGetter(e -> e.rewards)
    ).apply(instance, SimpleQuestEngine::new));
    private static final QuestLoader LOADER = new QuestLoader();
    public static final ResourceLocation IDENTIFIER = new ResourceLocation(Questing.MODID, "config_quests");
    private final Map<UUID, List<Quest>> active = new HashMap<>();
    private final Map<UUID, List<Reward>> rewards = new HashMap<>();

    public SimpleQuestEngine() {
    }

    private SimpleQuestEngine(Map<UUID, List<Quest>> active, Map<UUID, List<Reward>> rewards) {
        this.active.putAll(active);
        this.rewards.putAll(rewards);
    }

    @Override
    public boolean canStartQuest(Party party, Quest quest, World level) {
        UUID partyId = party.getOwner();
        return this.active.getOrDefault(partyId, Collections.emptyList()).isEmpty();
    }

    @Override
    public boolean shouldShowInQuestUI() {
        return true;
    }

    @Override
    public boolean canClaimRewardsViaUI() {
        return true;
    }

    @Override
    public List<Quest> generateQuests(Party party, World level) {
        if (Utils.isNullOrEmpty(this.active.get(party.getOwner()))) {
            TaskProvider<?> taskProvider = new KillEntityTaskProvider(ResponseType.PASS, Collections.emptyList(), false, AnyEntityFilter.ANY_ENTITY, 5);
            SimpleQuestProvider provider = new SimpleQuestProvider(IDENTIFIER, Collections.emptyList(), Collections.singletonList(taskProvider), NoRewardDistributor.NONE);
            Quest quest = new SimpleQuest(provider);
            quest.onGenerated(party, level, this);
            return Collections.singletonList(quest);
        }
        return Collections.emptyList();
    }

    @Override
    public ResourceLocation getIndentifier() {
        return IDENTIFIER;
    }

    @Override
    public Map<UUID, List<Quest>> getQuestsByParty() {
        return this.active;
    }

    @Override
    public Map<UUID, List<Reward>> getClaimableRewards() {
        return this.rewards;
    }

    @Override
    public void storeRewards(Map<UUID, Reward> map) {
        map.forEach(this::appendReward);
    }

    @Override
    public void onLoaded() {
        LOADER.loadQuests();
    }

    private void appendReward(UUID uuid, Reward reward) {
        List<Reward> list = this.rewards.computeIfAbsent(uuid, key -> new ArrayList<>(1));
        list.add(reward);
    }
}

package dev.toma.questing.common.reward;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.toma.questing.common.init.QuestingRegistries;
import dev.toma.questing.common.party.Party;
import dev.toma.questing.common.quest.Quest;
import dev.toma.questing.utils.Utils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SharedRewardDistributor implements RewardDistributor {

    public static final Codec<SharedRewardDistributor> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RewardType.CODEC.listOf().optionalFieldOf("rewards", Collections.emptyList()).forGetter(dist -> dist.rewards)
    ).apply(instance, SharedRewardDistributor::new));

    private final List<Reward> rewards;

    public SharedRewardDistributor(List<Reward> rewards) {
        this.rewards = rewards;
    }

    @Override
    public Map<PlayerEntity, List<Reward>> generateDistributedRewards(World world, Quest quest) {
        Party party = quest.getParty();
        Map<PlayerEntity, List<Reward>> rewardMap = new HashMap<>();
        party.forEachOnlineMemberExcept(null, world, player -> {
            List<Reward> rewardList = this.rewards.stream()
                    .map(ireward -> Utils.getAwardableReward(ireward, player, quest))
                    .collect(Collectors.toList());
            rewardMap.put(player, rewardList);
        });
        return rewardMap;
    }

    @Override
    public RewardDistributionType<?> getType() {
        return QuestingRegistries.SHARED_REWARD_DISTRIBUTOR;
    }
}

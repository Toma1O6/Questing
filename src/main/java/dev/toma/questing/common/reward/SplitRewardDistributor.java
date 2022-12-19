package dev.toma.questing.common.reward;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.toma.questing.common.init.QuestingRegistries;
import dev.toma.questing.common.party.QuestParty;
import dev.toma.questing.common.quest.Quest;
import dev.toma.questing.utils.Utils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

import java.util.*;
import java.util.stream.Collectors;

public class SplitRewardDistributor implements RewardDistributor {

    public static final Codec<SplitRewardDistributor> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RewardType.CODEC.listOf().optionalFieldOf("ownerRewards", Collections.emptyList()).forGetter(type -> type.ownerRewards),
            RewardType.CODEC.listOf().optionalFieldOf("otherRewards", Collections.emptyList()).forGetter(type -> type.otherRewards)
    ).apply(instance, SplitRewardDistributor::new));
    private final List<Reward> ownerRewards;
    private final List<Reward> otherRewards;

    public SplitRewardDistributor(List<Reward> ownerRewards, List<Reward> otherRewards) {
        this.ownerRewards = ownerRewards;
        this.otherRewards = otherRewards;
    }

    @Override
    public Map<PlayerEntity, List<Reward>> generateDistributedRewards(World world, Quest quest) {
        QuestParty party = quest.getParty();
        UUID ownerId = party.getOwner();
        Map<PlayerEntity, List<Reward>> rewards = new HashMap<>();
        party.getOwner(world)
                .ifPresent(owner -> generateRewards(owner, quest, rewards, ownerRewards));
        party.forEachOnlineMemberExcept(ownerId, world, player -> generateRewards(player, quest, rewards, otherRewards));
        return rewards;
    }

    @Override
    public RewardDistributionType<?> getType() {
        return QuestingRegistries.SPLIT_REWARD_DISTRIBUTOR;
    }

    private void generateRewards(PlayerEntity player, Quest quest, Map<PlayerEntity, List<Reward>> holder, List<Reward> rewards) {
        List<Reward> rewardList = rewards.stream()
                .map(ireward -> Utils.getAwardableReward(ireward, player, quest))
                .collect(Collectors.toList());
        holder.put(player, rewardList);
    }
}

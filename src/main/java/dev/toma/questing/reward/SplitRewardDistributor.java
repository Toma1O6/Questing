package dev.toma.questing.reward;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.toma.questing.init.QuestingRegistries;
import dev.toma.questing.party.QuestParty;
import dev.toma.questing.quest.Quest;
import dev.toma.questing.utils.JsonHelper;
import dev.toma.questing.utils.Utils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.JSONUtils;
import net.minecraft.world.World;

import java.util.*;
import java.util.stream.Collectors;

public class SplitRewardDistributor implements RewardDistributor {

    private final List<Reward> ownerRewards;
    private final List<Reward> otherRewards;

    public SplitRewardDistributor(Reward[] ownerRewards, Reward[] otherRewards) {
        this.ownerRewards = Arrays.asList(ownerRewards);
        this.otherRewards = Arrays.asList(otherRewards);
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

    public static final class Serializer implements RewardDistributionType.Serializer<SplitRewardDistributor> {

        @Override
        public SplitRewardDistributor distributorFromJson(JsonObject data) {
            JsonArray owner = JSONUtils.getAsJsonArray(data, "ownerRewards", new JsonArray());
            JsonArray other = JSONUtils.getAsJsonArray(data, "otherRewards", new JsonArray());
            Reward[] ownerRewards = JsonHelper.mapArray(owner, Reward[]::new, RewardType::fromJson);
            Reward[] otherRewards = JsonHelper.mapArray(other, Reward[]::new, RewardType::fromJson);
            return new SplitRewardDistributor(ownerRewards, otherRewards);
        }
    }
}

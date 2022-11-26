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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SharedRewardDistributor implements IRewardDistributor {

    private final List<IReward> rewards;

    public SharedRewardDistributor(IReward[] rewards) {
        this.rewards = Arrays.asList(rewards);
    }

    @Override
    public Map<PlayerEntity, List<IReward>> generateDistributedRewards(World world, Quest quest) {
        QuestParty party = quest.getParty();
        Map<PlayerEntity, List<IReward>> rewardMap = new HashMap<>();
        party.forEachOnlineMemberExcept(null, world, player -> {
            List<IReward> rewardList = this.rewards.stream()
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

    private void giveRewards(PlayerEntity player, Quest quest) {
        for (IReward reward : rewards) {
            reward.awardPlayer(player, quest);
        }
    }

    public static final class Serializer implements RewardDistributionType.Serializer<SharedRewardDistributor> {

        @Override
        public SharedRewardDistributor distributorFromJson(JsonObject data) {
            return new SharedRewardDistributor(JsonHelper.mapArray(JSONUtils.getAsJsonArray(data, "rewards", new JsonArray()), IReward[]::new, RewardType::fromJson));
        }
    }
}

package dev.toma.questing.reward;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.toma.questing.init.QuestingRegistries;
import dev.toma.questing.party.QuestParty;
import dev.toma.questing.quest.Quest;
import dev.toma.questing.utils.JsonHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.JSONUtils;
import net.minecraft.world.World;

public class SharedRewardDistributor implements IRewardDistributor {

    private final IReward[] rewards;

    public SharedRewardDistributor(IReward[] rewards) {
        this.rewards = rewards;
    }

    @Override
    public void distribute(World world, Quest quest) {
        QuestParty party = quest.getParty();
        party.forEachOnlineMemberExcept(null, world, player -> this.giveRewards(player, quest));
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

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

import java.util.UUID;

public class SplitRewardDistributor implements IRewardDistributor {

    private final IReward[] ownerRewards;
    private final IReward[] otherRewards;

    public SplitRewardDistributor(IReward[] ownerRewards, IReward[] otherRewards) {
        this.ownerRewards = ownerRewards;
        this.otherRewards = otherRewards;
    }

    @Override
    public void distribute(World world, Quest quest) {
        QuestParty party = quest.getParty();
        UUID ownerId = party.getOwner();
        party.getOwner(world).ifPresent(owner -> this.giveRewards(owner, quest, ownerRewards));
        party.forEachOnlineMemberExcept(ownerId, world, player -> this.giveRewards(player, quest, otherRewards));
    }

    @Override
    public RewardDistributionType<?> getType() {
        return QuestingRegistries.SPLIT_REWARD_DISTRIBUTOR;
    }

    private void giveRewards(PlayerEntity player, Quest quest, IReward[] rewards) {
        for (IReward reward : rewards) {
            reward.awardPlayer(player, quest);
        }
    }

    public static final class Serializer implements RewardDistributionType.Serializer<SplitRewardDistributor> {

        @Override
        public SplitRewardDistributor distributorFromJson(JsonObject data) {
            JsonArray owner = JSONUtils.getAsJsonArray(data, "ownerRewards", new JsonArray());
            JsonArray other = JSONUtils.getAsJsonArray(data, "otherRewards", new JsonArray());
            IReward[] ownerRewards = JsonHelper.mapArray(owner, IReward[]::new, RewardType::fromJson);
            IReward[] otherRewards = JsonHelper.mapArray(other, IReward[]::new, RewardType::fromJson);
            return new SplitRewardDistributor(ownerRewards, otherRewards);
        }
    }
}

package dev.toma.questing.common.reward.distributor;

import com.mojang.serialization.Codec;
import dev.toma.questing.common.init.QuestingRegistries;
import dev.toma.questing.common.party.Party;
import dev.toma.questing.common.reward.Reward;
import dev.toma.questing.common.reward.RewardType;
import net.minecraft.entity.player.PlayerEntity;

import javax.annotation.Nullable;

public class SharedRewardDistributor implements RewardDistributor {

    public static final Codec<SharedRewardDistributor> CODEC = RewardType.CODEC
            .xmap(SharedRewardDistributor::new, dist -> dist.reward)
            .fieldOf("reward").codec();

    private final Reward reward;

    public SharedRewardDistributor(Reward reward) {
        this.reward = reward;
    }

    @Nullable
    @Override
    public Reward createDistributedReward(PlayerEntity player, Party party) {
        return reward.copy();
    }

    @Override
    public RewardDistributionType<?> getType() {
        return QuestingRegistries.SHARED_REWARD_DISTRIBUTOR;
    }
}

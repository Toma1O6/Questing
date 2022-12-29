package dev.toma.questing.common.reward.distributor;

import com.mojang.serialization.Codec;
import dev.toma.questing.common.init.QuestingRegistries;
import dev.toma.questing.common.party.Party;
import dev.toma.questing.common.reward.Reward;
import net.minecraft.entity.player.PlayerEntity;

import javax.annotation.Nullable;

public class NoRewardDistributor implements RewardDistributor {

    public static final NoRewardDistributor NONE = new NoRewardDistributor();
    public static final Codec<NoRewardDistributor> CODEC = Codec.unit(NONE);

    private NoRewardDistributor() {}

    @Nullable
    @Override
    public Reward createDistributedReward(PlayerEntity player, Party party) {
        return null;
    }

    @Override
    public RewardDistributionType<?> getType() {
        return QuestingRegistries.NO_REWARD_DISTRIBUTOR;
    }
}

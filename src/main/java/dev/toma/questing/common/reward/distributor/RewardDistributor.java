package dev.toma.questing.common.reward.distributor;

import dev.toma.questing.common.party.Party;
import dev.toma.questing.common.reward.Reward;
import net.minecraft.entity.player.PlayerEntity;

import javax.annotation.Nullable;

public interface RewardDistributor {

    @Nullable
    Reward createDistributedReward(PlayerEntity player, Party party);

    RewardDistributionType<?> getType();
}

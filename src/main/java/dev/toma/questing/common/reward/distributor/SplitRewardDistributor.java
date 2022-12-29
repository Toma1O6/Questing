package dev.toma.questing.common.reward.distributor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.toma.questing.common.init.QuestingRegistries;
import dev.toma.questing.common.party.Party;
import dev.toma.questing.common.reward.Reward;
import dev.toma.questing.common.reward.RewardType;
import net.minecraft.entity.player.PlayerEntity;

import javax.annotation.Nullable;

public class SplitRewardDistributor implements RewardDistributor {

    public static final Codec<SplitRewardDistributor> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RewardType.CODEC.optionalFieldOf("ownerReward", null).forGetter(type -> type.ownerReward),
            RewardType.CODEC.optionalFieldOf("partyReward", null).forGetter(type -> type.otherReward)
    ).apply(instance, SplitRewardDistributor::new));

    @Nullable
    private final Reward ownerReward;
    @Nullable
    private final Reward otherReward;

    public SplitRewardDistributor(Reward ownerReward, Reward otherReward) {
        this.ownerReward = ownerReward;
        this.otherReward = otherReward;
    }

    @Nullable
    @Override
    public Reward createDistributedReward(PlayerEntity player, Party party) {
        Reward reward = party.getOwner().equals(player.getUUID()) ? this.ownerReward : this.otherReward;
        return reward != null ? reward.copy() : null;
    }

    @Override
    public RewardDistributionType<?> getType() {
        return QuestingRegistries.SPLIT_REWARD_DISTRIBUTOR;
    }
}

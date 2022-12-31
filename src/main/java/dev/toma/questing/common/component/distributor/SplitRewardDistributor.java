package dev.toma.questing.common.component.distributor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.toma.questing.common.init.QuestingRegistries;
import dev.toma.questing.common.party.Party;
import dev.toma.questing.common.component.reward.provider.RewardProvider;
import dev.toma.questing.common.component.reward.RewardType;
import dev.toma.questing.common.quest.instance.Quest;
import net.minecraft.entity.player.PlayerEntity;

import javax.annotation.Nullable;

public class SplitRewardDistributor implements RewardDistributor {

    public static final Codec<SplitRewardDistributor> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RewardType.PROVIDER_CODEC.optionalFieldOf("ownerReward", null).forGetter(type -> type.ownerReward),
            RewardType.PROVIDER_CODEC.optionalFieldOf("partyReward", null).forGetter(type -> type.otherReward)
    ).apply(instance, SplitRewardDistributor::new));

    @Nullable
    private final RewardProvider<?> ownerReward;
    @Nullable
    private final RewardProvider<?> otherReward;

    public SplitRewardDistributor(RewardProvider<?> ownerReward, RewardProvider<?> otherReward) {
        this.ownerReward = ownerReward;
        this.otherReward = otherReward;
    }

    @Nullable
    @Override
    public RewardProvider<?> getRewardProviderFor(PlayerEntity player, Party party, Quest quest) {
        return party.getOwner().equals(player.getUUID()) ? this.ownerReward : this.otherReward;
    }

    @Override
    public RewardDistributionType<?> getType() {
        return QuestingRegistries.SPLIT_REWARD_DISTRIBUTOR;
    }
}

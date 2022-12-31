package dev.toma.questing.common.component.distributor;

import com.mojang.serialization.Codec;
import dev.toma.questing.common.component.reward.RewardType;
import dev.toma.questing.common.component.reward.provider.RewardProvider;
import dev.toma.questing.common.init.QuestingRegistries;
import dev.toma.questing.common.party.Party;
import net.minecraft.entity.player.PlayerEntity;

import javax.annotation.Nullable;

public class SharedRewardDistributor implements RewardDistributor {

    public static final Codec<SharedRewardDistributor> CODEC = RewardType.PROVIDER_CODEC
            .xmap(SharedRewardDistributor::new, dist -> dist.reward)
            .fieldOf("reward").codec();

    private final RewardProvider<?> reward;

    public SharedRewardDistributor(RewardProvider<?> reward) {
        this.reward = reward;
    }

    @Nullable
    @Override
    public RewardProvider<?> getRewardProviderFor(PlayerEntity player, Party party) {
        return reward;
    }

    @Override
    public RewardDistributionType<?> getType() {
        return QuestingRegistries.SHARED_REWARD_DISTRIBUTOR;
    }
}

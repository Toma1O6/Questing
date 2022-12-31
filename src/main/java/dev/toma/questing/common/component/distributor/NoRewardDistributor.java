package dev.toma.questing.common.component.distributor;

import com.mojang.serialization.Codec;
import dev.toma.questing.common.component.reward.provider.RewardProvider;
import dev.toma.questing.common.init.QuestingRegistries;
import dev.toma.questing.common.party.Party;
import dev.toma.questing.common.quest.instance.Quest;
import net.minecraft.entity.player.PlayerEntity;

public class NoRewardDistributor implements RewardDistributor {

    public static final NoRewardDistributor NONE = new NoRewardDistributor();
    public static final Codec<NoRewardDistributor> CODEC = Codec.unit(NONE);

    private NoRewardDistributor() {}

    @Override
    public RewardProvider<?> getRewardProviderFor(PlayerEntity player, Party party, Quest quest) {
        return null;
    }

    @Override
    public RewardDistributionType<?> getType() {
        return QuestingRegistries.NO_REWARD_DISTRIBUTOR;
    }
}

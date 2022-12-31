package dev.toma.questing.common.component.distributor;

import dev.toma.questing.common.component.reward.provider.RewardProvider;
import dev.toma.questing.common.party.Party;
import dev.toma.questing.common.quest.instance.Quest;
import net.minecraft.entity.player.PlayerEntity;

public interface RewardDistributor {

    RewardProvider<?> getRewardProviderFor(PlayerEntity player, Party party, Quest quest);

    RewardDistributionType<?> getType();
}

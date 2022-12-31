package dev.toma.questing.common.component.reward.instance;

import dev.toma.questing.common.component.reward.provider.RewardProvider;
import dev.toma.questing.common.quest.Quest;
import net.minecraft.entity.player.PlayerEntity;

public interface Reward {

    RewardProvider<?> getProvider();

    void award(PlayerEntity player, Quest quest);
}

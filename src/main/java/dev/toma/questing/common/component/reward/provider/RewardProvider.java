package dev.toma.questing.common.component.reward.provider;

import dev.toma.questing.common.component.reward.RewardType;
import dev.toma.questing.common.component.reward.instance.Reward;
import dev.toma.questing.common.quest.Quest;
import net.minecraft.entity.player.PlayerEntity;

public interface RewardProvider<R extends Reward> {

    RewardType<R, ?> getType();

    R createReward(PlayerEntity player, Quest quest);
}

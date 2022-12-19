package dev.toma.questing.common.reward;

import dev.toma.questing.common.quest.Quest;
import net.minecraft.entity.player.PlayerEntity;

public interface NestedReward extends Reward {

    Reward getActualReward(PlayerEntity player, Quest quest);
}

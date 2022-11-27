package dev.toma.questing.reward;

import dev.toma.questing.quest.Quest;
import net.minecraft.entity.player.PlayerEntity;

public interface NestedReward extends Reward {

    Reward getActualReward(PlayerEntity player, Quest quest);
}

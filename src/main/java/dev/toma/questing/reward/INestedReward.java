package dev.toma.questing.reward;

import dev.toma.questing.quest.Quest;
import net.minecraft.entity.player.PlayerEntity;

public interface INestedReward extends IReward {

    IReward getActualReward(PlayerEntity player, Quest quest);
}

package dev.toma.questing.common.reward;

import dev.toma.questing.common.quest.Quest;
import net.minecraft.entity.player.PlayerEntity;

public interface Reward {

    RewardType<?> getType();

    void awardPlayer(PlayerEntity player, Quest quest);
}

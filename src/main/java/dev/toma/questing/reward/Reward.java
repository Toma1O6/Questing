package dev.toma.questing.reward;

import dev.toma.questing.quest.Quest;
import net.minecraft.entity.player.PlayerEntity;

public interface Reward {

    RewardType<?> getType();

    void awardPlayer(PlayerEntity player, Quest quest);
}

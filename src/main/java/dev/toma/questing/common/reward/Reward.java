package dev.toma.questing.common.reward;

import dev.toma.questing.common.quest.Quest;
import net.minecraft.entity.player.PlayerEntity;

public interface Reward {

    RewardType<?> getType();

    Reward copy();

    void generate(PlayerEntity player, Quest quest);

    void awardPlayer(PlayerEntity player, Quest quest);
}

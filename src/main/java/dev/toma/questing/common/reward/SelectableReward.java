package dev.toma.questing.common.reward;

import dev.toma.questing.common.quest.Quest;
import net.minecraft.entity.player.PlayerEntity;

public interface SelectableReward extends RewardProvider {

    void select(int index, PlayerEntity player, Quest quest);

    void deselect(int index, PlayerEntity player, Quest quest);

    boolean isSelected(int index);
}

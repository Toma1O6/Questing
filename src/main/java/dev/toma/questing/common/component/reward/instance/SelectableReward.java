package dev.toma.questing.common.component.reward.instance;

import dev.toma.questing.common.quest.instance.Quest;
import net.minecraft.entity.player.PlayerEntity;

public interface SelectableReward extends RewardHolder {

    void select(int index, PlayerEntity player, Quest quest);

    void deselect(int index, PlayerEntity player, Quest quest);

    boolean isSelected(int index);
}

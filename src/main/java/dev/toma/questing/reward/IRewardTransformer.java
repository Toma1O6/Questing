package dev.toma.questing.reward;

import dev.toma.questing.quest.Quest;
import net.minecraft.entity.player.PlayerEntity;

public interface IRewardTransformer<I> {

    I adjust(I originalValue, PlayerEntity player, Quest quest);
}

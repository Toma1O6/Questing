package dev.toma.questing.reward;

import dev.toma.questing.quest.Quest;
import net.minecraft.entity.player.PlayerEntity;

public interface RewardTransformer<I> {

    RewardTransformerType<?, ?> getType();

    I adjust(I originalValue, PlayerEntity player, Quest quest);
}

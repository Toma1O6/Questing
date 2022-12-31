package dev.toma.questing.common.component.reward.transformer;

import dev.toma.questing.common.quest.instance.Quest;
import net.minecraft.entity.player.PlayerEntity;

public interface RewardTransformer<I> {

    RewardTransformerType<?, ?> getType();

    I adjust(I originalValue, PlayerEntity player, Quest quest);
}

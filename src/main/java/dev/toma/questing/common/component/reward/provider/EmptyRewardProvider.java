package dev.toma.questing.common.component.reward.provider;

import com.mojang.serialization.Codec;
import dev.toma.questing.common.component.reward.RewardType;
import dev.toma.questing.common.component.reward.instance.EmptyReward;
import dev.toma.questing.common.init.QuestingRegistries;
import dev.toma.questing.common.quest.instance.Quest;
import net.minecraft.entity.player.PlayerEntity;

public class EmptyRewardProvider implements RewardProvider<EmptyReward> {

    public static final EmptyRewardProvider EMPTY = new EmptyRewardProvider();
    public static final Codec<EmptyRewardProvider> CODEC = Codec.unit(EMPTY);

    private EmptyRewardProvider() {}

    @Override
    public RewardType<EmptyReward, ?> getType() {
        return QuestingRegistries.EMPTY_REWARD;
    }

    @Override
    public EmptyReward createReward(PlayerEntity player, Quest quest) {
        return EmptyReward.EMPTY;
    }
}

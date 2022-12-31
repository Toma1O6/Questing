package dev.toma.questing.common.component.reward.instance;

import com.mojang.serialization.Codec;
import dev.toma.questing.common.component.reward.provider.EmptyRewardProvider;
import dev.toma.questing.common.component.reward.provider.RewardProvider;
import dev.toma.questing.common.quest.instance.Quest;
import net.minecraft.entity.player.PlayerEntity;

public class EmptyReward implements Reward {

    public static final EmptyReward EMPTY = new EmptyReward();
    public static final Codec<EmptyReward> CODEC = Codec.unit(EMPTY);

    @Override
    public RewardProvider<?> getProvider() {
        return EmptyRewardProvider.EMPTY;
    }

    @Override
    public void award(PlayerEntity player, Quest quest) {
    }
}

package dev.toma.questing.common.component.reward.provider;

import com.mojang.serialization.Codec;
import dev.toma.questing.common.component.reward.RewardType;
import dev.toma.questing.common.component.reward.instance.MultiReward;
import dev.toma.questing.common.init.QuestingRegistries;
import dev.toma.questing.common.quest.Quest;
import net.minecraft.entity.player.PlayerEntity;

import java.util.List;

public class MultiRewardProvider implements RewardProvider<MultiReward> {

    public static final Codec<MultiRewardProvider> CODEC = RewardType.PROVIDER_CODEC.listOf()
            .xmap(MultiRewardProvider::new, t -> t.rewardList)
            .fieldOf("rewards").codec();
    protected final List<RewardProvider<?>> rewardList;

    public MultiRewardProvider(List<RewardProvider<?>> rewardList) {
        this.rewardList = rewardList;
    }

    @Override
    public RewardType<MultiReward, ?> getType() {
        return QuestingRegistries.MULTI_REWARD;
    }

    @Override
    public MultiReward createReward(PlayerEntity player, Quest quest) {
        return new MultiReward(this, player, quest);
    }

    public List<RewardProvider<?>> getRewardList() {
        return rewardList;
    }
}

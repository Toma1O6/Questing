package dev.toma.questing.common.component.reward.instance;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.toma.questing.common.component.reward.RewardType;
import dev.toma.questing.common.component.reward.provider.RepeatedRewardProvider;
import dev.toma.questing.common.component.reward.provider.RewardProvider;
import dev.toma.questing.common.quest.Quest;
import net.minecraft.entity.player.PlayerEntity;

import java.util.ArrayList;
import java.util.List;

public class RepeatedReward implements Reward, RewardHolder {

    public static final Codec<RepeatedReward> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RepeatedRewardProvider.CODEC.fieldOf("provider").forGetter(t -> t.provider),
            RewardType.REWARD_CODEC.listOf().fieldOf("rewards").forGetter(t -> t.rewardList)
    ).apply(instance, RepeatedReward::new));
    protected final RepeatedRewardProvider provider;
    private final List<Reward> rewardList;

    public RepeatedReward(RepeatedRewardProvider provider, PlayerEntity player, Quest quest) {
        this.provider = provider;
        this.rewardList = this.generateRewardList(player, quest);
    }

    public RepeatedReward(RepeatedRewardProvider provider, List<Reward> rewardList) {
        this.provider = provider;
        this.rewardList = rewardList;
    }

    @Override
    public RepeatedRewardProvider getProvider() {
        return provider;
    }

    @Override
    public void award(PlayerEntity player, Quest quest) {
        this.rewardList.forEach(reward -> reward.award(player, quest));
    }

    @Override
    public List<Reward> getRewards() {
        return this.rewardList;
    }

    protected List<Reward> generateRewardList(PlayerEntity player, Quest quest) {
        RewardProvider<?> source = this.provider.getSource();
        int count = this.provider.getCount();
        List<Reward> list = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            Reward reward = source.createReward(player, quest);
            list.add(reward);
        }
        return list;
    }
}

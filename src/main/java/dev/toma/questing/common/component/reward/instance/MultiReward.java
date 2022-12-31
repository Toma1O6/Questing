package dev.toma.questing.common.component.reward.instance;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.toma.questing.common.component.reward.RewardType;
import dev.toma.questing.common.component.reward.provider.MultiRewardProvider;
import dev.toma.questing.common.component.reward.provider.RewardProvider;
import dev.toma.questing.common.quest.Quest;
import net.minecraft.entity.player.PlayerEntity;

import java.util.List;
import java.util.stream.Collectors;

public class MultiReward implements Reward, RewardHolder {

    public static final Codec<MultiReward> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            MultiRewardProvider.CODEC.fieldOf("provider").forGetter(t -> t.provider),
            RewardType.REWARD_CODEC.listOf().fieldOf("rewards").forGetter(t -> t.rewards)
    ).apply(instance, MultiReward::new));
    protected final MultiRewardProvider provider;
    private final List<Reward> rewards;

    public MultiReward(MultiRewardProvider provider, PlayerEntity player, Quest quest) {
        this.provider = provider;
        this.rewards = this.getRewards(player, quest);
    }

    public MultiReward(MultiRewardProvider provider, List<Reward> rewards) {
        this.provider = provider;
        this.rewards = rewards;
    }

    @Override
    public MultiRewardProvider getProvider() {
        return provider;
    }

    @Override
    public void award(PlayerEntity player, Quest quest) {
        this.rewards.forEach(reward -> reward.award(player, quest));
    }

    @Override
    public List<Reward> getRewards() {
        return rewards;
    }

    protected List<Reward> getRewards(PlayerEntity player, Quest quest) {
        List<RewardProvider<?>> providers = this.provider.getRewardList();
        return providers.stream().map(provider -> provider.createReward(player, quest)).collect(Collectors.toList());
    }
}

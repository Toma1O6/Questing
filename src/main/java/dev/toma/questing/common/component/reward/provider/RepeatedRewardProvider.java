package dev.toma.questing.common.component.reward.provider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.toma.questing.common.component.reward.RewardType;
import dev.toma.questing.common.component.reward.instance.RepeatedReward;
import dev.toma.questing.common.init.QuestingRegistries;
import dev.toma.questing.common.quest.instance.Quest;
import net.minecraft.entity.player.PlayerEntity;

public class RepeatedRewardProvider implements RewardProvider<RepeatedReward> {

    public static final Codec<RepeatedRewardProvider> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RewardType.PROVIDER_CODEC.fieldOf("reward").forGetter(t -> t.source),
            Codec.intRange(1, Integer.MAX_VALUE).fieldOf("count").forGetter(t -> t.count)
    ).apply(instance, RepeatedRewardProvider::new));
    private final RewardProvider<?> source;
    private final int count;

    public RepeatedRewardProvider(RewardProvider<?> source, int count) {
        this.source = source;
        this.count = count;
    }

    @Override
    public RewardType<RepeatedReward, ?> getType() {
        return QuestingRegistries.REPEATED_REWARD;
    }

    @Override
    public RepeatedReward createReward(PlayerEntity player, Quest quest) {
        return new RepeatedReward(this, player, quest);
    }

    public RewardProvider<?> getSource() {
        return source;
    }

    public int getCount() {
        return count;
    }
}

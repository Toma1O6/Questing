package dev.toma.questing.common.component.reward.provider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.toma.questing.common.component.reward.RewardType;
import dev.toma.questing.common.component.reward.instance.ChoiceReward;
import dev.toma.questing.common.init.QuestingRegistries;
import dev.toma.questing.common.quest.Quest;
import net.minecraft.entity.player.PlayerEntity;

import java.util.List;

public class ChoiceRewardProvider implements RewardProvider<ChoiceReward> {

    public static final Codec<ChoiceRewardProvider> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RewardType.PROVIDER_CODEC.listOf().fieldOf("choices").forGetter(t -> t.rewardList),
            Codec.intRange(1, Integer.MAX_VALUE).optionalFieldOf("choiceCount", 1).forGetter(ChoiceRewardProvider::getChoiceCount)
    ).apply(instance, ChoiceRewardProvider::new));

    private final List<RewardProvider<?>> rewardList;
    private final int choiceCount;

    public ChoiceRewardProvider(List<RewardProvider<?>> rewardList, int choiceCount) {
        this.rewardList = rewardList;
        this.choiceCount = choiceCount;
    }

    @Override
    public ChoiceReward createReward(PlayerEntity player, Quest quest) {
        return new ChoiceReward(this, player, quest);
    }

    @Override
    public RewardType<ChoiceReward, ?> getType() {
        return QuestingRegistries.CHOICE_REWARD;
    }

    public List<RewardProvider<?>> getRewardList() {
        return rewardList;
    }

    public int getChoiceCount() {
        return choiceCount;
    }
}

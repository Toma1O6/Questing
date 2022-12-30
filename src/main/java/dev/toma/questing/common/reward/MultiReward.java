package dev.toma.questing.common.reward;

import com.mojang.serialization.Codec;
import dev.toma.questing.common.init.QuestingRegistries;
import dev.toma.questing.common.quest.Quest;
import dev.toma.questing.utils.Utils;
import net.minecraft.entity.player.PlayerEntity;

import java.util.List;

public class MultiReward implements Reward {

    public static final Codec<MultiReward> CODEC = RewardType.CODEC.listOf()
            .xmap(MultiReward::new, t -> t.rewardList)
            .fieldOf("rewards").codec();
    protected final List<Reward> rewardList;

    public MultiReward(List<Reward> rewardList) {
        this.rewardList = rewardList;
    }

    @Override
    public RewardType<?> getType() {
        return QuestingRegistries.MULTI_REWARD;
    }

    @Override
    public Reward copy() {
        return new MultiReward(Utils.instantiate(this.rewardList, Reward::copy));
    }

    @Override
    public void generate(PlayerEntity player, Quest quest) {
        this.rewardList.forEach(reward -> reward.generate(player, quest));
    }

    @Override
    public void awardPlayer(PlayerEntity player, Quest quest) {
        this.rewardList.forEach(reward -> reward.awardPlayer(player, quest));
    }
}

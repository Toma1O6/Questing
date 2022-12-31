package dev.toma.questing.common.component.reward.provider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import dev.toma.questing.common.component.reward.instance.Reward;
import dev.toma.questing.common.component.reward.provider.RewardProvider;
import dev.toma.questing.common.component.reward.transformer.RewardTransformer;
import dev.toma.questing.common.component.reward.transformer.RewardTransformerType;
import dev.toma.questing.common.quest.Quest;
import net.minecraft.entity.player.PlayerEntity;

import java.util.List;

public abstract class VolumeBasedRewardProvider<R extends Reward> implements RewardProvider<R> {

    @SuppressWarnings("unchecked")
    public static final Codec<RewardTransformer<Integer>> VOLUME_ADJUSTER_CODEC = RewardTransformerType.CODEC.flatXmap(transformer -> {
        RewardTransformerType<?, ?> type = transformer.getType();
        if (!type.test(Integer.class)) {
            return DataResult.error("Incompatible reward transformer type - required Integer");
        }
        RewardTransformer<Integer> intTransformer = (RewardTransformer<Integer>) transformer;
        return DataResult.success(intTransformer);
    }, intTransformer -> intTransformer == null ? DataResult.error("Reward transformer is null") : DataResult.success(intTransformer));
    private final List<RewardTransformer<Integer>> countAdjusters;

    public VolumeBasedRewardProvider(List<RewardTransformer<Integer>> countAdjusters) {
        this.countAdjusters = countAdjusters;
    }

    public List<RewardTransformer<Integer>> getCountAdjusters() {
        return countAdjusters;
    }

    protected static int getCount(final int baseValue, PlayerEntity player, Quest quest, List<RewardTransformer<Integer>> countAdjusters) {
        int result = baseValue;
        for (RewardTransformer<Integer> transformer : countAdjusters) {
            result = transformer.adjust(result, player, quest);
        }
        return result;
    }
}

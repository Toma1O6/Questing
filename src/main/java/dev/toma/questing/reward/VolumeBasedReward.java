package dev.toma.questing.reward;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import dev.toma.questing.quest.Quest;
import net.minecraft.entity.player.PlayerEntity;

import java.util.List;

public abstract class VolumeBasedReward implements Reward {

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

    public VolumeBasedReward(List<RewardTransformer<Integer>> countAdjusters) {
        this.countAdjusters = countAdjusters;
    }

    protected List<RewardTransformer<Integer>> getCountAdjusters() {
        return countAdjusters;
    }

    protected int getCount(final int baseValue, PlayerEntity player, Quest quest) {
        int result = baseValue;
        for (RewardTransformer<Integer> transformer : countAdjusters) {
            result = transformer.adjust(result, player, quest);
        }
        return result;
    }
}

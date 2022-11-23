package dev.toma.questing.reward;

import com.google.gson.JsonArray;
import dev.toma.questing.quest.Quest;
import dev.toma.questing.utils.JsonHelper;
import net.minecraft.entity.player.PlayerEntity;

public abstract class VolumeBasedReward implements IReward {

    private final IRewardTransformer<Integer>[] countAdjusters;

    public VolumeBasedReward(IRewardTransformer<Integer>[] countAdjusters) {
        this.countAdjusters = countAdjusters;
    }

    protected int getCount(final int baseValue, PlayerEntity player, Quest quest) {
        int result = baseValue;
        for (IRewardTransformer<Integer> transformer : countAdjusters) {
            result = transformer.adjust(result, player, quest);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public static IRewardTransformer<Integer>[] resolveCountTransformers(JsonArray array) {
        return JsonHelper.mapArray(array, IRewardTransformer[]::new, element -> RewardTransformerType.fromJson(element, Integer.class));
    }
}

package dev.toma.questing.common.component.reward.provider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import dev.toma.questing.common.component.reward.instance.AbstractItemReward;
import dev.toma.questing.common.component.reward.transformer.RewardTransformer;
import dev.toma.questing.common.component.reward.transformer.RewardTransformerType;
import net.minecraft.item.ItemStack;

import java.util.List;

public abstract class AbstractItemRewardProvider<R extends AbstractItemReward<?>> extends VolumeBasedRewardProvider<R> {

    @SuppressWarnings("unchecked")
    public static final Codec<RewardTransformer<ItemStack>> ITEM_ADJUSTER_CODEC = RewardTransformerType.CODEC.flatXmap(transformer -> {
        RewardTransformerType<?, ?> type = transformer.getType();
        if (!type.test(ItemStack.class)) {
            return DataResult.error("Incompatible reward transformer type - required ItemList");
        }
        RewardTransformer<ItemStack> itemTransformer = (RewardTransformer<ItemStack>) transformer;
        return DataResult.success(itemTransformer);
    }, itemTransformer -> itemTransformer == null ? DataResult.error("Reward transformer is null") : DataResult.success(itemTransformer));
    private final List<RewardTransformer<ItemStack>> itemAdjusters;

    public AbstractItemRewardProvider(List<RewardTransformer<Integer>> countAdjusters, List<RewardTransformer<ItemStack>> itemAdjusters) {
        super(countAdjusters);
        this.itemAdjusters = itemAdjusters;
    }

    public List<RewardTransformer<ItemStack>> getItemAdjusters() {
        return itemAdjusters;
    }
}

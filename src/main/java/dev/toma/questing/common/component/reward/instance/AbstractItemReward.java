package dev.toma.questing.common.component.reward.instance;

import dev.toma.questing.common.component.reward.provider.AbstractItemRewardProvider;
import dev.toma.questing.common.component.reward.transformer.RewardTransformer;
import dev.toma.questing.common.quest.Quest;
import dev.toma.questing.utils.Utils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractItemReward<P extends AbstractItemRewardProvider<?>> implements Reward {

    protected final P provider;
    protected final List<ItemStack> rewards;

    public AbstractItemReward(P provider, PlayerEntity player, Quest quest) {
        this.provider = provider;
        this.rewards = this.applyTransformers(Utils.instantiate(this.getItems(player, quest), ItemStack::copy), player, quest);
    }

    public AbstractItemReward(P provider, List<ItemStack> rewards) {
        this.provider = provider;
        this.rewards = rewards;
    }

    public abstract List<ItemStack> getItems(PlayerEntity player, Quest quest);

    @Override
    public void award(PlayerEntity player, Quest quest) {
        this.rewards.forEach(stack -> Utils.giveItemToPlayer(stack, player));
    }

    @Override
    public P getProvider() {
        return this.provider;
    }

    protected List<ItemStack> applyTransformers(List<ItemStack> inputs, PlayerEntity player, Quest quest) {
        List<ItemStack> results = new ArrayList<>();
        for (ItemStack stack : inputs) {
            ItemStack ref = stack;
            for (RewardTransformer<ItemStack> itemAdjuster : provider.getItemAdjusters()) {
                ref = itemAdjuster.adjust(ref, player, quest);
            }
            int size = ref.getCount();
            for (RewardTransformer<Integer> countAdjuster : provider.getCountAdjusters()) {
                size = countAdjuster.adjust(size, player, quest);
            }
            List<ItemStack> largeItem = Utils.createLargeItemStack(ref, size);
            results.addAll(largeItem);
        }
        return results;
    }
}

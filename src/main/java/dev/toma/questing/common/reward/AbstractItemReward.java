package dev.toma.questing.common.reward;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import dev.toma.questing.common.quest.Quest;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

public abstract class AbstractItemReward extends VolumeBasedReward {

    @SuppressWarnings("unchecked")
    public static final Codec<RewardTransformer<ItemList>> ITEM_ADJUSTER_CODEC = RewardTransformerType.CODEC.flatXmap(transformer -> {
        RewardTransformerType<?, ?> type = transformer.getType();
        if (!type.test(ItemList.class)) {
            return DataResult.error("Incompatible reward transformer type - required ItemList");
        }
        RewardTransformer<ItemList> itemTransformer = (RewardTransformer<ItemList>) transformer;
        return DataResult.success(itemTransformer);
    }, itemTransformer -> itemTransformer == null ? DataResult.error("Reward transformer is null") : DataResult.success(itemTransformer));
    private final List<RewardTransformer<ItemList>> itemAdjusters;

    public AbstractItemReward(List<RewardTransformer<Integer>> countAdjusters, List<RewardTransformer<ItemList>> itemAdjusters) {
        super(countAdjusters);
        this.itemAdjusters = itemAdjusters;
    }

    protected abstract ItemList getItems(PlayerEntity player, Quest quest);

    public List<RewardTransformer<ItemList>> getItemAdjusters() {
        return itemAdjusters;
    }

    protected List<ItemStack> adjustCount(ItemStack stack, PlayerEntity player, Quest quest) {
        int base = stack.getCount();
        int count = getCount(base, player, quest);
        int limit = stack.getMaxStackSize();
        List<ItemStack> list = new ArrayList<>();
        while (count > 0) {
            int take = Math.min(limit, count);
            ItemStack itemStack = stack.copy();
            itemStack.setCount(take);
            list.add(itemStack);
            count -= take;
        }
        return list;
    }

    @Override
    public void awardPlayer(PlayerEntity player, Quest quest) {
        ItemList list = this.getItems(player, quest);
        for (RewardTransformer<ItemList> transformer : itemAdjusters) {
            list = transformer.adjust(list, player, quest);
        }
        for (ItemStack stack : list) {
            List<ItemStack> adjusted = this.adjustCount(stack, player, quest);
            adjusted.forEach(item -> giveToPlayer(item, player));
        }
    }

    public static void giveToPlayer(ItemStack stack, PlayerEntity player) {
        if (player.level.isClientSide) return;
        if (!player.addItem(stack)) {
            ItemEntity entity = new ItemEntity(player.level, player.getX(), player.getY(), player.getZ(), stack.copy());
            entity.setNoPickUpDelay();
            player.level.addFreshEntity(entity);
        }
    }

    public static List<ItemStack> createNonstandartSizeItemStacks(Function<Integer, ItemStack> itemFactory, int stackSize, int maxStackSize) {
        List<ItemStack> items = new ArrayList<>();
        int remaining = stackSize;
        while (remaining > 0) {
            int take = Math.min(maxStackSize, remaining);
            ItemStack stack = itemFactory.apply(take);
            remaining -= take;
            items.add(stack);
        }
        return items;
    }

    public static final class ItemList implements Iterable<ItemStack> {

        public final List<ItemStack> items;

        public ItemList(List<ItemStack> items) {
            this.items = items;
        }

        @Override
        public Iterator<ItemStack> iterator() {
            return items.iterator();
        }
    }
}

package dev.toma.questing.reward;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.toma.questing.quest.Quest;
import dev.toma.questing.utils.JsonHelper;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JSONUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

public abstract class AbstractItemReward extends VolumeBasedReward {

    private final RewardTransformer<ItemList>[] itemAdjusters;

    public AbstractItemReward(RewardTransformer<Integer>[] countAdjusters, RewardTransformer<ItemList>[] itemAdjusters) {
        super(countAdjusters);
        this.itemAdjusters = itemAdjusters;
    }

    protected abstract ItemList getItems(PlayerEntity player, Quest quest);

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

    @SuppressWarnings("unchecked")
    public static RewardTransformer<ItemList>[] resolveItemListTransformers(JsonArray array) {
        return JsonHelper.mapArray(array, RewardTransformer[]::new, element -> RewardTransformerType.fromJson(element, ItemList.class));
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

    public static abstract class AbstractSerializer<R extends AbstractItemReward> implements RewardType.RewardSerializer<R> {

        @Override
        public final R rewardFromJson(JsonObject data) {
            JsonArray countTransformers = JSONUtils.getAsJsonArray(data, "countFunctions", new JsonArray());
            RewardTransformer<Integer>[] counts = resolveCountTransformers(countTransformers);
            JsonArray itemTransformers = JSONUtils.getAsJsonArray(data, "itemFunctions", new JsonArray());
            RewardTransformer<ItemList>[] items = resolveItemListTransformers(itemTransformers);
            return this.resolveJson(data, counts, items);
        }

        public abstract R resolveJson(JsonObject data, RewardTransformer<Integer>[] counts, RewardTransformer<ItemList>[] items);
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

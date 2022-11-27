package dev.toma.questing.reward;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import dev.toma.questing.quest.Quest;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;
import net.minecraft.tags.TagCollectionManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ItemTagReward extends AbstractItemReward {

    private final ITag<Item> tag;
    private final int fetchCount;
    private final int itemCount;

    public ItemTagReward(RewardTransformer<Integer>[] countAdjusters, RewardTransformer<ItemList>[] itemAdjusters, ITag<Item> tag, int fetchCount, int itemCount) {
        super(countAdjusters, itemAdjusters);
        this.tag = tag;
        this.fetchCount = fetchCount;
        this.itemCount = itemCount;
    }

    @Override
    protected ItemList getItems(PlayerEntity player, Quest quest) {
        List<Item> items = this.tag.getValues();
        Random random = player.getRandom();
        List<ItemStack> generated = new ArrayList<>();
        for (int i = 0; i < this.fetchCount; i++) {
            Item item = items.get(random.nextInt(items.size()));
            List<ItemStack> split = createNonstandartSizeItemStacks(size -> new ItemStack(item, size), this.itemCount, item.getMaxStackSize());
            generated.addAll(split);
        }
        return new ItemList(generated);
    }

    public static final class Serializer extends AbstractSerializer<ItemTagReward> {

        @Override
        public ItemTagReward resolveJson(JsonObject data, RewardTransformer<Integer>[] counts, RewardTransformer<ItemList>[] items) {
            int fetchCount = JSONUtils.getAsInt(data, "fetchCount", 1);
            int count = JSONUtils.getAsInt(data, "count", 1);
            ResourceLocation location = new ResourceLocation(JSONUtils.getAsString(data, "tag"));
            ITag<Item> iTag = TagCollectionManager.getInstance().getItems().getTag(location);
            if (iTag == null) {
                throw new JsonSyntaxException("Unknown tag " + location);
            }
            return new ItemTagReward(counts, items, iTag, fetchCount, count);
        }
    }
}

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

    public ItemTagReward(IRewardTransformer<Integer>[] countAdjusters, IRewardTransformer<ItemList>[] itemAdjusters, ITag<Item> tag, int fetchCount) {
        super(countAdjusters, itemAdjusters);
        this.tag = tag;
        this.fetchCount = fetchCount;
    }

    @Override
    protected ItemList getItems(PlayerEntity player, Quest quest) {
        List<Item> items = this.tag.getValues();
        Random random = player.getRandom();
        List<ItemStack> generated = new ArrayList<>();
        for (int i = 0; i < this.fetchCount; i++) {
            ItemStack stack = new ItemStack(items.get(random.nextInt(items.size())));
            generated.add(stack);
        }
        return new ItemList(generated);
    }

    public static final class Serializer extends AbstractSerializer<ItemTagReward> {

        @Override
        public ItemTagReward resolveJson(JsonObject data, IRewardTransformer<Integer>[] counts, IRewardTransformer<ItemList>[] items) {
            int fetchCount = JSONUtils.getAsInt(data, "fetchCount", 1);
            ResourceLocation location = new ResourceLocation(JSONUtils.getAsString(data, "tag"));
            ITag<Item> iTag = TagCollectionManager.getInstance().getItems().getTag(location);
            if (iTag == null) {
                throw new JsonSyntaxException("Unknown tag " + location);
            }
            return new ItemTagReward(counts, items, iTag, fetchCount);
        }
    }
}

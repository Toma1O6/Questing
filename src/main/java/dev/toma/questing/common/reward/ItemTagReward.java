package dev.toma.questing.common.reward;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.toma.questing.common.init.QuestingRegistries;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;
import net.minecraft.tags.TagCollectionManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ItemTagReward extends AbstractItemReward {

    public static final Codec<ItemTagReward> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            VOLUME_ADJUSTER_CODEC.listOf().optionalFieldOf("countFunctions", Collections.emptyList()).forGetter(VolumeBasedReward::getCountAdjusters),
            ITEM_ADJUSTER_CODEC.listOf().optionalFieldOf("itemFunctions", Collections.emptyList()).forGetter(AbstractItemReward::getItemAdjusters),
            ITag.codec(() -> TagCollectionManager.getInstance().getItems()).fieldOf("tag").forGetter(t -> t.tag),
            Codec.intRange(1, Integer.MAX_VALUE).optionalFieldOf("fetchCount", 1).forGetter(t -> t.fetchCount),
            Codec.intRange(1, Integer.MAX_VALUE).optionalFieldOf("count", 1).forGetter(t -> t.itemCount),
            ItemList.CODEC.optionalFieldOf("generated", ItemList.EMPTY).forGetter(AbstractItemReward::getItemList)
    ).apply(instance, ItemTagReward::new));
    private final ITag<Item> tag;
    private final int fetchCount;
    private final int itemCount;

    public ItemTagReward(List<RewardTransformer<Integer>> countAdjusters, List<RewardTransformer<ItemList>> itemAdjusters, ITag<Item> tag, int fetchCount, int itemCount, ItemList itemList) {
        super(countAdjusters, itemAdjusters, itemList.isEmpty() ? createDefaultFilledItemList(tag, fetchCount, itemCount) : itemList);
        this.tag = tag;
        this.fetchCount = fetchCount;
        this.itemCount = itemCount;
    }

    @Override
    public RewardType<?> getType() {
        return QuestingRegistries.ITEMTAG_REWARD;
    }

    @Override
    public Reward copy() {
        return new ItemTagReward(this.getCountAdjusters(), this.getItemAdjusters(), this.tag, this.fetchCount, this.itemCount, ItemList.EMPTY);
    }

    protected static ItemList createDefaultFilledItemList(ITag<Item> tag, int fetchCount, int itemCount) {
        List<Item> items = tag.getValues();
        Random random = new Random();
        List<ItemStack> generated = new ArrayList<>();
        for (int i = 0; i < fetchCount; i++) {
            Item item = items.get(random.nextInt(items.size()));
            List<ItemStack> split = createNonstandartSizeItemStacks(size -> new ItemStack(item, size), itemCount, item.getMaxStackSize());
            generated.addAll(split);
        }
        return new ItemList(generated);
    }
}

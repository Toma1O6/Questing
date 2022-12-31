package dev.toma.questing.common.component.reward.provider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.toma.questing.common.component.reward.RewardType;
import dev.toma.questing.common.component.reward.instance.ItemTagReward;
import dev.toma.questing.common.component.reward.transformer.RewardTransformer;
import dev.toma.questing.common.init.QuestingRegistries;
import dev.toma.questing.common.quest.Quest;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;
import net.minecraft.tags.TagCollectionManager;

import java.util.Collections;
import java.util.List;

public class ItemTagRewardProvider extends AbstractItemRewardProvider<ItemTagReward> {

    public static final Codec<ItemTagRewardProvider> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            VOLUME_ADJUSTER_CODEC.listOf().optionalFieldOf("countFunctions", Collections.emptyList()).forGetter(VolumeBasedRewardProvider::getCountAdjusters),
            ITEM_ADJUSTER_CODEC.listOf().optionalFieldOf("itemFunctions", Collections.emptyList()).forGetter(AbstractItemRewardProvider::getItemAdjusters),
            ITag.codec(() -> TagCollectionManager.getInstance().getItems()).fieldOf("tag").forGetter(t -> t.tag),
            Codec.intRange(1, Integer.MAX_VALUE).optionalFieldOf("fetchCount", 1).forGetter(t -> t.fetchCount),
            Codec.intRange(1, Integer.MAX_VALUE).optionalFieldOf("count", 1).forGetter(t -> t.itemCount)
    ).apply(instance, ItemTagRewardProvider::new));
    private final ITag<Item> tag;
    private final int fetchCount;
    private final int itemCount;

    public ItemTagRewardProvider(List<RewardTransformer<Integer>> countAdjusters, List<RewardTransformer<ItemStack>> itemAdjusters, ITag<Item> tag, int fetchCount, int itemCount) {
        super(countAdjusters, itemAdjusters);
        this.tag = tag;
        this.fetchCount = fetchCount;
        this.itemCount = itemCount;
    }

    @Override
    public RewardType<ItemTagReward, ?> getType() {
        return QuestingRegistries.ITEMTAG_REWARD;
    }

    @Override
    public ItemTagReward createReward(PlayerEntity player, Quest quest) {
        return new ItemTagReward(this, player, quest);
    }

    public ITag<Item> getTag() {
        return tag;
    }

    public int getFetchCount() {
        return fetchCount;
    }

    public int getItemCount() {
        return itemCount;
    }
}

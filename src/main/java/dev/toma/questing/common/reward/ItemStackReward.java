package dev.toma.questing.common.reward;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.toma.questing.common.init.QuestingRegistries;
import dev.toma.questing.utils.Codecs;
import dev.toma.questing.utils.Utils;
import net.minecraft.item.ItemStack;

import java.util.Collections;
import java.util.List;

public class ItemStackReward extends AbstractItemReward {

    public static final Codec<ItemStackReward> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            VOLUME_ADJUSTER_CODEC.listOf().optionalFieldOf("countFunctions", Collections.emptyList()).forGetter(VolumeBasedReward::getCountAdjusters),
            ITEM_ADJUSTER_CODEC.listOf().optionalFieldOf("itemFunctions", Collections.emptyList()).forGetter(AbstractItemReward::getItemAdjusters),
            Codecs.SIMPLIFIED_ITEMSTACK.listOf().fieldOf("items").forGetter(type -> type.items),
            ItemList.CODEC.optionalFieldOf("generated", ItemList.EMPTY).forGetter(AbstractItemReward::getItemList)
    ).apply(instance, ItemStackReward::new));
    private final List<ItemStack> items;

    public ItemStackReward(List<RewardTransformer<Integer>> countAdjusters, List<RewardTransformer<ItemList>> itemAdjusters, List<ItemStack> items, ItemList itemList) {
        super(countAdjusters, itemAdjusters, itemList.isEmpty() ? new ItemList(Utils.instantiate(items, ItemStack::copy)) : itemList);
        this.items = items;
    }

    @Override
    public RewardType<?> getType() {
        return QuestingRegistries.ITEMSTACK_REWARD;
    }

    @Override
    public Reward copy() {
        return new ItemStackReward(this.getCountAdjusters(), this.getItemAdjusters(), this.items, ItemList.EMPTY);
    }
}

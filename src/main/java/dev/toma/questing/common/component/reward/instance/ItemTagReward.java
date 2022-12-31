package dev.toma.questing.common.component.reward.instance;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.toma.questing.Questing;
import dev.toma.questing.common.component.reward.provider.ItemTagRewardProvider;
import dev.toma.questing.common.quest.instance.Quest;
import dev.toma.questing.utils.Codecs;
import dev.toma.questing.utils.Utils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ItemTagReward extends AbstractItemReward<ItemTagRewardProvider> {

    public static final Codec<ItemTagReward> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemTagRewardProvider.CODEC.fieldOf("provider").forGetter(AbstractItemReward::getProvider),
            Codecs.SIMPLIFIED_ITEMSTACK.listOf().fieldOf("items").forGetter(t -> t.rewards)
    ).apply(instance, ItemTagReward::new));

    public ItemTagReward(ItemTagRewardProvider provider, PlayerEntity player, Quest quest) {
        super(provider, player, quest);
    }

    public ItemTagReward(ItemTagRewardProvider provider, List<ItemStack> rewards) {
        super(provider, rewards);
    }

    @Override
    public List<ItemStack> getItems(PlayerEntity player, Quest quest) {
        Random random = player.getRandom();
        List<ItemStack> items = new ArrayList<>();
        ITag<Item> itemTag = provider.getTag();
        List<Item> itemList = itemTag.getValues();
        if (itemList.isEmpty()) {
            Questing.LOGGER.warn(Questing.MARKER, "Generated empty reward due to empty item tag {}", itemTag);
            return Collections.emptyList();
        }
        int itemStackSize = provider.getItemCount();
        for (int i = 0; i < provider.getFetchCount(); i++) {
            Item item = Utils.getRandomListElement(itemList, random);
            ItemStack baseItem = new ItemStack(item);
            if (itemStackSize > baseItem.getMaxStackSize()) {
                List<ItemStack> itemStacks = Utils.createLargeItemStack(baseItem, itemStackSize);
                items.addAll(itemStacks);
            } else {
                baseItem.setCount(itemStackSize);
                items.add(baseItem);
            }
        }
        return items;
    }
}

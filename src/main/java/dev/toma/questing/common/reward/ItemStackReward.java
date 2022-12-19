package dev.toma.questing.common.reward;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.toma.questing.common.init.QuestingRegistries;
import dev.toma.questing.common.quest.Quest;
import dev.toma.questing.utils.Codecs;
import dev.toma.questing.utils.Utils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ItemStackReward extends AbstractItemReward {

    public static final Codec<ItemStackReward> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            VOLUME_ADJUSTER_CODEC.listOf().optionalFieldOf("countFunctions", Collections.emptyList()).forGetter(VolumeBasedReward::getCountAdjusters),
            ITEM_ADJUSTER_CODEC.listOf().optionalFieldOf("itemFunctions", Collections.emptyList()).forGetter(AbstractItemReward::getItemAdjusters),
            Codecs.SIMPLIFIED_ITEMSTACK.listOf().fieldOf("items").forGetter(type -> type.items)
    ).apply(instance, ItemStackReward::new));
    private final List<ItemStack> items;

    public ItemStackReward(List<RewardTransformer<Integer>> countAdjusters, List<RewardTransformer<ItemList>> itemAdjusters, List<ItemStack> items) {
        super(countAdjusters, itemAdjusters);
        this.items = items;
    }

    @Override
    public RewardType<?> getType() {
        return QuestingRegistries.ITEMSTACK_REWARD;
    }

    @Override
    protected ItemList getItems(PlayerEntity player, Quest quest) {
        return new ItemList(Utils.instantiate(items, ItemStack::copy));
    }
}

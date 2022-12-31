package dev.toma.questing.common.component.reward.provider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.toma.questing.common.component.reward.RewardType;
import dev.toma.questing.common.component.reward.instance.ItemStackReward;
import dev.toma.questing.common.component.reward.transformer.RewardTransformer;
import dev.toma.questing.common.init.QuestingRegistries;
import dev.toma.questing.common.quest.instance.Quest;
import dev.toma.questing.utils.Codecs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.Collections;
import java.util.List;

public class ItemStackRewardProvider extends AbstractItemRewardProvider<ItemStackReward> {

    public static final Codec<ItemStackRewardProvider> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            VOLUME_ADJUSTER_CODEC.listOf().optionalFieldOf("countFunctions", Collections.emptyList()).forGetter(VolumeBasedRewardProvider::getCountAdjusters),
            ITEM_ADJUSTER_CODEC.listOf().optionalFieldOf("itemFunctions", Collections.emptyList()).forGetter(AbstractItemRewardProvider::getItemAdjusters),
            Codecs.SIMPLIFIED_ITEMSTACK.listOf().fieldOf("items").forGetter(type -> type.items)
    ).apply(instance, ItemStackRewardProvider::new));
    private final List<ItemStack> items;

    public ItemStackRewardProvider(List<RewardTransformer<Integer>> countAdjusters, List<RewardTransformer<ItemStack>> itemAdjusters, List<ItemStack> items) {
        super(countAdjusters, itemAdjusters);
        this.items = items;
    }

    @Override
    public RewardType<ItemStackReward, ?> getType() {
        return QuestingRegistries.ITEMSTACK_REWARD;
    }

    @Override
    public ItemStackReward createReward(PlayerEntity player, Quest quest) {
        return new ItemStackReward(this, player, quest);
    }

    public List<ItemStack> getItems() {
        return items;
    }
}

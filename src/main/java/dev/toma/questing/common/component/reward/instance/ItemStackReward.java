package dev.toma.questing.common.component.reward.instance;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.toma.questing.common.component.reward.provider.ItemStackRewardProvider;
import dev.toma.questing.common.quest.instance.Quest;
import dev.toma.questing.utils.Codecs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.List;

public class ItemStackReward extends AbstractItemReward<ItemStackRewardProvider> {

    public static final Codec<ItemStackReward> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemStackRewardProvider.CODEC.fieldOf("provider").forGetter(AbstractItemReward::getProvider),
            Codecs.SIMPLIFIED_ITEMSTACK.listOf().fieldOf("items").forGetter(t -> t.rewards)
    ).apply(instance, ItemStackReward::new));

    public ItemStackReward(ItemStackRewardProvider provider, PlayerEntity player, Quest quest) {
        super(provider, player, quest);
    }

    public ItemStackReward(ItemStackRewardProvider provider, List<ItemStack> rewards) {
        super(provider, rewards);
    }

    @Override
    public List<ItemStack> getItems(PlayerEntity player, Quest quest) {
        return this.provider.getItems();
    }
}

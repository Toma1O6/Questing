package dev.toma.questing.common.component.reward.transformer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.toma.questing.common.init.QuestingRegistries;
import dev.toma.questing.common.quest.Quest;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

public class RewardItemNbtTransformer implements RewardTransformer<ItemStack> {

    public static final Codec<RewardItemNbtTransformer> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            CompoundNBT.CODEC.fieldOf("nbt").forGetter(ins -> ins.nbt)
    ).apply(builder, RewardItemNbtTransformer::new));
    private final CompoundNBT nbt;

    public RewardItemNbtTransformer(CompoundNBT nbt) {
        this.nbt = nbt;
    }

    @Override
    public ItemStack adjust(ItemStack originalValue, PlayerEntity player, Quest quest) {
        CompoundNBT copy = nbt.copy();
        originalValue.setTag(copy);
        return originalValue;
    }

    @Override
    public RewardTransformerType<?, ?> getType() {
        return QuestingRegistries.REWARD_ITEM_NBT_TRANSFORMER;
    }
}

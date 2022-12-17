package dev.toma.questing.reward;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.toma.questing.init.QuestingRegistries;
import dev.toma.questing.quest.Quest;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

public class RewardItemNbtTransformer implements RewardTransformer<AbstractItemReward.ItemList> {

    public static final Codec<RewardItemNbtTransformer> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            CompoundNBT.CODEC.fieldOf("nbt").forGetter(ins -> ins.nbt)
    ).apply(builder, RewardItemNbtTransformer::new));
    private final CompoundNBT nbt;

    public RewardItemNbtTransformer(CompoundNBT nbt) {
        this.nbt = nbt;
    }

    @Override
    public AbstractItemReward.ItemList adjust(AbstractItemReward.ItemList originalValue, PlayerEntity player, Quest quest) {
        for (ItemStack stack : originalValue) {
            CompoundNBT copy = nbt.copy();
            stack.setTag(copy);
        }
        return originalValue;
    }

    @Override
    public RewardTransformerType<?, ?> getType() {
        return QuestingRegistries.REWARD_ITEM_NBT_TRANSFORMER;
    }
}

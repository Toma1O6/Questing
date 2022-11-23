package dev.toma.questing.reward;

import com.google.gson.JsonObject;
import dev.toma.questing.quest.Quest;
import dev.toma.questing.utils.JsonHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.JSONUtils;

public class RewardItemNbtTransformer implements IRewardTransformer<AbstractItemReward.ItemList> {

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

    public static final class Serializer implements RewardTransformerType.Serializer<AbstractItemReward.ItemList, RewardItemNbtTransformer> {

        @Override
        public RewardItemNbtTransformer transformerFromJson(JsonObject data) {
            String rawNbt = JSONUtils.getAsString(data, "nbt");
            CompoundNBT nbt = JsonHelper.getNbt(rawNbt);
            return new RewardItemNbtTransformer(nbt);
        }
    }
}

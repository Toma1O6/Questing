package dev.toma.questing.reward;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import dev.toma.questing.quest.Quest;
import dev.toma.questing.utils.JsonHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ItemStackReward extends AbstractItemReward {

    private final ItemStack[] items;

    public ItemStackReward(IRewardTransformer<Integer>[] countAdjusters, IRewardTransformer<ItemList>[] itemAdjusters, ItemStack[] items) {
        super(countAdjusters, itemAdjusters);
        this.items = items;
    }

    @Override
    protected ItemList getItems(PlayerEntity player, Quest quest) {
        return new ItemList(Arrays.stream(this.items).map(ItemStack::copy).collect(Collectors.toList()));
    }

    public static final class Serializer extends AbstractSerializer<ItemStackReward> {

        @Override
        public ItemStackReward resolveJson(JsonObject data, IRewardTransformer<Integer>[] counts, IRewardTransformer<ItemList>[] items) {
            JsonArray itemArray = JSONUtils.getAsJsonArray(data, "items");
            List<ItemStack> rewardStacks = new ArrayList<>();
            for (JsonElement element : itemArray) {
                JsonObject itemData = JsonHelper.requireObject(element);
                ResourceLocation id = new ResourceLocation(JSONUtils.getAsString(itemData, "item"));
                if (!ForgeRegistries.ITEMS.containsKey(id)) {
                    throw new JsonSyntaxException("Unknown item: " + id);
                }
                Item item = ForgeRegistries.ITEMS.getValue(id);
                int itemCount = JSONUtils.getAsInt(itemData, "count", 1);
                CompoundNBT nbt = null;
                if (itemData.has("nbt")) {
                    String rawNbt = JSONUtils.getAsString(itemData, "nbt");
                    nbt = JsonHelper.getNbt(rawNbt);
                }
                CompoundNBT nbt1 = nbt; // (:
                List<ItemStack> itemList = createNonstandartSizeItemStacks(size -> {
                    ItemStack stack = new ItemStack(item, size);
                    stack.setTag(nbt1);
                    return stack;
                }, itemCount, item.getMaxStackSize());
                rewardStacks.addAll(itemList);
            }
            return new ItemStackReward(counts, items, rewardStacks.toArray(new ItemStack[0]));
        }
    }
}

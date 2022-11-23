package dev.toma.questing.reward;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.toma.questing.quest.Quest;
import dev.toma.questing.utils.JsonHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Arrays;
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
            ItemStack[] itemStacks = JsonHelper.mapArray(itemArray, ItemStack[]::new, this::resolve);
            return new ItemStackReward(counts, items, itemStacks);
        }

        private ItemStack resolve(JsonElement element) {
            JsonObject data = JsonHelper.requireObject(element);
            ResourceLocation id = new ResourceLocation(JSONUtils.getAsString(data, "id"));
            if (!ForgeRegistries.ITEMS.containsKey(id)) {
                throw new JsonSyntaxException("Unknown item: " + id);
            }
            Item item = ForgeRegistries.ITEMS.getValue(id);
            int count = JSONUtils.getAsInt(data, "count", 1);
            CompoundNBT nbt = null;
            if (data.has("nbt")) {
                String rawNbt = JSONUtils.getAsString(data, "nbt");
                nbt = JsonHelper.getNbt(rawNbt);
            }
            ItemStack stack = new ItemStack(item, count);
            stack.setTag(nbt);
            return stack;
        }
    }
}

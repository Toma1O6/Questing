package dev.toma.questing.area.spawner.processor;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import dev.toma.questing.area.Area;
import dev.toma.questing.area.spawner.Spawner;
import dev.toma.questing.quest.Quest;
import dev.toma.questing.utils.JsonHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.EnumMap;
import java.util.Map;

public class SetEquipmentProcessor extends LivingEntityProcessor {

    private final Map<EquipmentSlotType, ItemStack> equipmentMap;

    public SetEquipmentProcessor(Map<EquipmentSlotType, ItemStack> equipmentMap) {
        this.equipmentMap = equipmentMap;
    }

    @Override
    public void processEntitySpawn(LivingEntity entity, Spawner spawner, World world, Quest quest, Area area) {
        for (Map.Entry<EquipmentSlotType, ItemStack> entry : this.equipmentMap.entrySet()) {
            EquipmentSlotType slotType = entry.getKey();
            ItemStack stack = entry.getValue().copy();
            entity.setItemSlot(slotType, stack);
        }
    }

    @Override
    public SpawnerProcessorType<?> getType() {
        return null;
    }

    public static final class Serializer implements SpawnerProcessorType.Serializer<SetEquipmentProcessor> {

        @Override
        public SetEquipmentProcessor processorFromJson(JsonObject data) {
            JsonObject equipmentJson = JSONUtils.getAsJsonObject(data, "equipment");
            Map<EquipmentSlotType, ItemStack> equipment = new EnumMap<>(EquipmentSlotType.class);
            for (Map.Entry<String, JsonElement> entry : equipmentJson.entrySet()) {
                String equipmentKey = entry.getKey();
                EquipmentSlotType slotType;
                try {
                    slotType = EquipmentSlotType.byName(equipmentKey);
                } catch (IllegalArgumentException e) {
                    throw new JsonSyntaxException("Unknown equipment slot type: " + equipmentKey);
                }
                JsonObject itemData = JsonHelper.requireObject(entry.getValue());
                ResourceLocation itemId = new ResourceLocation(JSONUtils.getAsString(itemData, "item"));
                if (!ForgeRegistries.ITEMS.containsKey(itemId)) {
                    throw new JsonSyntaxException("Unknown item: " + itemId);
                }
                Item item = ForgeRegistries.ITEMS.getValue(itemId);
                CompoundNBT nbt = null;
                if (itemData.has("nbt")) {
                    String rawNbt = JSONUtils.getAsString(itemData, "nbt");
                    nbt = JsonHelper.getNbt(rawNbt);
                }
                ItemStack stack = new ItemStack(item);
                stack.setTag(nbt);
                equipment.put(slotType, stack);
            }
            return new SetEquipmentProcessor(equipment);
        }
    }
}

package dev.toma.questing.area.spawner.processor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.toma.questing.area.Area;
import dev.toma.questing.area.spawner.Spawner;
import dev.toma.questing.init.QuestingRegistries;
import dev.toma.questing.quest.Quest;
import dev.toma.questing.utils.Codecs;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.Map;
import java.util.Objects;

public class SetEquipmentProcessor extends LivingEntityProcessor {

    public static final Codec<SetEquipmentProcessor> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Codec.unboundedMap(
                    Codecs.enumCodec(EquipmentSlotType.class, EquipmentSlotType::byName, EquipmentSlotType::getName),
                    ItemStack.CODEC).fieldOf("equipment").forGetter(instance -> instance.equipmentMap)
    ).apply(builder, SetEquipmentProcessor::new));
    private final Map<EquipmentSlotType, ItemStack> equipmentMap;

    public SetEquipmentProcessor(Map<EquipmentSlotType, ItemStack> equipmentMap) {
        this.equipmentMap = Objects.requireNonNull(equipmentMap);
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
        return QuestingRegistries.EQUIPMENT_SPAWNER_PROCESSOR;
    }
}

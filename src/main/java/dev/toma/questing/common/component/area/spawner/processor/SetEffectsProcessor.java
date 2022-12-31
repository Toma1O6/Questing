package dev.toma.questing.common.component.area.spawner.processor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.toma.questing.common.component.area.instance.Area;
import dev.toma.questing.common.component.area.spawner.Spawner;
import dev.toma.questing.common.init.QuestingRegistries;
import dev.toma.questing.common.quest.Quest;
import dev.toma.questing.utils.EffectProvider;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;

import java.util.List;
import java.util.function.Supplier;

public class SetEffectsProcessor extends LivingEntityProcessor {

    public static final Codec<SetEffectsProcessor> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            EffectProvider.CODEC.listOf().fieldOf("effects").forGetter(processor -> processor.providerList)
    ).apply(builder, SetEffectsProcessor::new));

    private final List<EffectProvider> providerList;

    public SetEffectsProcessor(List<EffectProvider> providerList) {
        this.providerList = providerList;
    }

    @Override
    public void processEntitySpawn(LivingEntity entity, Spawner spawner, World world, Quest quest, Area area) {
        providerList.stream()
                .map(Supplier::get)
                .forEach(entity::addEffect);
    }

    @Override
    public SpawnerProcessorType<?> getType() {
        return QuestingRegistries.EFFECTS_SPAWNER_PROCESSOR;
    }

}

package dev.toma.questing.area.spawner.processor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.toma.questing.area.Area;
import dev.toma.questing.area.spawner.Spawner;
import dev.toma.questing.init.QuestingRegistries;
import dev.toma.questing.quest.Quest;
import dev.toma.questing.utils.EffectProvider;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.List;
import java.util.function.Supplier;

public class SetEffectsProcessor extends LivingEntityProcessor {

    private static final Codec<EffectProvider> PROVIDER_CODEC = RecordCodecBuilder.create(b -> b.group(
            Registry.MOB_EFFECT.fieldOf("effect").forGetter(EffectProvider::getEffect),
            Codec.intRange(1, Integer.MAX_VALUE).fieldOf("duration").orElse(100).forGetter(EffectProvider::getDuration),
            Codec.intRange(0, 255).fieldOf("amplifier").orElse(0).forGetter(EffectProvider::getAmplifier),
            Codec.BOOL.optionalFieldOf("ambient", false).forGetter(EffectProvider::isAmbient),
            Codec.BOOL.optionalFieldOf("visible", true).forGetter(EffectProvider::isVisible),
            Codec.BOOL.optionalFieldOf("showIcon", true).forGetter(EffectProvider::showIcon)
    ).apply(b, EffectProvider::new));
    public static final Codec<SetEffectsProcessor> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            PROVIDER_CODEC.listOf().fieldOf("effects").forGetter(processor -> processor.providerList)
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

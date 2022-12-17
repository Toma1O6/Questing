package dev.toma.questing.area.spawner.processor;

import com.mojang.serialization.Codec;
import dev.toma.questing.utils.IdentifierHolder;
import dev.toma.questing.init.QuestingRegistries;
import net.minecraft.util.ResourceLocation;

public class SpawnerProcessorType<P extends SpawnerProcessor> implements IdentifierHolder {

    public static final Codec<SpawnerProcessor> CODEC = QuestingRegistries.SPAWNER_PROCESSOR.dispatch("type", SpawnerProcessor::getType, type -> type.codec);
    private final ResourceLocation identifier;
    private final Codec<P> codec;

    public SpawnerProcessorType(ResourceLocation identifier, Codec<P> codec) {
        this.identifier = identifier;
        this.codec = codec;
    }

    @Override
    public ResourceLocation getIdentifier() {
        return identifier;
    }
}

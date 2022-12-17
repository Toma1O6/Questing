package dev.toma.questing.area.spawner;

import com.mojang.serialization.Codec;
import dev.toma.questing.utils.IdentifierHolder;
import dev.toma.questing.init.QuestingRegistries;
import net.minecraft.util.ResourceLocation;

public class SpawnerType<S extends Spawner> implements IdentifierHolder {

    public static final Codec<Spawner> CODEC = QuestingRegistries.SPAWNER.dispatch("type", Spawner::getType, spawnerType -> spawnerType.codec);
    private final ResourceLocation identifier;
    private final Codec<S> codec;

    public SpawnerType(ResourceLocation identifier, Codec<S> codec) {
        this.identifier = identifier;
        this.codec = codec;
    }

    @Override
    public ResourceLocation getIdentifier() {
        return identifier;
    }
}

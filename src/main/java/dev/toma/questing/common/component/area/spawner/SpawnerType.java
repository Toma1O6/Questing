package dev.toma.questing.common.component.area.spawner;

import com.mojang.serialization.Codec;
import dev.toma.questing.common.init.QuestingRegistries;
import dev.toma.questing.utils.IdentifierHolder;
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

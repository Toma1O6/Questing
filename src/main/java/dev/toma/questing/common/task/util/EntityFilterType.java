package dev.toma.questing.common.task.util;

import com.mojang.serialization.Codec;
import dev.toma.questing.common.init.QuestingRegistries;
import dev.toma.questing.utils.IdentifierHolder;
import net.minecraft.util.ResourceLocation;

public final class EntityFilterType<F extends EntityFilter> implements IdentifierHolder {

    public static final Codec<EntityFilter> CODEC = QuestingRegistries.ENTITY_FILTER.dispatch("type", EntityFilter::getType, type -> type.codec);
    private final ResourceLocation identifier;
    private final Codec<F> codec;

    public EntityFilterType(ResourceLocation identifier, Codec<F> codec) {
        this.identifier = identifier;
        this.codec = codec;
    }

    @Override
    public ResourceLocation getIdentifier() {
        return identifier;
    }
}

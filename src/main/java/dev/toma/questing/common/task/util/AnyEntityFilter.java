package dev.toma.questing.common.task.util;

import com.mojang.serialization.Codec;
import dev.toma.questing.common.init.QuestingRegistries;
import net.minecraft.entity.Entity;

public class AnyEntityFilter implements EntityFilter {

    public static final AnyEntityFilter ANY_ENTITY = new AnyEntityFilter();
    public static final Codec<AnyEntityFilter> CODEC = Codec.unit(ANY_ENTITY);

    private AnyEntityFilter() {}

    @Override
    public boolean acceptEntity(Entity entity) {
        return true;
    }

    @Override
    public EntityFilterType<?> getType() {
        return QuestingRegistries.ANY_ENTITY_FILTER;
    }
}

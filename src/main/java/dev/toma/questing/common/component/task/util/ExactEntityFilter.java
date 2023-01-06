package dev.toma.questing.common.component.task.util;

import com.mojang.serialization.Codec;
import dev.toma.questing.common.init.QuestingRegistries;
import dev.toma.questing.utils.Codecs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ExactEntityFilter implements EntityFilter {

    public static final Codec<ExactEntityFilter> CODEC = Codecs.forgeRegistryCodec(ForgeRegistries.ENTITIES).listOf()
            .xmap(list -> new ExactEntityFilter(new HashSet<>(list)), filter -> new ArrayList<>(filter.entityTypes))
            .fieldOf("entities").codec();

    private final Set<EntityType<?>> entityTypes;

    public ExactEntityFilter(Set<EntityType<?>> entityTypes) {
        this.entityTypes = entityTypes;
    }

    @Override
    public boolean acceptEntity(Entity entity) {
        EntityType<?> type = entity.getType();
        return this.entityTypes.contains(type);
    }

    @Override
    public EntityFilterType<?> getType() {
        return QuestingRegistries.EXACT_ENTITY_FILTER;
    }
}

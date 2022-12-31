package dev.toma.questing.common.component.task.util;

import com.mojang.serialization.Codec;
import dev.toma.questing.common.init.QuestingRegistries;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.MonsterEntity;

public class HostileEntityFilter implements EntityFilter {

    public static final HostileEntityFilter HOSTILE_MOBS = new HostileEntityFilter();
    public static final Codec<HostileEntityFilter> CODEC = Codec.unit(HOSTILE_MOBS);

    private HostileEntityFilter() {}

    @Override
    public boolean acceptEntity(Entity entity) {
        return entity instanceof MonsterEntity;
    }

    @Override
    public EntityFilterType<?> getType() {
        return QuestingRegistries.HOSTILE_ENTITY_FILTER;
    }
}

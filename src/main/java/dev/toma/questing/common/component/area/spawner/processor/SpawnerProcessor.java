package dev.toma.questing.common.component.area.spawner.processor;

import dev.toma.questing.common.component.area.instance.Area;
import dev.toma.questing.common.component.area.spawner.Spawner;
import dev.toma.questing.common.quest.Quest;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public interface SpawnerProcessor {

    void processEntitySpawn(Entity entity, Spawner spawner, World world, Quest quest, Area area);

    SpawnerProcessorType<?> getType();
}

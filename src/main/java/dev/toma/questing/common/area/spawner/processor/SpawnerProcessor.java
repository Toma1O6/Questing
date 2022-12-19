package dev.toma.questing.common.area.spawner.processor;

import dev.toma.questing.common.area.Area;
import dev.toma.questing.common.area.spawner.Spawner;
import dev.toma.questing.common.quest.Quest;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public interface SpawnerProcessor {

    void processEntitySpawn(Entity entity, Spawner spawner, World world, Quest quest, Area area);

    SpawnerProcessorType<?> getType();
}

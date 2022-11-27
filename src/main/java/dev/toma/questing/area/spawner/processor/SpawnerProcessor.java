package dev.toma.questing.area.spawner.processor;

import dev.toma.questing.area.Area;
import dev.toma.questing.area.spawner.Spawner;
import dev.toma.questing.quest.Quest;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public interface SpawnerProcessor {

    void processEntitySpawn(Entity entity, Spawner spawner, World world, Quest quest, Area area);

    SpawnerProcessorType<?> getType();
}

package dev.toma.questing.common.component.area.spawner;

import dev.toma.questing.common.component.area.instance.Area;
import dev.toma.questing.common.quest.instance.Quest;
import net.minecraft.world.World;

public interface Spawner {

    void trySpawn(World world, Area area, Quest quest);

    SpawnerType<?> getType();

    Spawner copy();
}

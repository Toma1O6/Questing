package dev.toma.questing.area.spawner;

import dev.toma.questing.area.Area;
import dev.toma.questing.quest.Quest;
import net.minecraft.world.World;

public interface Spawner {

    void tick(World world, Area area, Quest quest);

    SpawnerType<?> getType();
}

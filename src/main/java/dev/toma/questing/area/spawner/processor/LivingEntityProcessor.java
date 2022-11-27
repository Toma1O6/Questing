package dev.toma.questing.area.spawner.processor;

import dev.toma.questing.Questing;
import dev.toma.questing.area.Area;
import dev.toma.questing.area.spawner.Spawner;
import dev.toma.questing.quest.Quest;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;

public abstract class LivingEntityProcessor implements SpawnerProcessor {

    @Override
    public final void processEntitySpawn(Entity entity, Spawner spawner, World world, Quest quest, Area area) {
        if (!(entity instanceof LivingEntity)) {
            Questing.LOGGER.warn(Questing.MARKER_AREA, "Unable to spawn entity {} as this spawn processor '{}' requires living entities", entity.getType(), this.getType().getIdentifier());
        }
        this.processEntitySpawn((LivingEntity) entity, spawner, world, quest, area);
    }

    public abstract void processEntitySpawn(LivingEntity entity, Spawner spawner, World world, Quest quest, Area area);
}

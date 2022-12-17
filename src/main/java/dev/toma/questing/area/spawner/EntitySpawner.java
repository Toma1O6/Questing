package dev.toma.questing.area.spawner;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.toma.questing.area.Area;
import dev.toma.questing.area.spawner.processor.SpawnerProcessor;
import dev.toma.questing.area.spawner.processor.SpawnerProcessorType;
import dev.toma.questing.init.QuestingRegistries;
import dev.toma.questing.quest.Quest;
import dev.toma.questing.utils.Codecs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class EntitySpawner implements Spawner {

    public static final Codec<EntitySpawner> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codecs.enumCodec(SpawnMode.class).optionalFieldOf("mode", SpawnMode.GROUND).forGetter(spawner -> spawner.spawnMode),
            ResourceLocation.CODEC.flatXmap(location -> {
                if (!ForgeRegistries.ENTITIES.containsKey(location)) {
                    return DataResult.error("Unknown entity " + location);
                }
                return DataResult.success(ForgeRegistries.ENTITIES.getValue(location));
            }, type -> type == null ? DataResult.error("Entity type is null") : DataResult.success(type.getRegistryName())).fieldOf("entity").forGetter(EntitySpawner::getEntity),
            Codec.intRange(1, 64).optionalFieldOf("min", 1).forGetter(spawner -> spawner.minCount),
            Codec.intRange(1, 64).optionalFieldOf("max", 1).forGetter(spawner -> spawner.maxCount),
            SpawnerProcessorType.CODEC.listOf().optionalFieldOf("processors", Collections.emptyList()).forGetter(spawner -> spawner.processors)
    ).apply(instance, EntitySpawner::new));
    protected final SpawnMode spawnMode;
    protected final EntityType<?> entity;
    private final int minCount;
    private final int maxCount;
    protected final List<SpawnerProcessor> processors;

    public EntitySpawner(SpawnMode spawnMode, EntityType<?> entity, int minCount, int maxCount, List<SpawnerProcessor> processors) {
        this.spawnMode = spawnMode;
        this.entity = entity;
        this.minCount = minCount;
        this.maxCount = maxCount;
        this.processors = processors;
    }

    @Override
    public void tick(World world, Area area, Quest quest) {
        Random random = world.getRandom();
        int spawnCount = this.minCount + random.nextInt(this.maxCount - this.minCount + 1);
        for (int i = 0; i < spawnCount; i++) {
            this.spawnMob(world, area, quest);
        }
    }

    @Override
    public SpawnerType<?> getType() {
        return QuestingRegistries.ENTITY_SPAWNER;
    }

    @Override
    public Spawner copy() {
        return new EntitySpawner(spawnMode, entity, minCount, maxCount, processors);
    }

    protected void spawnMob(World world, Area area, Quest quest) {
        Entity entity = this.entity.create(world);
        BlockPos pos = this.pickSpawnLocation(world, area, quest, entity);
        entity.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        world.addFreshEntity(entity);
        this.entityAddedToWorld(world, area, quest, entity);
        for (SpawnerProcessor processor : this.processors) {
            processor.processEntitySpawn(entity, this, world, quest, area);
        }
    }

    protected void entityAddedToWorld(World world, Area area, Quest quest, Entity entity) {
    }

    protected BlockPos pickSpawnLocation(World world, Area area, Quest quest, Entity entity) {
        Vector3d a = area.getLeftCorner();
        Vector3d b = area.getRightCorner();
        Random random = world.getRandom();
        int i = random.nextInt(4);
        int minX = (int) a.x;
        int minZ = (int) a.y;
        int maxX = (int) b.x;
        int maxZ = (int) b.y;
        switch (i) {
            case 0: {
                int z = minZ + random.nextInt(maxZ - minZ + 1);
                int y = this.getYCoordinate(world, minX, z);
                return new BlockPos(minX, y, z);
            }
            case 1: {
                int x = minX + random.nextInt(maxX - minX + 1);
                int y = this.getYCoordinate(world, x, minZ);
                return new BlockPos(x, y, minZ);
            }
            case 2: {
                int z = minZ + random.nextInt(maxZ - minZ + 1);
                int y = this.getYCoordinate(world, maxX, z);
                return new BlockPos(maxX, y, z);
            }
            default: {
                int x = minX + random.nextInt(maxX - minX + 1);
                int y = this.getYCoordinate(world, x, maxZ);
                return new BlockPos(minX, y, maxZ);
            }
        }
    }

    protected int getYCoordinate(World world, int x, int z) {
        int y = world.getHeight(Heightmap.Type.WORLD_SURFACE, x, z);
        if (spawnMode == SpawnMode.AIR) {
            y += 15;
        }
        return y;
    }

    protected EntityType getEntity() {
        return entity;
    }

    public enum SpawnMode {

        GROUND,
        AIR
    }
}

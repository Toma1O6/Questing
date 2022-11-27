package dev.toma.questing.area.spawner;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import dev.toma.questing.area.Area;
import dev.toma.questing.area.spawner.processor.SpawnerProcessor;
import dev.toma.questing.area.spawner.processor.SpawnerProcessorType;
import dev.toma.questing.init.QuestingRegistries;
import dev.toma.questing.quest.Quest;
import dev.toma.questing.utils.JsonHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Random;

public class EntitySpawner implements Spawner {

    protected final SpawnMode spawnMode;
    protected final EntityType<?> entity;
    private final int minCount;
    private final int maxCount;
    protected final SpawnerProcessor[] processors;

    public EntitySpawner(SpawnMode spawnMode, EntityType<?> entity, int minCount, int maxCount, SpawnerProcessor[] processors) {
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

    public enum SpawnMode {

        GROUND,
        AIR
    }

    public static final class Serializer implements SpawnerType.Serializer<EntitySpawner> {

        @Override
        public SpawnerProvider<EntitySpawner> spawnerFromJson(JsonObject data) {
            String modeId = JSONUtils.getAsString(data, "spawnMode", SpawnMode.GROUND.name());
            SpawnMode mode;
            try {
                mode = SpawnMode.valueOf(modeId);
            } catch (IllegalArgumentException e) {
                throw new JsonSyntaxException("Unknown spawn mode: " + modeId);
            }
            ResourceLocation entityId = new ResourceLocation(JSONUtils.getAsString(data, "entity"));
            if (!ForgeRegistries.ENTITIES.containsKey(entityId)) {
                throw new JsonSyntaxException("Unknown entity: " + entityId);
            }
            EntityType<?> type = ForgeRegistries.ENTITIES.getValue(entityId);
            int minCount = JSONUtils.getAsInt(data, "min", 1);
            int maxCount = JSONUtils.getAsInt(data, "max", minCount);
            JsonArray processorArray = JSONUtils.getAsJsonArray(data, "processors", new JsonArray());
            SpawnerProcessor[] processors = JsonHelper.mapArray(processorArray, SpawnerProcessor[]::new, SpawnerProcessorType::fromJson);
            return () -> new EntitySpawner(mode, type, minCount, maxCount, processors);
        }
    }
}

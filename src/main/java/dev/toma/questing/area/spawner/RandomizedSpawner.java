package dev.toma.questing.area.spawner;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.toma.questing.area.Area;
import dev.toma.questing.init.QuestingRegistries;
import dev.toma.questing.quest.Quest;
import dev.toma.questing.utils.JsonHelper;
import dev.toma.questing.utils.Utils;
import net.minecraft.util.JSONUtils;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class RandomizedSpawner implements Spawner {

    private final float chance;
    private final List<Spawner> spawnerList;

    public RandomizedSpawner(float chance, SpawnerProvider<?>[] providers) {
        this.chance = chance;
        this.spawnerList = Utils.getProvidedSpawners(Arrays.stream(providers));
    }

    @Override
    public void tick(World world, Area area, Quest quest) {
        Random random = world.getRandom();
        if (random.nextFloat() <= this.chance) {
            this.spawnerList.forEach(spawner -> spawner.tick(world, area, quest));
        }
    }

    @Override
    public SpawnerType<?> getType() {
        return QuestingRegistries.RANDOMIZED_SPAWNER;
    }

    public static final class Serializer implements SpawnerType.Serializer<RandomizedSpawner> {

        @Override
        public SpawnerProvider<RandomizedSpawner> spawnerFromJson(JsonObject data) {
            float chance = JSONUtils.getAsFloat(data, "chance");
            JsonArray array = JSONUtils.getAsJsonArray(data, "spawners");
            SpawnerProvider<?>[] providers = JsonHelper.mapArray(array, SpawnerProvider[]::new, SpawnerType::fromJson);
            return () -> new RandomizedSpawner(chance, providers);
        }
    }
}

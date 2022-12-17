package dev.toma.questing.area.spawner;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.toma.questing.area.Area;
import dev.toma.questing.init.QuestingRegistries;
import dev.toma.questing.quest.Quest;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public class RandomizedSpawner implements Spawner {

    public static final Codec<RandomizedSpawner> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.floatRange(0.0F, 1.0F).fieldOf("chance").forGetter(spanwer -> spanwer.chance),
            SpawnerType.CODEC.listOf().fieldOf("spawners").forGetter(spawner -> spawner.spawnerList)
    ).apply(instance, RandomizedSpawner::new));
    private final float chance;
    private final List<Spawner> spawnerList;

    public RandomizedSpawner(float chance, List<Spawner> spawners) {
        this.chance = chance;
        this.spawnerList = spawners;
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

    @Override
    public Spawner copy() {
        return new RandomizedSpawner(chance, spawnerList);
    }
}

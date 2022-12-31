package dev.toma.questing.common.component.area.spawner;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.toma.questing.common.component.area.instance.Area;
import dev.toma.questing.common.init.QuestingRegistries;
import dev.toma.questing.common.quest.instance.Quest;
import dev.toma.questing.utils.Utils;
import net.minecraft.world.World;

import java.util.List;

public class WaveBasedSpawner implements Spawner {

    public static final Codec<WaveBasedSpawner> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.intRange(0, Integer.MAX_VALUE).fieldOf("interval").forGetter(spawner -> spawner.spawnInterval),
            Codec.INT.optionalFieldOf("waves", -1).forGetter(spawner -> spawner.waveCount),
            SpawnerType.CODEC.listOf().fieldOf("spawners").forGetter(spawner -> spawner.spawners)
    ).apply(instance, WaveBasedSpawner::new));
    private final int spawnInterval;
    private final int waveCount;
    private final List<Spawner> spawners;
    private final boolean waveLimit;
    private int currentDelay;
    private int wavesLeft;

    public WaveBasedSpawner(int spawnInterval, int waveCount, List<Spawner> spawners) {
        this.spawnInterval = spawnInterval;
        this.waveCount = waveCount;
        this.spawners = spawners;
        this.waveLimit = waveCount >= 0;
        this.currentDelay = this.spawnInterval;
        this.wavesLeft = this.waveCount;
    }

    @Override
    public void trySpawn(World world, Area area, Quest quest) {
        if (!this.hasWave())
            return;
        if (--this.currentDelay <= 0) {
            this.currentDelay = this.spawnInterval;
            this.spawners.forEach(spawner -> spawner.trySpawn(world, area, quest));
            if (this.waveLimit) {
                --this.wavesLeft;
            }
        }
    }

    @Override
    public SpawnerType<?> getType() {
        return QuestingRegistries.WAVE_BASED_SPAWNER;
    }

    @Override
    public Spawner copy() {
        return new WaveBasedSpawner(spawnInterval, waveCount, Utils.instantiate(spawners, Spawner::copy));
    }

    protected boolean hasWave() {
        return !this.waveLimit || this.wavesLeft > 0;
    }
}

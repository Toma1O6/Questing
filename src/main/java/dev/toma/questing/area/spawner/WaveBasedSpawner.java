package dev.toma.questing.area.spawner;

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

public class WaveBasedSpawner implements Spawner {

    private final int spawnInterval;
    private final int waveCount;
    private final List<Spawner> spawners;
    private final boolean waveLimit;
    private int currentDelay;
    private int wavesLeft;

    public WaveBasedSpawner(int spawnInterval, int waveCount, SpawnerProvider<?>[] spawnerProviders) {
        this.spawnInterval = spawnInterval;
        this.waveCount = waveCount;
        this.spawners = Utils.getProvidedSpawners(Arrays.stream(spawnerProviders));
        this.waveLimit = waveCount >= 0;
        this.currentDelay = this.spawnInterval;
        this.wavesLeft = this.waveCount;
    }

    @Override
    public void tick(World world, Area area, Quest quest) {
        if (!this.hasWave())
            return;
        if (--this.currentDelay <= 0) {
            this.currentDelay = this.spawnInterval;
            this.spawners.forEach(spawner -> spawner.tick(world, area, quest));
            if (this.waveLimit) {
                --this.wavesLeft;
            }
        }
    }

    @Override
    public SpawnerType<?> getType() {
        return QuestingRegistries.WAVE_BASED_SPAWNER;
    }

    protected boolean hasWave() {
        return !this.waveLimit || this.wavesLeft > 0;
    }

    public static final class Serializer implements SpawnerType.Serializer<WaveBasedSpawner> {

        @Override
        public SpawnerProvider<WaveBasedSpawner> spawnerFromJson(JsonObject data) {
            int spawnInterval = JSONUtils.getAsInt(data, "interval");
            int waves = JSONUtils.getAsInt(data, "waves", -1);
            SpawnerProvider<?>[] providers = JsonHelper.mapArray(JSONUtils.getAsJsonArray(data, "spawners"), SpawnerProvider[]::new, SpawnerType::fromJson);
            return () -> new WaveBasedSpawner(spawnInterval, waves, providers);
        }
    }
}

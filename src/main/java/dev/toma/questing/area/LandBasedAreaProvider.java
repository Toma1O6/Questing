package dev.toma.questing.area;

import com.google.gson.JsonObject;
import dev.toma.questing.area.spawner.SpawnerProvider;
import dev.toma.questing.quest.Quest;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import java.util.List;

public class LandBasedAreaProvider extends SimpleAreaProvider {

    public LandBasedAreaProvider(int distanceMin, int distanceMax, int areaSize, AreaInteractionMode interactionMode, List<SpawnerProvider<?>> spawners) {
        super(distanceMin, distanceMax, areaSize, interactionMode, spawners);
    }

    @Override
    public boolean isValidLocationForArea(World world, Quest quest, BlockPos position) {
        Biome biome = world.getBiome(position);
        Biome.Category category = biome.getBiomeCategory();
        return category != Biome.Category.RIVER && category != Biome.Category.OCEAN;
    }

    @Override
    public AreaType<?> getAreaType() {
        return null;
    }

    public static final class Serializer extends SimpleAreaProvider.AbstractSerializer<LandBasedAreaProvider> {

        @Override
        public LandBasedAreaProvider create(int distanceMin, int distanceMax, int size, AreaInteractionMode interactionMode, List<SpawnerProvider<?>> providers, JsonObject data) {
            return new LandBasedAreaProvider(distanceMin, distanceMax, size, interactionMode, providers);
        }
    }
}

package dev.toma.questing.area;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.toma.questing.area.spawner.Spawner;
import dev.toma.questing.area.spawner.SpawnerType;
import dev.toma.questing.init.QuestingRegistries;
import dev.toma.questing.quest.Quest;
import dev.toma.questing.utils.Codecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import java.util.Collections;
import java.util.List;

public class LandBasedAreaProvider extends SimpleAreaProvider {

    public static final Codec<LandBasedAreaProvider> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.intRange(0, Integer.MAX_VALUE).fieldOf("minDistance").forGetter(SimpleAreaProvider::getDistanceMin),
            Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("maxDistance", 0).forGetter(SimpleAreaProvider::getDistanceMax),
            Codec.intRange(0, Integer.MAX_VALUE).fieldOf("size").forGetter(SimpleAreaProvider::getAreaSize),
            Codecs.enumCodec(AreaInteractionMode.class).optionalFieldOf("interactionMode", AreaInteractionMode.NO_INTERACTION).forGetter(SimpleAreaProvider::getInteractionMode),
            SpawnerType.CODEC.listOf().optionalFieldOf("spawners", Collections.emptyList()).forGetter(SimpleAreaProvider::getMobSpawners)
    ).apply(instance, LandBasedAreaProvider::new));

    public LandBasedAreaProvider(int distanceMin, int distanceMax, int areaSize, AreaInteractionMode interactionMode, List<Spawner> spawners) {
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
        return QuestingRegistries.LAND_AREA;
    }
}

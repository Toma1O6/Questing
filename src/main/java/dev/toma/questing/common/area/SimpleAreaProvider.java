package dev.toma.questing.common.area;

import dev.toma.questing.Questing;
import dev.toma.questing.common.area.spawner.Spawner;
import dev.toma.questing.common.quest.Quest;
import dev.toma.questing.utils.Utils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public abstract class SimpleAreaProvider implements AreaProvider<SimpleArea> {

    private final int distanceMin;
    private final int distanceMax;
    private final int areaSize;
    private final AreaInteractionMode interactionMode;
    private final List<Spawner> mobSpawners;
    private final int generationAttempts;

    public SimpleAreaProvider(int distanceMin, int distanceMax, int areaSize, AreaInteractionMode interactionMode, List<Spawner> mobSpawners) {
        this.distanceMin = distanceMin;
        this.distanceMax = Math.max(this.distanceMin, distanceMax);
        this.areaSize = areaSize;
        this.interactionMode = interactionMode;
        this.mobSpawners = mobSpawners;
        this.generationAttempts = this.getGenerationAttemptCount();
    }

    public abstract boolean isValidLocationForArea(World world, Quest quest, BlockPos position);

    @Override
    public SimpleArea generateArea(World world, Quest quest, Vector3d sourcePosition) {
        Questing.LOGGER.debug(Questing.MARKER_AREA, "Generating new area from {} position. Area {}", sourcePosition, this);
        Random random = world.getRandom();
        int attempt = 0;
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        int srcX = (int) sourcePosition.x;
        int srcY = (int) sourcePosition.y;
        int srcZ = (int) sourcePosition.z;
        while (true) {
            int x = srcX + this.distanceMin + random.nextInt(this.distanceMax - this.distanceMin + 1);
            int z = srcZ + this.distanceMin + random.nextInt(this.distanceMax - this.distanceMin + 1);
            int y = this.selectHeightCoordinate(world, x, srcY, z);
            Questing.LOGGER.debug(Questing.MARKER_AREA, "Area generate attempt #{} at [{};{};{}] for area {}", attempt + 1, x, y, z, this);
            mutable.set(x, y, z);
            if (this.isValidLocationForArea(world, quest, mutable.immutable())) {
                break;
            }
            Questing.LOGGER.debug(Questing.MARKER_AREA, "Area generation failed, retrying...");
            if (attempt++ >= this.generationAttempts) {
                Questing.LOGGER.warn(Questing.MARKER_AREA, "Area generation has run out of {} retry attempts, default area center will be used", this.generationAttempts);
                break;
            }
        }
        BlockPos areaCenter = this.createDefaultArea(mutable, srcX, srcY, srcZ);
        if (areaCenter == null) {
            throw new IllegalStateException("Area center is null! " + this);
        }
        List<Spawner> spawners = Utils.instantiate(this.mobSpawners, Spawner::copy);
        return new SimpleArea(this, areaCenter, this.interactionMode, this.areaSize, spawners);
    }

    @Override
    public AreaInteractionMode getInteractionMode() {
        return this.interactionMode;
    }

    protected int getGenerationAttemptCount() {
        return 100;
    }

    protected int selectHeightCoordinate(World world, int x, int y, int z) {
        return y;
    }

    protected BlockPos createDefaultArea(@Nullable BlockPos.Mutable generatedPosition, int x, int y, int z) {
        return generatedPosition != null ? generatedPosition.immutable() : new BlockPos(x, y, z);
    }

    public int getDistanceMin() {
        return distanceMin;
    }

    public int getDistanceMax() {
        return distanceMax;
    }

    public int getAreaSize() {
        return areaSize;
    }

    public List<Spawner> getMobSpawners() {
        return mobSpawners;
    }
}

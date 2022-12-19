package dev.toma.questing.common.area;

import dev.toma.questing.common.area.spawner.Spawner;
import dev.toma.questing.common.quest.Quest;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.List;

public class SimpleArea implements Area {

    private final AreaProvider<?> provider;
    private final BlockPos center;
    private final Vector3d a, b;
    private final AreaInteractionMode interactionMode;
    private final List<Spawner> spawnerList;
    private boolean active;

    public SimpleArea(AreaProvider<?> provider, BlockPos center, AreaInteractionMode interactionMode, int size, List<Spawner> spawnerList) {
        this.provider = provider;
        this.center = center;
        this.a = new Vector3d(center.getX() + 0.5 - size, center.getY(), center.getZ() + 0.5 - size);
        this.b = new Vector3d(center.getX() + 0.5 + size, center.getY(), center.getZ() + 0.5 + size);
        this.interactionMode = interactionMode;
        this.spawnerList = spawnerList;
    }

    @Override
    public void onActivated(World world, Quest quest) {
        this.active = true;
    }

    @Override
    public void onUpdate(World world, Quest quest) {
        if (this.active) {
            this.spawnerList.forEach(spawner -> spawner.tick(world, this, quest));
        }
    }

    @Override
    public Vector3d getLeftCorner() {
        return this.a;
    }

    @Override
    public Vector3d getRightCorner() {
        return this.b;
    }

    @Override
    public double getEntityDistance(Entity entity) {
        double x = this.center.getX() + 0.5 - entity.getX();
        double z = this.center.getZ() + 0.5 - entity.getZ();
        return Math.sqrt(x * x + z * z);
    }

    @Override
    public boolean isActiveArea() {
        return this.active;
    }

    @Override
    public boolean isWithin(double x, double y, double z) {
        return x >= this.a.x && z >= this.a.z && x <= this.b.x && z <= this.b.z;
    }

    @Override
    public AreaInteractionMode getInteractionMode() {
        return interactionMode;
    }

    @Override
    public AreaType<?> getAreaType() {
        return this.provider.getAreaType();
    }
}

package dev.toma.questing.area;

import dev.toma.questing.quest.Quest;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public interface Area {

    void onActivated(World world, Quest quest);

    void onUpdate(World world, Quest quest);

    double getEntityDistance(Entity entity);

    boolean isActiveArea();

    Vector3d getLeftCorner();

    Vector3d getRightCorner();

    AreaInteractionMode getInteractionMode();

    AreaType<?> getAreaType();

    boolean isWithin(double x, double y, double z);

    default boolean isWithin(Vector3d vector3d) {
        return this.isWithin(vector3d.x, vector3d.y, vector3d.z);
    }

    default boolean isWithin(BlockPos blockPos) {
        return this.isWithin(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5);
    }

    default boolean isWithin(Entity entity) {
        return this.isWithin(entity.getX(), entity.getY(), entity.getZ());
    }
}

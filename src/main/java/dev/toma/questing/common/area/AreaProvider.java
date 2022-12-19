package dev.toma.questing.common.area;

import dev.toma.questing.common.quest.Quest;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public interface AreaProvider<A extends Area> {

    A generateArea(World world, Quest quest, Vector3d sourcePosition);

    AreaInteractionMode getInteractionMode();

    AreaType<?> getAreaType();
}

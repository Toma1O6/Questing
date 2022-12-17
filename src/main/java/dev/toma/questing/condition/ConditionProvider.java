package dev.toma.questing.condition;

import dev.toma.questing.quest.Quest;
import net.minecraft.world.World;

public interface ConditionProvider<C extends Condition> {

    ConditionType<?> getType();

    C createConditionInstance(World world, Quest quest);
}

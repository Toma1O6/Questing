package dev.toma.questing.common.condition;

import dev.toma.questing.common.party.Party;
import dev.toma.questing.common.quest.Quest;
import net.minecraft.world.World;

public abstract class Condition {

    private final ConditionProvider<?> provider;

    public Condition(ConditionProvider<?> provider) {
        this.provider = provider;
    }

    public final ConditionProvider<?> getProvider() {
        return this.provider;
    }

    public abstract void registerTriggerResponders(ConditionRegisterHandler registerHandler);

    public void onConditionConstructing(Party party, Quest quest, World world) {}
}

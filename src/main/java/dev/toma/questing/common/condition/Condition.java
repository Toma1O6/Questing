package dev.toma.questing.common.condition;

import dev.toma.questing.common.party.Party;
import dev.toma.questing.common.quest.Quest;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

import java.util.Set;
import java.util.UUID;

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

    public static boolean checkIfEntityIsPartyMember(Entity entity, Party party) {
        if (entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            UUID playerId = player.getUUID();
            Set<UUID> members = party.getMembers();
            return members.contains(playerId);
        }
        return false;
    }
}

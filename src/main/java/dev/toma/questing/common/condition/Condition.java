package dev.toma.questing.common.condition;

import dev.toma.questing.common.party.Party;
import dev.toma.questing.common.quest.Quest;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

import java.util.Set;
import java.util.UUID;

public interface Condition {

    ConditionType<?> getType();

    Condition copy();

    void registerTriggerResponders(ConditionRegisterHandler registerHandler);

    default void onConditionConstructing(Party party, Quest quest, World world) {}

    static boolean checkIfEntityIsPartyMember(Entity entity, Party party) {
        if (entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            UUID playerId = player.getUUID();
            Set<UUID> members = party.getMembers();
            return members.contains(playerId);
        }
        return false;
    }
}

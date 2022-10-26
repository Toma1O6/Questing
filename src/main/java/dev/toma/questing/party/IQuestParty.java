package dev.toma.questing.party;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;

public interface IQuestParty {

    UUID getPartyID();

    List<UUID> getAllParticipants();

    List<PlayerEntity> getOnlineParticipants(World level);
}

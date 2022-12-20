package dev.toma.questing.common.party;

import com.mojang.serialization.Codec;
import dev.toma.questing.Questing;
import dev.toma.questing.common.data.PlayerDataProvider;
import dev.toma.questing.file.DataFileManager;
import dev.toma.questing.utils.Codecs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public final class PartyManager implements DataFileManager.DataHandler<Map<UUID, QuestParty>> {

    public static final Marker MARKER = MarkerManager.getMarker("PartyManager");
    public static final Codec<Map<UUID, QuestParty>> CODEC = Codec.unboundedMap(Codecs.UUID_STRING, QuestParty.CODEC);

    private final Map<UUID, QuestParty> partyMap;

    public PartyManager(Map<UUID, QuestParty> map) {
        partyMap = map;
    }

    public PartyManager() {
        this(new HashMap<>());
    }

    public void onPlayerLoaded(ServerPlayerEntity player) {
        Player2PartyManager p2p = Questing.PLAYER2PARTY_MANAGER.get();
        UUID playerId = player.getUUID();
        Optional<UUID> partyOccupation = p2p.getPartyOccupation(playerId);
        if (partyOccupation.isPresent()) {
            if (!this.getPartyById(partyOccupation.get()).isPresent()) {
                this.assignDefaultParty(player);
            }
        } else {
            this.assignDefaultParty(player);
        }
    }

    public void partyCreate(QuestParty party) {
        Set<UUID> memberSet = party.getMembers();
        Player2PartyManager p2p = Questing.PLAYER2PARTY_MANAGER.get();
        for (UUID uuid : memberSet) {
            Optional<UUID> partyOccupation = p2p.getPartyOccupation(uuid);
            if (partyOccupation.isPresent()) {
                UUID occupiedPartyId = partyOccupation.get();
                boolean valid = !this.getPartyById(occupiedPartyId).isPresent();
                if (!valid) {
                    Questing.LOGGER.warn(MARKER, "Unable to create new party due to already existing occupation by {} in {}", uuid, occupiedPartyId);
                    return;
                }
            }
        }
        UUID partyId = party.getOwner();
        p2p.registerMembers(partyId, memberSet);
        partyMap.put(partyId, party);
        Questing.LOGGER.debug(MARKER, "Created ");
        requestDataWrite().exceptionally(throwable -> {
            Questing.LOGGER.fatal(MARKER, "Party data write failed", throwable);
            return null;
        });
    }

    public void assignDefaultParty(PlayerEntity player) {
        QuestParty party = QuestParty.create(player);
        partyCreate(party);
        PlayerDataProvider.getOptional(player).ifPresent(data -> data.getPartyData().setActiveParty(party));
    }

    public Optional<QuestParty> getPartyById(UUID partyId) {
        return Optional.ofNullable(this.partyMap.get(partyId));
    }

    public CompletableFuture<?> requestDataWrite() {
        return Questing.PARTY_MANAGER.writeAsync();
    }

    @Override
    public void loadData(Map<UUID, QuestParty> data) {
        partyMap.clear();
        if (data != null) {
            partyMap.putAll(data);
        }
    }

    @Override
    public Map<UUID, QuestParty> getSaveData() {
        return partyMap;
    }
}

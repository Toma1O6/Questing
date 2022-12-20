package dev.toma.questing.common.party;

import com.mojang.serialization.Codec;
import dev.toma.questing.Questing;
import dev.toma.questing.file.DataFileManager;
import dev.toma.questing.utils.Codecs;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public final class Player2PartyManager implements DataFileManager.DataHandler<Map<UUID, UUID>> {

    public static final Codec<Map<UUID, UUID>> CODEC = Codec.unboundedMap(Codecs.UUID_STRING, Codecs.UUID_STRING);
    private final Map<UUID, UUID> player2party;

    public Player2PartyManager(Map<UUID, UUID> player2party) {
        this.player2party = player2party;
    }

    public Player2PartyManager() {
        this(new HashMap<>());
    }

    public Optional<UUID> getPartyOccupation(UUID playerId) {
        return Optional.ofNullable(this.player2party.get(playerId));
    }

    public void registerMembers(UUID party, Set<UUID> members) {
        members.forEach(member -> player2party.put(member, party));
        this.requestDataSave().exceptionally(throwable -> {
            Questing.LOGGER.fatal(PartyManager.MARKER, "Player to party data write failed", throwable);
            return null;
        });
    }

    private CompletableFuture<?> requestDataSave() {
        return Questing.PLAYER2PARTY_MANAGER.writeAsync();
    }

    @Override
    public void loadData(Map<UUID, UUID> data) {
        player2party.clear();
        if (data != null) {
            player2party.putAll(data);
        }
    }

    @Override
    public Map<UUID, UUID> getSaveData() {
        return player2party;
    }
}

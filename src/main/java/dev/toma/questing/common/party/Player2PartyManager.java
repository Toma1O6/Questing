package dev.toma.questing.common.party;

import com.mojang.serialization.Codec;
import dev.toma.questing.Questing;
import net.minecraft.util.UUIDCodec;

import java.util.*;
import java.util.concurrent.Future;

public final class Player2PartyManager {

    public static final Codec<Player2PartyManager> CODEC = Codec.unboundedMap(UUIDCodec.CODEC, UUIDCodec.CODEC)
            .xmap(Player2PartyManager::new, t -> t.player2party);
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
        this.requestDataSave();
    }

    private Future<?> requestDataSave() {
        return Questing.PLAYER2PARTY_MANAGER.writeAsync();
    }
}

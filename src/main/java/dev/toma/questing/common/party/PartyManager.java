package dev.toma.questing.common.party;

import com.mojang.serialization.Codec;
import dev.toma.questing.Questing;
import net.minecraft.util.UUIDCodec;

import java.util.*;
import java.util.concurrent.Future;
import java.util.function.Supplier;

public final class PartyManager {

    public static final Codec<PartyManager> CODEC = Codec.unboundedMap(UUIDCodec.CODEC, QuestParty.CODEC)
            .xmap(PartyManager::new, mng -> mng.partyMap);

    private final Map<UUID, QuestParty> partyMap;
    private final Supplier<Player2PartyManager> playerReferenceProvider;

    public PartyManager(Map<UUID, QuestParty> map) {
        partyMap = map;
        playerReferenceProvider = Questing.PLAYER2PARTY_MANAGER;
    }

    public PartyManager() {
        this(new HashMap<>());
    }

    public boolean partyCreate(QuestParty party) {
        Set<UUID> memberSet = party.getMembers();
        Player2PartyManager p2p = playerReferenceProvider.get();
        for (UUID uuid : memberSet) {
            Optional<UUID> partyOccupation = p2p.getPartyOccupation(uuid);
            if (partyOccupation.isPresent()) {
                UUID occupiedPartyId = partyOccupation.get();
                boolean valid = !this.getPartyById(occupiedPartyId).isPresent();
                if (!valid) {
                    return false;
                }
            }
        }
        UUID partyId = party.getOwner();
        p2p.registerMembers(partyId, memberSet);
        partyMap.put(partyId, party);
        requestDataWrite();
        return true;
    }

    public Optional<QuestParty> getPartyById(UUID partyId) {
        return Optional.ofNullable(this.partyMap.get(partyId));
    }

    public Future<?> requestDataWrite() {
        return Questing.PARTY_MANAGER.writeAsync();
    }
}

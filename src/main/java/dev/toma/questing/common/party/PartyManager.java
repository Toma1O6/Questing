package dev.toma.questing.common.party;

import com.mojang.serialization.Codec;
import dev.toma.questing.Questing;
import dev.toma.questing.common.data.PartyData;
import dev.toma.questing.common.data.PlayerDataProvider;
import dev.toma.questing.file.DataFileManager;
import dev.toma.questing.network.Networking;
import dev.toma.questing.network.packet.s2c.S2C_SynchronizePartyData;
import dev.toma.questing.utils.Codecs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.world.World;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class PartyManager implements DataFileManager.DataHandler<Map<UUID, Party>> {

    public static final Marker MARKER = MarkerManager.getMarker("PartyManager");
    public static final Codec<Map<UUID, Party>> CODEC = Codec.unboundedMap(Codecs.UUID_STRING, Party.CODEC);

    private final Map<UUID, Party> partyMap;

    public PartyManager(Map<UUID, Party> map) {
        partyMap = map;
    }

    public PartyManager() {
        this(new HashMap<>());
    }

    public void onPlayerLoaded(ServerPlayerEntity player) {
        PlayerDataProvider.getOptional(player).ifPresent(data -> {
            PartyData partyData = data.getPartyData();
            UUID partyOccupation = partyData.getPartyId();
            if (!partyOccupation.equals(Util.NIL_UUID)) {
                if (!this.getPartyById(partyOccupation).isPresent()) {
                    this.assignDefaultParty(player);
                }
            } else {
                this.assignDefaultParty(player);
            }
            this.getPartyById(partyData.getPartyId()).ifPresent(party -> Networking.toClient(player, new S2C_SynchronizePartyData(party)));
        });
    }

    public void partyRegister(Party party) {
        UUID partyId = party.getOwner();
        partyMap.put(partyId, party);
        Questing.LOGGER.debug(MARKER, "Created ");
        requestDataWrite().exceptionally(throwable -> {
            Questing.LOGGER.fatal(MARKER, "Party data write failed", throwable);
            return null;
        });
    }

    public void assignDefaultParty(PlayerEntity player) {
        Party party = Party.create(player);
        partyRegister(party);
        PlayerDataProvider.getOptional(player).ifPresent(data -> data.getPartyData().setActiveParty(party));
    }

    public Optional<Party> getPartyById(UUID partyId) {
        return Optional.ofNullable(this.partyMap.get(partyId));
    }

    public CompletableFuture<?> requestDataWrite() {
        return Questing.PARTY_MANAGER.writeAsync();
    }

    @Override
    public void loadData(Map<UUID, Party> data) {
        partyMap.clear();
        if (data != null) {
            partyMap.putAll(data);
        }
    }

    @Override
    public Map<UUID, Party> getSaveData() {
        return partyMap;
    }

    public void set(Party party) {
        this.partyMap.put(party.getOwner(), party);
    }

    public void sendClientData(World world, Party party) {
        if (world.isClientSide)
            return;
        S2C_SynchronizePartyData packet = new S2C_SynchronizePartyData(party);
        party.forEachOnlineMemberExcept(null, world, player -> Networking.toClient((ServerPlayerEntity) player, packet));
    }
}

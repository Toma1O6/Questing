package dev.toma.questing.network.packet.c2s;

import dev.toma.questing.Questing;
import dev.toma.questing.common.data.PartyData;
import dev.toma.questing.common.data.PlayerDataProvider;
import dev.toma.questing.common.party.Party;
import dev.toma.questing.common.party.PartyManager;
import dev.toma.questing.network.Networking;
import dev.toma.questing.network.packet.AbstractPacket;
import dev.toma.questing.utils.PlayerLookup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import sun.net.www.content.text.plain;

import java.util.Optional;
import java.util.UUID;

public class C2S_RequestInviteCreation extends AbstractPacket<C2S_RequestInviteCreation> {

    private final UUID invite;

    public C2S_RequestInviteCreation(UUID invite) {
        this.invite = invite;
    }

    public C2S_RequestInviteCreation(PacketBuffer buffer) {
        this(buffer.readUUID());
    }

    @Override
    public void encode(PacketBuffer buffer) {
        buffer.writeUUID(invite);
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        ServerPlayerEntity packetSrc = context.getSender();
        PlayerDataProvider.getOptional(packetSrc).ifPresent(data -> {
            PartyData partyData = data.getPartyData();
            UUID partyId = partyData.getPartyId();
            PartyManager manager = Questing.PARTY_MANAGER.get();
            Optional<Party> partyOptional = manager.getPartyById(partyId);
            partyOptional.ifPresent(party -> {
                ServerPlayerEntity invitee = PlayerLookup.findServerPlayer(packetSrc.getLevel(), invite);
                if (invitee == null) {
                    Questing.LOGGER.warn(Networking.MARKER, "{} cannot invite player {}, no player found", packetSrc, invite);
                    return;
                }
                party.invite(packetSrc, invitee);
            });
        });
    }
}

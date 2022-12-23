package dev.toma.questing.network.packet.c2s;

import dev.toma.questing.Questing;
import dev.toma.questing.common.data.PartyData;
import dev.toma.questing.common.data.PlayerDataProvider;
import dev.toma.questing.common.party.PartyInvite;
import dev.toma.questing.common.party.PartyManager;
import dev.toma.questing.network.Networking;
import dev.toma.questing.network.packet.AbstractPacket;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Optional;
import java.util.UUID;

public class C2S_RequestInviteDelete extends AbstractPacket<C2S_RequestInviteDelete> {

    private final UUID invitedMember;

    public C2S_RequestInviteDelete(UUID invitedMember) {
        this.invitedMember = invitedMember;
    }

    public C2S_RequestInviteDelete(PacketBuffer buffer) {
        this.invitedMember = buffer.readUUID();
    }

    @Override
    public void encode(PacketBuffer buffer) {
        buffer.writeUUID(invitedMember);
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        ServerPlayerEntity player = context.getSender();
        PlayerDataProvider.getOptional(player).ifPresent(data -> {
            PartyManager manager = Questing.PARTY_MANAGER.get();
            PartyData partyData = data.getPartyData();
            manager.getPartyById(partyData.getPartyId()).ifPresent(party -> {
                Questing.LOGGER.debug(Networking.MARKER, "Processing invite cancel request by {} in {}. Invitee {}", player, party, invitedMember);
                Optional<PartyInvite> inviteOptional = party.findActiveInviteFor(invitedMember);
                inviteOptional.ifPresent(invite -> {
                    party.cancelInvite(player, invite);
                    manager.sendClientData(player.level, party);
                });
            });
        });
    }
}

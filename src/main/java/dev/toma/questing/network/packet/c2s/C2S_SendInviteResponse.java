package dev.toma.questing.network.packet.c2s;

import dev.toma.questing.Questing;
import dev.toma.questing.common.data.PartyData;
import dev.toma.questing.common.data.PlayerDataProvider;
import dev.toma.questing.common.data.PlayerDataSynchronizationFlags;
import dev.toma.questing.common.party.Party;
import dev.toma.questing.common.party.PartyInvite;
import dev.toma.questing.common.party.PartyManager;
import dev.toma.questing.network.Networking;
import dev.toma.questing.network.packet.AbstractPacket;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Optional;
import java.util.UUID;

public class C2S_SendInviteResponse extends AbstractPacket<C2S_SendInviteResponse> {

    private final boolean accepted;
    private final UUID partyId;

    public C2S_SendInviteResponse(boolean accepted, UUID partyId) {
        this.accepted = accepted;
        this.partyId = partyId;
    }

    public C2S_SendInviteResponse(PacketBuffer buffer) {
        this(buffer.readBoolean(), buffer.readUUID());
    }

    @Override
    public void encode(PacketBuffer buffer) {
        buffer.writeBoolean(accepted);
        buffer.writeUUID(partyId);
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        ServerPlayerEntity player = context.getSender();
        // Delete the invite from player's messages
        PlayerDataProvider.getOptional(player).ifPresent(playerData -> {
            Questing.LOGGER.debug(Networking.MARKER, "Removing expired invite in player data for {}", player);
            PartyData partyData = playerData.getPartyData();
            partyData.removeInvite(PartyInvite.dummy(partyId, player));
            playerData.sendDataToClient(PlayerDataSynchronizationFlags.PARTY);
        });
        // Process the invite
        PartyManager manager = Questing.PARTY_MANAGER.get();
        Optional<Party> optional = manager.getPartyById(partyId);
        optional.ifPresent(party -> {
            Questing.LOGGER.debug(Networking.MARKER, "Processing invite response in {} by {}. Invite accept response: {}", party, player, accepted);
            Optional<PartyInvite> invite = party.findActiveInviteFor(player.getUUID());
            invite.ifPresent(activeInvite -> {
                if (!activeInvite.getInviteeId().equals(player.getUUID())) {
                    Questing.LOGGER.warn(Networking.MARKER, "{} attempted to process invite for other member, aborting", player);
                    return;
                }
                Questing.LOGGER.debug(Networking.MARKER, "Processing {} response in {}. Accepted: {}", activeInvite, party, accepted);
                if (accepted) {
                    activeInvite.acceptInvite(player.level);
                } else {
                    activeInvite.declineInvite(player.level);
                }
            });
        });
    }
}

package dev.toma.questing.network.packet.c2s;

import dev.toma.questing.Questing;
import dev.toma.questing.common.data.PartyData;
import dev.toma.questing.common.data.PlayerDataProvider;
import dev.toma.questing.common.data.PlayerDataSynchronizationFlags;
import dev.toma.questing.common.party.Party;
import dev.toma.questing.common.party.PartyInvite;
import dev.toma.questing.common.party.PartyManager;
import dev.toma.questing.network.packet.AbstractActionPacket;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class C2S_CheckMyInvites extends AbstractActionPacket<C2S_CheckMyInvites> {

    @Override
    public void handle(NetworkEvent.Context context) {
        ServerPlayerEntity player = context.getSender();
        PlayerDataProvider.getOptional(player).ifPresent(data -> {
            PartyManager manager = Questing.PARTY_MANAGER.get();
            PartyData partyData = data.getPartyData();
            UUID playerId = player.getUUID();
            Set<PartyInvite> invites = partyData.getMyInvites();
            boolean resyncRequired = false;
            Iterator<PartyInvite> it = invites.iterator();
            while (it.hasNext()) {
                PartyInvite invite = it.next();
                UUID partyId = invite.getPartyId();
                Party party = manager.getPartyById(partyId).orElse(null);
                if (party == null) {
                    it.remove();
                    resyncRequired = true;
                    continue;
                }
                UUID inviteeId = invite.getInviteeId();
                if (!inviteeId.equals(playerId)) {
                    it.remove();
                    resyncRequired = true;
                    continue;
                }
                Optional<PartyInvite> partyInviteOptional = party.findActiveInviteFor(playerId);
                if (!partyInviteOptional.isPresent()) {
                    it.remove();
                    resyncRequired = true;
                }
            }
            if (resyncRequired) {
                data.sendDataToClient(PlayerDataSynchronizationFlags.PARTY);
            }
        });
    }
}

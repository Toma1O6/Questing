package dev.toma.questing.network.packet.c2s;

import dev.toma.questing.Questing;
import dev.toma.questing.common.data.PartyData;
import dev.toma.questing.common.data.PlayerDataProvider;
import dev.toma.questing.common.data.PlayerDataSynchronizationFlags;
import dev.toma.questing.common.notification.Notification;
import dev.toma.questing.common.notification.NotificationFactory;
import dev.toma.questing.common.notification.NotificationsHelper;
import dev.toma.questing.common.party.PartyManager;
import dev.toma.questing.network.Networking;
import dev.toma.questing.network.packet.AbstractActionPacket;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Set;
import java.util.UUID;

public class C2S_LeaveParty extends AbstractActionPacket<C2S_LeaveParty> {

    @Override
    public void handle(NetworkEvent.Context context) {
        ServerPlayerEntity player = context.getSender();
        PlayerDataProvider.getOptional(player).ifPresent(data -> {
            PartyData partyData = data.getPartyData();
            PartyManager manager = Questing.PARTY_MANAGER.get();
            manager.getPartyById(partyData.getPartyId()).ifPresent(party -> {
                // If player is in "empty" party, simply ignore
                Set<UUID> members = party.getMembers();
                if (members.size() == 1 && members.contains(player.getUUID())) {
                    Questing.LOGGER.warn(Networking.MARKER, "{} tried to abandon default party {}, ignoring request", player, party);
                    return;
                }
                UUID uuid = player.getUUID();
                UUID owner = party.getOwner();
                // If player is owner, whole party needs to be disbanded
                if (uuid.equals(owner)) {
                    party.disband(player);
                } else { // otherwise member can leave normally
                    party.removeMember(player, uuid);
                    manager.sendClientData(player.level, party);
                    Notification notification = NotificationFactory.getMemberLeftNotification(player.getUUID(), player.getName().getString());
                    party.forEachOnlineMemberExcept(player.getUUID(), player.level, pl -> NotificationsHelper.sendNotification(pl, notification));
                }
                manager.assignDefaultParty(player);
                manager.getPartyById(partyData.getPartyId()).ifPresent(newParty -> manager.sendClientData(player.level, newParty));
            });
            data.sendDataToClient(PlayerDataSynchronizationFlags.PARTY);
        });
    }
}

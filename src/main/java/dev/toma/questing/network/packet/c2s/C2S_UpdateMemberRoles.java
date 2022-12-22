package dev.toma.questing.network.packet.c2s;

import dev.toma.questing.Questing;
import dev.toma.questing.common.data.PartyData;
import dev.toma.questing.common.data.PlayerDataProvider;
import dev.toma.questing.common.party.PartyManager;
import dev.toma.questing.common.party.PartyPermission;
import dev.toma.questing.network.packet.AbstractPacket;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;

public class C2S_UpdateMemberRoles extends AbstractPacket<C2S_UpdateMemberRoles> {

    private final UUID member;
    private final Set<PartyPermission> roles;

    public C2S_UpdateMemberRoles(UUID member, Set<PartyPermission> roles) {
        this.member = member;
        this.roles = roles;
    }

    public C2S_UpdateMemberRoles(PacketBuffer buffer) {
        this.member = buffer.readUUID();
        int count = buffer.readInt();
        Set<PartyPermission> set = EnumSet.noneOf(PartyPermission.class);
        for (int i = 0; i < count; i++) {
            set.add(buffer.readEnum(PartyPermission.class));
        }
        this.roles = set;
    }

    @Override
    public void encode(PacketBuffer buffer) {
        buffer.writeUUID(member);
        buffer.writeInt(roles.size());
        roles.forEach(buffer::writeEnum);
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        ServerPlayerEntity player = context.getSender();
        PlayerDataProvider.getOptional(player).ifPresent(data -> {
            PartyManager manager = Questing.PARTY_MANAGER.get();
            PartyData partyData = data.getPartyData();
            manager.getPartyById(partyData.getPartyId()).ifPresent(party -> {
                if (!party.isMember(member)) {
                    return;
                }
                boolean hasUserMngRole = party.isAuthorized(PartyPermission.MANAGE_MEMBERS, member);
                if (hasUserMngRole && !player.getUUID().equals(party.getOwner())) {
                    return;
                }
                party.executeWithAuthorization(PartyPermission.MANAGE_MEMBERS, player.getUUID(), () -> {
                    party.readjustRoles(member, roles);
                    manager.sendClientData(player.level, party);
                });
            });
        });
    }
}

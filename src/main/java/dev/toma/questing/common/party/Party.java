package dev.toma.questing.common.party;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.toma.questing.Questing;
import dev.toma.questing.common.data.PartyData;
import dev.toma.questing.common.data.PlayerDataProvider;
import dev.toma.questing.common.data.PlayerDataSynchronizationFlags;
import dev.toma.questing.network.Networking;
import dev.toma.questing.network.packet.s2c.S2C_SynchronizePartyData;
import dev.toma.questing.utils.Codecs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.World;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public final class Party {

    public static final Marker MARKER = MarkerManager.getMarker("Parties");
    public static final Codec<Party> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codecs.UUID_STRING.fieldOf("owner").forGetter(p -> p.owner),
            Codecs.UUID_STRING.listOf().xmap(LinkedHashSet::new, ArrayList::new).fieldOf("members").forGetter(p -> p.members),
            Codec.unboundedMap(Codecs.UUID_STRING, Codec.STRING).fieldOf("usernames").forGetter(p -> p.usernameCache),
            PartyInvite.CODEC.listOf().xmap(HashSet::new, ArrayList::new).fieldOf("invites").forGetter(p -> p.activeInvites),
            Codec.STRING.fieldOf("name").forGetter(p -> p.partyName),
            Codec.unboundedMap(Codecs.UUID_STRING, Codec.INT).fieldOf("permissions").forGetter(p -> p.permissionMap)
    ).apply(instance, Party::new));
    private final UUID owner;
    private final LinkedHashSet<UUID> members = new LinkedHashSet<>();
    private final Map<UUID, String> usernameCache = new HashMap<>();
    private final HashSet<PartyInvite> activeInvites = new HashSet<>();
    private final Map<UUID, Integer> permissionMap = new HashMap<>();
    private String partyName;

    private Party(UUID owner, Set<UUID> members, Map<UUID, String> usernameCache, Set<PartyInvite> invites, String partyName, Map<UUID, Integer> permissionMap) {
        this.owner = owner;
        this.members.addAll(members);
        this.usernameCache.putAll(usernameCache);
        this.activeInvites.addAll(invites);
        this.activeInvites.forEach(invite -> invite.setResponseHandlers(this::onInviteAccepted, this::onInviteDeclined));
        this.permissionMap.putAll(permissionMap);
        this.partyName = partyName;
    }

    public static Party create(PlayerEntity player) {
        UUID ownerId = player.getUUID();
        String name = player.getName().getString();
        Set<UUID> set = new HashSet<>();
        set.add(ownerId);
        Map<UUID, String> usernames = new HashMap<>();
        usernames.put(ownerId, name);
        String partyName = String.format("%s's party", name);
        Map<UUID, Integer> map = new HashMap<>();
        map.put(ownerId, PartyPermission.OWNER.getAsInt());
        return new Party(ownerId, set, usernames, Collections.emptySet(), partyName, map);
    }

    public boolean canAddNewMember() {
        int size = this.members.size();
        return size < Questing.<Integer>getProperty(Questing.Properties.PARTY_SIZE).orElse(5);
    }

    public void addMember(PlayerEntity member) {
        if (!this.canAddNewMember()) {
            Questing.LOGGER.error(MARKER, "Unable to add new member {} because party is already full", member.getName().getString());
            return;
        }
        UUID uuid = member.getUUID();
        this.members.add(uuid);
        this.usernameCache.put(uuid, member.getName().getString());
        this.permissionMap.put(uuid, PartyPermission.USER.getAsInt());
    }

    public void removeMember(PlayerEntity source, UUID member) {
        PartyPermission permission = source.getUUID().equals(member) ? PartyPermission.USER : PartyPermission.MANAGE_MEMBERS;
        this.executeWithAuthorization(permission, source.getUUID(), () -> {
            if (member.equals(owner)) {
                throw new UnsupportedOperationException("Cannot remove owner of quest party");
            }
            this.members.remove(member);
            this.usernameCache.remove(member);
            this.permissionMap.remove(member);
            Questing.PARTY_MANAGER.get().sendClientData(source.level, this);
        });
    }

    public void disband(PlayerEntity owner) {
        PartyManager manager = Questing.PARTY_MANAGER.get();
        this.forEachOnlineMemberExcept(owner.getUUID(), owner.level, player -> {
            manager.assignDefaultParty(player);
            PlayerDataProvider.getOptional(player).ifPresent(data -> {
                PartyData partyData = data.getPartyData();
                manager.getPartyById(partyData.getPartyId()).ifPresent(party -> manager.sendClientData(player.level, party));
                data.sendDataToClient(PlayerDataSynchronizationFlags.PARTY);
            });
        });
        manager.partyDelete(this);
    }

    public void invite(PlayerEntity sender, PlayerEntity receiver) {
        this.executeWithAuthorization(PartyPermission.INVITE_PLAYERS, sender.getUUID(), () -> {
            PartyInvite invite = PartyInvite.createInvite(receiver, sender);
            invite.setResponseHandlers(this::onInviteAccepted, this::onInviteDeclined);
            activeInvites.add(invite);
            Questing.PARTY_MANAGER.get().sendClientData(sender.level, this);
            PlayerDataProvider.getOptional(receiver).ifPresent(playerData -> {
                PartyData data = playerData.getPartyData();
                data.addInvite(invite);
                playerData.sendDataToClient(PlayerDataSynchronizationFlags.PARTY);
            });
        });
    }

    public UUID getOwner() {
        return owner;
    }

    public Set<UUID> getMembers() {
        return members;
    }

    public String getMemberUsername(UUID member) {
        return this.usernameCache.getOrDefault(member, member.toString());
    }

    public Optional<PlayerEntity> getOwner(World world) {
        return Optional.ofNullable(world.getPlayerByUUID(this.owner));
    }

    public void executeWithAuthorization(PartyPermission type, UUID id, Runnable onSuccess) {
        if (this.isAuthorized(type, id)) {
            onSuccess.run();
        } else {
            Questing.LOGGER.warn(MARKER, "Authorization failed: {} is not allowed to execute {} actions", id, type);
        }
    }

    public boolean isAuthorized(PartyPermission type, UUID uuid) {
        Set<PartyPermission> permissions = this.getMemberProfiles(uuid);
        int requiredPermLevel = type.getPermissionLevel() + 1;
        for (PartyPermission permission : permissions) {
            if (type == permission || permission.getPermissionLevel() >= requiredPermLevel) {
                return true;
            }
        }
        return false;
    }

    public boolean hasAnyProfile(UUID uuid, PartyPermission... profiles) {
        boolean found = false;
        for (PartyPermission profile : profiles) {
            if (this.isAuthorized(profile, uuid)) {
                found = true;
                break;
            }
        }
        return found;
    }

    public Set<PartyPermission> getMemberProfiles(UUID member) {
        int value = this.permissionMap.getOrDefault(member, 0);
        return Arrays.stream(PartyPermission.values())
                .filter(perm -> PartyPermission.is(perm, value))
                .collect(Collectors.toSet());
    }

    public boolean isMember(UUID member) {
        return this.members.contains(member);
    }

    public void readjustRoles(UUID member, Set<PartyPermission> activeRoles) {
        if (isAuthorized(PartyPermission.OWNER, member)) {
            activeRoles.add(PartyPermission.OWNER);
        }
        if (isAuthorized(PartyPermission.USER, member)) {
            activeRoles.add(PartyPermission.USER);
        }
        int value = activeRoles.stream().mapToInt(PartyPermission::getAsInt).reduce(0, (a, b) -> a | b);
        this.permissionMap.put(member, value);
    }

    public List<UUID> getMembersSortedByRoles() {
        return this.members.stream()
                .sorted(Comparator.comparingInt(this::getMemberSortIndexByRoles))
                .collect(Collectors.toList());
    }

    public int getMemberSortIndexByRoles(UUID member) {
        return this.getMemberProfiles(member).stream()
                .min(Comparator.comparingInt(Enum::ordinal))
                .map(Enum::ordinal)
                .orElse(Integer.MAX_VALUE);
    }

    public String getName() {
        return partyName;
    }

    public void forEachOnlineMemberExcept(@Nullable UUID exception, World world, Consumer<PlayerEntity> action) {
        for (UUID memberId : members) {
            if (Objects.equals(memberId, exception)) {
                continue;
            }
            PlayerEntity player = world.getPlayerByUUID(memberId);
            if (player != null) {
                action.accept(player);
            }
        }
    }

    public void setPartyName(String partyName) {
        this.partyName = partyName;
    }

    public Optional<PartyInvite> findActiveInviteFor(UUID receiver) {
        PartyInvite invite = null;
        for (PartyInvite partyInvite : activeInvites) {
            if (partyInvite.getInviteeId().equals(receiver)) {
                invite = partyInvite;
                break;
            }
        }
        return invite != null ? Optional.of(invite) : Optional.empty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Party party = (Party) o;
        return owner.equals(party.owner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(owner);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Party[id=").append(owner).append(", name=").append(partyName).append(", members=[");
        Iterator<UUID> iterator = members.iterator();
        while (iterator.hasNext()) {
            UUID member = iterator.next();
            String name = getMemberUsername(member);
            builder.append(name);
            if (iterator.hasNext()) {
                builder.append(",");
            }
        }
        builder.append("]]");
        return builder.toString();
    }

    private void onInviteAccepted(PartyInvite invite, PlayerEntity invited) {
        PlayerDataProvider.getOptional(invited).ifPresent(data -> {
            if (!this.canAddNewMember())
                return;
            PartyData partyData = data.getPartyData();
            UUID originalParty = partyData.getPartyId();
            PartyManager manager = Questing.PARTY_MANAGER.get();
            Optional<Party> oldParty = manager.getPartyById(originalParty);
            // Handle old party
            oldParty.ifPresent(party -> {
                // If user was owner of the party, it needs to be disbanded
                if (party.getOwner().equals(invited.getUUID())) {
                    party.disband(invited);
                } else { // Otherwise just delete the member from party
                    party.removeMember(invited, invite.getInviteeId());
                }
            });
            this.addMember(invited);
            partyData.setActiveParty(this);
            data.sendDataToClient(PlayerDataSynchronizationFlags.PARTY);
            this.activeInvites.remove(invite);
            if (!this.canAddNewMember()) {
                // Cancel pending invites on client side when no more members can be added
                this.activeInvites.forEach(activeInvite -> {
                    partyData.removeInvite(activeInvite);
                    data.sendDataToClient(PlayerDataSynchronizationFlags.PARTY);
                });
                // Remove all party stored invites
                this.activeInvites.clear();
            }
            // synchronize data
            if (!invited.level.isClientSide) {
                S2C_SynchronizePartyData packet = new S2C_SynchronizePartyData(this);
                this.forEachOnlineMemberExcept(null, invited.level, player -> {
                    ServerPlayerEntity member = (ServerPlayerEntity) player;
                    Networking.toClient(member, packet);
                });
            }
        });
    }

    private void onInviteDeclined(PartyInvite invite, PlayerEntity invited) {
        this.activeInvites.remove(invite);
        if (!invited.level.isClientSide) {
            S2C_SynchronizePartyData packet = new S2C_SynchronizePartyData(this);
            this.forEachOnlineMemberExcept(null, invited.level, player -> {
                ServerPlayerEntity member = (ServerPlayerEntity) player;
                Networking.toClient(member, packet);
            });
        }
    }
}

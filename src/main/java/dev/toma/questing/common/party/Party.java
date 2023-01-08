package dev.toma.questing.common.party;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.toma.questing.Questing;
import dev.toma.questing.common.data.PartyData;
import dev.toma.questing.common.data.PlayerDataProvider;
import dev.toma.questing.common.data.PlayerDataSynchronizationFlags;
import dev.toma.questing.common.notification.NotificationFactory;
import dev.toma.questing.common.notification.NotificationsHelper;
import dev.toma.questing.network.Networking;
import dev.toma.questing.network.packet.s2c.S2C_SynchronizePartyData;
import dev.toma.questing.utils.Codecs;
import dev.toma.questing.utils.PlayerLookup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
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
    public static final Codec<Party> REF_CODEC = Codecs.UUID_STRING.comapFlatMap(partyId -> {
        PartyManager manager = Questing.PARTY_MANAGER.get();
        Optional<Party> optional = manager.getPartyById(partyId);
        return optional.map(DataResult::success).orElseGet(() -> DataResult.error("Party not found by ID"));
    }, Party::getOwner);
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

    public static boolean isAnyMemberOnline(Party party, World level) {
        if (level.isClientSide)
            return false;
        Set<UUID> members = party.getMembers();
        for (UUID uuid : members) {
            if (PlayerLookup.findServerPlayer((ServerWorld) level, uuid) != null) {
                return true;
            }
        }
        return false;
    }

    public boolean canAddNewMember() {
        int size = this.members.size();
        return size < Questing.config.maxPartySize;
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
        Questing.LOGGER.debug(MARKER, "Added new member {} to {}", member, this);
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
            Questing.LOGGER.debug(MARKER, "Removed member ID {} from party {}", member, this);
        });
    }

    public void disband(PlayerEntity owner) {
        Questing.LOGGER.debug(MARKER, "Disbanding {}...", this);
        PartyManager manager = Questing.PARTY_MANAGER.get();
        this.forEachOnlineMemberExcept(owner.getUUID(), owner.level, player -> {
            manager.assignDefaultParty(player);
            PlayerDataProvider.getOptional(player).ifPresent(data -> {
                PartyData partyData = data.getPartyData();
                manager.getPartyById(partyData.getPartyId()).ifPresent(party -> manager.sendClientData(player.level, party));
                data.sendDataToClient(PlayerDataSynchronizationFlags.PARTY);
            });
            NotificationsHelper.sendNotification(player, NotificationFactory.getPartyDisbandedNotification(partyName));
        });
        manager.partyDelete(this);
    }

    public void invite(PlayerEntity sender, PlayerEntity receiver) {
        Questing.LOGGER.debug(MARKER, "Creating new invite for {} in {}...", receiver, this);
        this.executeWithAuthorization(PartyPermission.INVITE_PLAYERS, sender.getUUID(), () -> {
            PartyInvite invite = PartyInvite.createInvite(receiver, sender);
            invite.setResponseHandlers(this::onInviteAccepted, this::onInviteDeclined);
            activeInvites.add(invite);
            Questing.LOGGER.debug(MARKER, "Invite created {}, sending to client", invite);
            Questing.PARTY_MANAGER.get().sendClientData(sender.level, this);
            PlayerDataProvider.getOptional(receiver).ifPresent(playerData -> {
                PartyData data = playerData.getPartyData();
                data.addInvite(invite);
                playerData.sendDataToClient(PlayerDataSynchronizationFlags.PARTY);
                NotificationsHelper.sendNotification(receiver, NotificationFactory.getInviteNotification(invite));
            });
            this.forEachOnlineMemberExcept(null, sender.level, player -> NotificationsHelper.sendNotification(player, NotificationFactory.getInviteSentNotification(invite)));
        });
    }

    public void cancelInvite(ServerPlayerEntity sender, PartyInvite invite) {
        Questing.LOGGER.debug(MARKER, "Cancelling invite {} in {}", invite, this);
        this.executeWithAuthorization(PartyPermission.MANAGE_INVITES, sender.getUUID(), () -> {
            if (this.activeInvites.contains(invite)) {
                this.activeInvites.remove(invite);
                Questing.LOGGER.debug(MARKER, "Cancelled invite {}, sending to client", invite);
                ServerPlayerEntity invitee = PlayerLookup.findServerPlayer(sender.getLevel(), invite.getInviteeId());
                if (invitee != null) {
                    PlayerDataProvider.getOptional(invitee).ifPresent(data -> {
                        PartyData partyData = data.getPartyData();
                        partyData.removeInvite(invite);
                        data.sendDataToClient(PlayerDataSynchronizationFlags.PARTY);
                    });
                    NotificationsHelper.sendNotification(invitee, NotificationFactory.getInviteCancelledNotification(invite));
                }
            } else {
                Questing.LOGGER.warn(MARKER, "Attempted to cancel non-existent invite. Request origin: {}, invite: {}", sender, invite);
            }
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

    public Optional<ServerPlayerEntity> getOwner(ServerWorld world) {
        return Optional.ofNullable(PlayerLookup.findServerPlayer(world, owner));
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
        Questing.LOGGER.debug(MARKER, "Readjusting member {} roles in {}", member, this);
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
        if (world.isClientSide)
            return;
        for (UUID memberId : members) {
            if (Objects.equals(memberId, exception)) {
                continue;
            }
            PlayerEntity player = PlayerLookup.findServerPlayer((ServerWorld) world, memberId);
            if (player != null) {
                action.accept(player);
            }
        }
    }

    public List<PlayerEntity> getOnlineMembers(World level, @Nullable UUID exceptFor) {
        if (level.isClientSide)
            return Collections.emptyList();
        List<PlayerEntity> list = new ArrayList<>();
        for (UUID memberId : members) {
            if (Objects.equals(memberId, exceptFor)) {
                continue;
            }
            PlayerEntity player = PlayerLookup.findServerPlayer((ServerWorld) level, memberId);
            if (player != null) {
                list.add(player);
            }
        }
        return list;
    }

    public void setPartyName(String partyName) {
        Questing.LOGGER.debug(MARKER, "Updating party {} name to {}", this, partyName);
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

    public List<PartyInvite> getActiveInvites() {
        return this.activeInvites.stream()
                .sorted(Comparator.comparing(PartyInvite::getInvitedName))
                .collect(Collectors.toList());
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
            if (!this.canAddNewMember()) {
                Questing.LOGGER.warn(MARKER, "Unable to complete invite {} in {}, party is full", invite, this);
                return;
            }
            forEachOnlineMemberExcept(null, invited.level, player -> NotificationsHelper.sendNotification(player, NotificationFactory.getInviteResponseNotification(invite, true)));
            PartyData partyData = data.getPartyData();
            UUID originalParty = partyData.getPartyId();
            PartyManager manager = Questing.PARTY_MANAGER.get();
            Optional<Party> oldParty = manager.getPartyById(originalParty);
            // Handle old party
            oldParty.ifPresent(party -> {
                Questing.LOGGER.debug(MARKER, "Removing {} from old party", invited);
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
            Questing.LOGGER.debug(MARKER, "Deleted used invite {} in {}", invite, this);
            if (!this.canAddNewMember()) {
                Questing.LOGGER.debug(MARKER, "{} is now full, cancelling all pending invites", this);
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
                Questing.LOGGER.debug(MARKER, "Sending client data");
                S2C_SynchronizePartyData packet = new S2C_SynchronizePartyData(this);
                this.forEachOnlineMemberExcept(null, invited.level, player -> {
                    ServerPlayerEntity member = (ServerPlayerEntity) player;
                    Networking.toClient(member, packet);
                });
            }
            Questing.LOGGER.debug(MARKER, "Invite acceptance process completed in {} for {}", this, invite);
        });
    }

    private void onInviteDeclined(PartyInvite invite, PlayerEntity invited) {
        this.activeInvites.remove(invite);
        Questing.LOGGER.debug(MARKER, "Removing declined invite {} in {}", invite, this);
        if (!invited.level.isClientSide) {
            forEachOnlineMemberExcept(null, invited.level, player -> NotificationsHelper.sendNotification(player, NotificationFactory.getInviteResponseNotification(invite, false)));
            S2C_SynchronizePartyData packet = new S2C_SynchronizePartyData(this);
            this.forEachOnlineMemberExcept(null, invited.level, player -> {
                ServerPlayerEntity member = (ServerPlayerEntity) player;
                Networking.toClient(member, packet);
            });
        }
    }
}

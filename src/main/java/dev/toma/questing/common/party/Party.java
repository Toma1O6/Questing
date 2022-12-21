package dev.toma.questing.common.party;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.toma.questing.Questing;
import dev.toma.questing.utils.Codecs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;

public final class Party {

    public static final Marker MARKER = MarkerManager.getMarker("Parties");
    public static final Codec<Party> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codecs.UUID_STRING.fieldOf("owner").forGetter(p -> p.owner),
            Codecs.UUID_STRING.listOf().xmap(LinkedHashSet::new, ArrayList::new).fieldOf("members").forGetter(p -> p.members),
            Codec.unboundedMap(Codecs.UUID_STRING, Codec.STRING).fieldOf("usernames").forGetter(p -> p.usernameCache),
            PartyInvite.CODEC.listOf().xmap(HashSet::new, ArrayList::new).fieldOf("invites").forGetter(p -> p.activeInvites),
            Codec.STRING.fieldOf("name").forGetter(p -> p.partyName)
    ).apply(instance, Party::new));
    public static final int MAX_PARTY_SIZE = 16; // TODO config option
    private final UUID owner;
    private final LinkedHashSet<UUID> members = new LinkedHashSet<>();
    private final Map<UUID, String> usernameCache = new HashMap<>();
    private final HashSet<PartyInvite> activeInvites = new HashSet<>();
    private String partyName;

    private Party(UUID owner, Set<UUID> members, Map<UUID, String> usernameCache, Set<PartyInvite> invites, String partyName) {
        this.owner = owner;
        this.members.addAll(members);
        this.usernameCache.putAll(usernameCache);
        this.activeInvites.addAll(invites);
        this.activeInvites.forEach(invite -> invite.setResponseHandlers(this::onInviteAccepted, this::onInviteDeclined));
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
        return new Party(ownerId, set, usernames, Collections.emptySet(), partyName);
    }

    public boolean canAddNewMember() {
        int size = this.members.size();
        return size < MAX_PARTY_SIZE;
    }

    public void addMember(PlayerEntity member) {
        if (!this.canAddNewMember()) {
            Questing.LOGGER.error(MARKER, "Unable to add new member {} because party is already full", member.getName().getString());
            return;
        }
        UUID uuid = member.getUUID();
        this.members.add(uuid);
        this.usernameCache.put(uuid, member.getName().getString());
    }

    public void removeMember(UUID member) {
        if (member.equals(owner)) {
            throw new UnsupportedOperationException("Cannot remove owner of quest party");
        }
        this.members.remove(member);
        this.usernameCache.remove(member);
    }

    public void invite(PlayerEntity sender, PlayerEntity receiver) {
        PartyInvite invite = PartyInvite.createInvite(receiver, sender);
        activeInvites.add(invite);
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
        // TODO synchronize
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
        if (this.canAddNewMember()) {
            this.addMember(invited);
            // TODO synchronize data
        }
        this.activeInvites.remove(invite);
        if (!this.canAddNewMember()) {
            // TODO cancel all pending invites
            this.activeInvites.clear();
        }
    }

    private void onInviteDeclined(PartyInvite invite, PlayerEntity invited) {
        this.activeInvites.remove(invite);
    }
}

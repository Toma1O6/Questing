package dev.toma.questing.common.party;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.toma.questing.Questing;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.UUIDCodec;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;

public final class QuestParty {

    public static final Codec<QuestParty> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            UUIDCodec.CODEC.fieldOf("owner").forGetter(p -> p.owner),
            UUIDCodec.CODEC.listOf().xmap(LinkedHashSet::new, ArrayList::new).fieldOf("members").forGetter(p -> p.members),
            Codec.unboundedMap(UUIDCodec.CODEC, Codec.STRING).fieldOf("usernames").forGetter(p -> p.usernameCache)
    ).apply(instance, QuestParty::new));
    public static final int MAX_PARTY_SIZE = 16;
    private final UUID owner;
    private final LinkedHashSet<UUID> members = new LinkedHashSet<>();
    private final Map<UUID, String> usernameCache = new HashMap<>();

    private QuestParty(UUID owner, Set<UUID> members, Map<UUID, String> usernameCache) {
        this.owner = owner;
        this.members.addAll(members);
        this.usernameCache.putAll(usernameCache);
    }

    public static QuestParty create(PlayerEntity player) {
        UUID ownerId = player.getUUID();
        String name = player.getName().getString();
        Set<UUID> set = new HashSet<>();
        set.add(ownerId);
        Map<UUID, String> usernames = new HashMap<>();
        usernames.put(ownerId, name);
        return new QuestParty(ownerId, set, usernames);
    }

    public boolean canAddNewMember() {
        int size = this.members.size();
        return size < MAX_PARTY_SIZE;
    }

    public void addMember(PlayerEntity member) {
        if (!this.canAddNewMember()) {
            Questing.LOGGER.error(Questing.MARKER_PARTIES, "Unable to add new member {} because party is already full", member.getName().getString());
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

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("QuestParty[id=").append(owner).append(", members=[");
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
}

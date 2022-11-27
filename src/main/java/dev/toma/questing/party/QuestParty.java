package dev.toma.questing.party;

import dev.toma.questing.Questing;
import dev.toma.questing.provider.QuestProvider;
import dev.toma.questing.utils.NbtHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.nbt.StringNBT;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;

public final class QuestParty {

    private final QuestProvider.Options options;
    private final UUID owner;
    private final Set<UUID> members = new LinkedHashSet<>();
    private final Map<UUID, String> usernameCache = new HashMap<>();

    public QuestParty(QuestProvider.Options options, PlayerEntity owner) {
        this.options = options;
        this.owner = owner.getUUID();
        this.addMember(owner);
    }

    // For NBT read ops
    public QuestParty(QuestProvider.Options options, CompoundNBT nbt) {
        this.options = options;
        this.owner = nbt.getUUID("owner");
        NbtHelper.readCollection(() -> members, nbt.getList("members", Constants.NBT.TAG_INT_ARRAY), NBTUtil::loadUUID);
        NbtHelper.readMap(() -> usernameCache, nbt.getList("usernamecache", Constants.NBT.TAG_COMPOUND), NBTUtil::loadUUID, INBT::getAsString);
    }

    public boolean canAddNewMember() {
        int size = this.members.size();
        return size < this.options.maxPartyGroupSize();
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

    public CompoundNBT createSaveData() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putUUID("owner", owner);
        nbt.put("members", NbtHelper.saveCollection(members, (list, uuid) -> list.add(NBTUtil.createUUID(uuid))));
        nbt.put("usernamecache", NbtHelper.saveMap(usernameCache, NBTUtil::createUUID, StringNBT::valueOf));
        return nbt;
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
}

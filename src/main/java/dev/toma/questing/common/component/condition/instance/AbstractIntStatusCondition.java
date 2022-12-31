package dev.toma.questing.common.component.condition.instance;

import dev.toma.questing.common.component.condition.provider.AbstractDefaultConditionProvider;
import dev.toma.questing.common.component.trigger.Events;
import dev.toma.questing.common.component.trigger.ResponseType;
import dev.toma.questing.common.party.Party;
import dev.toma.questing.common.quest.ConditionRegisterHandler;
import dev.toma.questing.common.quest.instance.Quest;
import dev.toma.questing.utils.PlayerLookup;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public abstract class AbstractIntStatusCondition implements Condition {

    private final Object2IntMap<UUID> statusMap;

    public AbstractIntStatusCondition() {
        this.statusMap = new Object2IntOpenHashMap<>();
    }

    public AbstractIntStatusCondition(Map<UUID, Integer> map) {
        this();
        this.statusMap.putAll(map);
    }

    public abstract int getValue(PlayerEntity player);

    @Override
    public abstract AbstractDefaultConditionProvider<?> getProvider();

    @Override
    public void onConditionConstructing(Party party, Quest quest, World world) {
        party.forEachOnlineMemberExcept(null, world, player -> {
            UUID playerId = player.getUUID();
            int value = this.getValue(player);
            statusMap.put(playerId, value);
        });
    }

    @Override
    public void registerTriggerResponders(ConditionRegisterHandler registerHandler) {
        registerHandler.register(Events.EVENT, (eventData, level, quest) -> {
            Party party = quest.getParty();
            Set<UUID> members = party.getMembers();
            if (level.isClientSide) {
                return ResponseType.SKIP;
            }
            ServerWorld serverLevel = (ServerWorld) level;
            for (UUID member : members) {
                ServerPlayerEntity player = PlayerLookup.findServerPlayer(serverLevel, member);
                if (player == null)
                    continue;
                boolean shouldFail = this.shouldFail(player);
                if (!shouldFail)
                    continue;
                return this.getProvider().getDefaultFailureResponse();
            }
            return ResponseType.OK;
        });
    }

    public Object2IntMap<UUID> getStatusMap() {
        return statusMap;
    }

    protected boolean isValueValid(int currentPlayerValue, int storedValue) {
        return currentPlayerValue <= storedValue;
    }

    protected boolean shouldFail(PlayerEntity player) {
        UUID playerId = player.getUUID();
        if (!statusMap.containsKey(playerId))
            return true;
        int value = statusMap.getInt(playerId);
        int level = this.getValue(player);
        boolean fail = !this.isValueValid(level, value);
        statusMap.put(playerId, level);
        return fail;
    }
}

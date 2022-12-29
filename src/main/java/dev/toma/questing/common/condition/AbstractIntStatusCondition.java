package dev.toma.questing.common.condition;

import dev.toma.questing.common.party.Party;
import dev.toma.questing.common.quest.Quest;
import dev.toma.questing.common.trigger.Events;
import dev.toma.questing.common.trigger.ResponseType;
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

public abstract class AbstractIntStatusCondition extends AbstractDefaultCondition {

    private final Object2IntMap<UUID> statusMap;

    public AbstractIntStatusCondition(ResponseType defaultResponse) {
        super(defaultResponse);
        this.statusMap = new Object2IntOpenHashMap<>();
    }

    public AbstractIntStatusCondition(ResponseType defaultResponseType, Map<UUID, Integer> map) {
        this(defaultResponseType);
        this.statusMap.putAll(map);
    }

    public abstract int getValue(PlayerEntity player);

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
        registerHandler.register(Events.EVENT, (eventData, quest) -> {
            Party party = quest.getParty();
            Set<UUID> members = party.getMembers();
            if (quest.level.isClientSide) {
                return ResponseType.SKIP;
            }
            ServerWorld serverLevel = (ServerWorld) quest.level;
            for (UUID member : members) {
                ServerPlayerEntity player = PlayerLookup.findServerPlayer(serverLevel, member);
                if (player == null)
                    continue;
                boolean shouldFail = this.shouldFail(player);
                if (!shouldFail)
                    continue;
                return this.getDefaultFailureResponse();
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

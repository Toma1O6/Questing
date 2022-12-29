package dev.toma.questing.common.condition;

import com.mojang.serialization.Codec;
import dev.toma.questing.common.init.QuestingRegistries;
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

import java.util.Set;
import java.util.UUID;

public class NoHealthGainedCondition extends ConditionProvider<NoHealthGainedCondition.Instance> {

    public static final Codec<NoHealthGainedCondition> CODEC = Codec.STRING.comapFlatMap(ResponseType::fromString, Enum::name)
            .optionalFieldOf("onFail", ResponseType.FAIL).codec()
            .xmap(NoHealthGainedCondition::new, ConditionProvider::getDefaultFailureResponse);

    public NoHealthGainedCondition(ResponseType defaultFailureResponse) {
        super(defaultFailureResponse);
    }

    @Override
    public ConditionType<?> getType() {
        return QuestingRegistries.NO_HEALTH_GAINED;
    }

    @Override
    public Instance createConditionInstance(World world, Quest quest) {
        return new Instance(this);
    }

    static final class Instance extends Condition {

        private final Object2IntMap<UUID> statusCache = new Object2IntOpenHashMap<>();

        public Instance(NoHealthGainedCondition provider) {
            super(provider);
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
                    boolean shouldFail = shouldFail(player);
                    if (!shouldFail)
                        continue;
                    return this.getProvider().getDefaultFailureResponse();
                }
                return ResponseType.OK;
            });
        }

        @Override
        public void onConditionConstructing(Party party, Quest quest, World world) {
            party.forEachOnlineMemberExcept(null, world, player -> {
                int healthLevel = Math.round(player.getHealth());
                statusCache.put(player.getUUID(), healthLevel);
            });
        }

        private boolean shouldFail(PlayerEntity player) {
            UUID playerId = player.getUUID();
            if (!statusCache.containsKey(playerId))
                return true;
            int value = statusCache.getInt(playerId);
            int level = Math.round(player.getHealth());
            if (level > value) {
                return true;
            }
            statusCache.put(playerId, level); // Store actual level
            return false;
        }
    }
}

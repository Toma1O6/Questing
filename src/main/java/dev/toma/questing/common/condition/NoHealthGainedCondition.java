package dev.toma.questing.common.condition;

import com.mojang.serialization.Codec;
import dev.toma.questing.common.init.QuestingRegistries;
import dev.toma.questing.common.party.Party;
import dev.toma.questing.common.quest.Quest;
import dev.toma.questing.common.trigger.TriggerResponse;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

import java.util.UUID;

public class NoHealthGainedCondition extends ConditionProvider<NoHealthGainedCondition.Instance> {

    public static final Codec<NoHealthGainedCondition> CODEC = Codec.STRING.comapFlatMap(TriggerResponse::fromString, Enum::name)
            .optionalFieldOf("onFail", TriggerResponse.FAIL).codec()
            .xmap(NoHealthGainedCondition::new, ConditionProvider::getDefaultFailureResponse);

    public NoHealthGainedCondition(TriggerResponse defaultFailureResponse) {
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

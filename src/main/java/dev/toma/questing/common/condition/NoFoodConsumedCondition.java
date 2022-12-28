package dev.toma.questing.common.condition;

import com.mojang.serialization.Codec;
import dev.toma.questing.common.init.QuestingRegistries;
import dev.toma.questing.common.party.Party;
import dev.toma.questing.common.quest.Quest;
import dev.toma.questing.common.trigger.TriggerResponse;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.FoodStats;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NoFoodConsumedCondition extends ConditionProvider<NoFoodConsumedCondition.Instance> {

    public static final Codec<NoFoodConsumedCondition> CODEC = Codec.STRING.comapFlatMap(TriggerResponse::fromString, Enum::name)
            .optionalFieldOf("onFail", TriggerResponse.FAIL).codec()
            .xmap(NoFoodConsumedCondition::new, ConditionProvider::getDefaultFailureResponse);

    public NoFoodConsumedCondition(TriggerResponse defaultFailureResponse) {
        super(defaultFailureResponse);
    }

    @Override
    public ConditionType<?> getType() {
        return QuestingRegistries.NO_FOOD_CONSUMED;
    }

    @Override
    public Instance createConditionInstance(World world, Quest quest) {
        return new Instance(this);
    }

    static final class Instance extends Condition {

        private final Object2IntMap<UUID> statusCache = new Object2IntOpenHashMap<>();

        public Instance(NoFoodConsumedCondition provider) {
            super(provider);
        }

        @Override
        public void onConditionConstructing(Party party, Quest quest, World world) {
            party.forEachOnlineMemberExcept(null, world, player -> {
                UUID playerId = player.getUUID();
                FoodStats stats = player.getFoodData();
                statusCache.put(playerId, stats.getFoodLevel());
            });
        }

        @Override
        public void registerTriggerResponders(ConditionRegisterHandler registerHandler) {

        }

        private boolean shouldFail(PlayerEntity player) {
            UUID playerId = player.getUUID();
            if (!statusCache.containsKey(playerId))
                return true;
            int value = statusCache.getInt(playerId);
            FoodStats stats = player.getFoodData();
            int level = stats.getFoodLevel();
            if (level > value) {
                return true;
            }
            statusCache.put(playerId, level); // Store actual level
            return false;
        }
    }
}

package dev.toma.questing.common.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.toma.questing.common.init.QuestingRegistries;
import dev.toma.questing.common.trigger.ResponseType;
import dev.toma.questing.utils.Codecs;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.entity.player.PlayerEntity;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

public class NoFoodConsumedCondition extends AbstractIntStatusCondition {

    public static final Codec<NoFoodConsumedCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.comapFlatMap(ResponseType::fromString, Enum::name).optionalFieldOf("onFail", ResponseType.FAIL).forGetter(AbstractDefaultCondition::getDefaultFailureResponse),
            Codec.unboundedMap(Codecs.UUID_STRING, Codec.INT).optionalFieldOf("statusMap", Collections.emptyMap()).forGetter(AbstractIntStatusCondition::getStatusMap)
    ).apply(instance, NoFoodConsumedCondition::new));

    public NoFoodConsumedCondition(ResponseType defaultFailureResponse) {
        super(defaultFailureResponse);
    }

    public NoFoodConsumedCondition(ResponseType defaultResponseType, Map<UUID, Integer> map) {
        super(defaultResponseType, map);
    }

    @Override
    public ConditionType<?> getType() {
        return QuestingRegistries.NO_FOOD_CONSUMED;
    }

    @Override
    public int getValue(PlayerEntity player) {
        return player.getFoodData().getFoodLevel();
    }

    @Override
    public Condition copy() {
        return new NoFoodConsumedCondition(this.getDefaultFailureResponse(), new Object2IntOpenHashMap<>(this.getStatusMap()));
    }
}

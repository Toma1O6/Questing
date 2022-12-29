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

public class NoHealthGainedCondition extends AbstractIntStatusCondition {

    public static final Codec<NoHealthGainedCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.comapFlatMap(ResponseType::fromString, Enum::name).optionalFieldOf("onFail", ResponseType.FAIL).forGetter(AbstractDefaultCondition::getDefaultFailureResponse),
            Codec.unboundedMap(Codecs.UUID_STRING, Codec.INT).optionalFieldOf("statusMap", Collections.emptyMap()).forGetter(AbstractIntStatusCondition::getStatusMap)
    ).apply(instance, NoHealthGainedCondition::new));

    public NoHealthGainedCondition(ResponseType defaultResponseType, Map<UUID, Integer> map) {
        super(defaultResponseType, map);
    }

    public NoHealthGainedCondition(ResponseType defaultFailureResponse) {
        super(defaultFailureResponse);
    }

    @Override
    public ConditionType<?> getType() {
        return QuestingRegistries.NO_HEALTH_GAINED;
    }

    @Override
    public int getValue(PlayerEntity player) {
        return Math.round(player.getHealth());
    }

    @Override
    public Condition copy() {
        return new NoHealthGainedCondition(this.getDefaultFailureResponse(), new Object2IntOpenHashMap<>(this.getStatusMap()));
    }
}

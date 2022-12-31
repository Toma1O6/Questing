package dev.toma.questing.common.component.condition.provider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.toma.questing.common.component.condition.ConditionType;
import dev.toma.questing.common.component.condition.instance.DistanceCondition;
import dev.toma.questing.common.component.trigger.ResponseType;
import dev.toma.questing.common.init.QuestingRegistries;
import dev.toma.questing.common.quest.Quest;

public class DistanceConditionProvider extends AbstractDefaultConditionProvider<DistanceCondition> {

    public static final Codec<DistanceConditionProvider> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.comapFlatMap(ResponseType::fromString, Enum::name).optionalFieldOf("onFail", ResponseType.PASS).forGetter(AbstractDefaultConditionProvider::getDefaultFailureResponse),
            Codec.DOUBLE.fieldOf("minDistance").forGetter(t -> t.min),
            Codec.DOUBLE.fieldOf("maxDistance").forGetter(t -> t.max)
    ).apply(instance, DistanceConditionProvider::new));
    private final double min;
    private final double max;

    public DistanceConditionProvider(ResponseType defaultFailureResponse, double min, double max) {
        super(defaultFailureResponse);
        this.min = min;
        this.max = max;
    }

    @Override
    public DistanceCondition createCondition(Quest quest) {
        return new DistanceCondition(this);
    }

    @Override
    public ConditionType<DistanceCondition, ?> getType() {
        return QuestingRegistries.DISTANCE_CONDITION;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }
}

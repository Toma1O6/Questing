package dev.toma.questing.common.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.toma.questing.common.quest.Quest;
import dev.toma.questing.common.trigger.TriggerResponse;
import net.minecraft.world.World;

public class DistanceCondition extends ConditionProvider<DistanceCondition.Instance> {

    public static final Codec<DistanceCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.comapFlatMap(TriggerResponse::fromString, Enum::name).optionalFieldOf("onFail", TriggerResponse.PASS).forGetter(ConditionProvider::getDefaultFailureResponse),
            Codec.DOUBLE.fieldOf("minDistance").forGetter(t -> t.min),
            Codec.DOUBLE.fieldOf("maxDistance").forGetter(t -> t.max)
    ).apply(instance, DistanceCondition::new));
    private final double min;
    private final double max;

    public DistanceCondition(TriggerResponse defaultFailureResponse, double min, double max) {
        super(defaultFailureResponse);
        this.min = min;
        this.max = max;
    }

    @Override
    public ConditionType<?> getType() {
        return null;
    }

    @Override
    public Instance createConditionInstance(World world, Quest quest) {
        return new Instance(this);
    }

    static final class Instance extends Condition {

        public Instance(DistanceCondition provider) {
            super(provider);
        }

        @Override
        public void registerTriggerResponders(ConditionRegisterHandler registerHandler) {

        }
    }
}

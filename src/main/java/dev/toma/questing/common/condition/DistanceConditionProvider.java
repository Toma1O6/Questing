package dev.toma.questing.common.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.toma.questing.common.quest.Quest;
import dev.toma.questing.common.trigger.TriggerResponse;
import net.minecraft.world.World;

public class DistanceConditionProvider extends ConditionProvider<DistanceConditionProvider.Instance> {

    public static final Codec<DistanceConditionProvider> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.comapFlatMap(TriggerResponse::fromString, Enum::name).optionalFieldOf("onFail", TriggerResponse.PASS).forGetter(ConditionProvider::getDefaultFailureResponse),
            Codec.DOUBLE.fieldOf("minDistance").forGetter(t -> t.min),
            Codec.DOUBLE.fieldOf("maxDistance").forGetter(t -> t.max)
    ).apply(instance, DistanceConditionProvider::new));
    private final double min;
    private final double max;

    public DistanceConditionProvider(TriggerResponse defaultFailureResponse, double min, double max) {
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

        public Instance(DistanceConditionProvider provider) {
            super(provider);
        }

        @Override
        public void registerTriggerResponders(ConditionRegisterHandler registerHandler) {

        }
    }
}

package dev.toma.questing.common.condition;

import com.mojang.serialization.Codec;
import dev.toma.questing.common.quest.Quest;
import dev.toma.questing.common.trigger.TriggerResponse;
import net.minecraft.world.World;

public class ExplodeCondition extends ConditionProvider<ExplodeCondition.Instance> {

    public static final Codec<ExplodeCondition> CODEC = Codec.STRING.comapFlatMap(TriggerResponse::fromString, Enum::name)
            .optionalFieldOf("onFail", TriggerResponse.PASS)
            .xmap(ExplodeCondition::new, ConditionProvider::getDefaultFailureResponse)
            .codec();

    public ExplodeCondition(TriggerResponse defaultFailureResponse) {
        super(defaultFailureResponse);
    }

    @Override
    public ConditionType<?> getType() {
        return null;
    }

    @Override
    public Instance createConditionInstance(World world, Quest quest) {
        return null;
    }

    static final class Instance extends Condition {

        public Instance(ExplodeCondition provider) {
            super(provider);
        }

        @Override
        public void registerTriggerResponders(ConditionRegisterHandler registerHandler) {

        }
    }
}

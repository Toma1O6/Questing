package dev.toma.questing.common.condition;

import com.mojang.serialization.Codec;
import dev.toma.questing.common.init.QuestingRegistries;
import dev.toma.questing.common.quest.Quest;
import dev.toma.questing.common.trigger.TriggerResponse;
import net.minecraft.world.World;

public class NoDamageGivenCondition extends ConditionProvider<NoDamageGivenCondition.Instance> {

    public static final Codec<NoDamageGivenCondition> CODEC = Codec.STRING.comapFlatMap(TriggerResponse::fromString, Enum::name)
            .optionalFieldOf("onFail", TriggerResponse.PASS).codec()
            .xmap(NoDamageGivenCondition::new, ConditionProvider::getDefaultFailureResponse);

    public NoDamageGivenCondition(TriggerResponse response) {
        super(response);
    }

    @Override
    public ConditionType<?> getType() {
        return QuestingRegistries.NO_DAMAGE_GIVEN_CONDITION;
    }

    @Override
    public Instance createConditionInstance(World world, Quest quest) {
        return new Instance(this);
    }

    static final class Instance extends Condition {

        public Instance(NoDamageGivenCondition provider) {
            super(provider);
        }

        @Override
        public void registerTriggerResponders(ConditionRegisterHandler registerHandler) {

        }
    }
}

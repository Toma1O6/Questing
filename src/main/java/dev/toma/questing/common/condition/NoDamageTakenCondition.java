package dev.toma.questing.common.condition;

import com.mojang.serialization.Codec;
import dev.toma.questing.common.init.QuestingRegistries;
import dev.toma.questing.common.quest.Quest;
import dev.toma.questing.common.trigger.TriggerResponse;
import net.minecraft.world.World;

public class NoDamageTakenCondition extends ConditionProvider<NoDamageTakenCondition.Instance> {

    public static final Codec<NoDamageTakenCondition> CODEC = Codec.STRING.comapFlatMap(TriggerResponse::fromString, Enum::name)
            .optionalFieldOf("onFail", TriggerResponse.PASS).codec()
            .xmap(NoDamageTakenCondition::new, ConditionProvider::getDefaultFailureResponse);

    public NoDamageTakenCondition(TriggerResponse response) {
        super(response);
    }

    @Override
    public ConditionType<?> getType() {
        return QuestingRegistries.NO_DAMAGE_TAKEN_CONDITION;
    }

    @Override
    public Instance createConditionInstance(World world, Quest quest) {
        return new Instance(this);
    }

    static final class Instance extends Condition {

        public Instance(NoDamageTakenCondition provider) {
            super(provider);
        }

        @Override
        public void registerTriggerResponders(ConditionRegisterHandler registerHandler) {

        }
    }
}

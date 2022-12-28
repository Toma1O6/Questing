package dev.toma.questing.common.condition;

import com.mojang.serialization.Codec;
import dev.toma.questing.common.init.QuestingRegistries;
import dev.toma.questing.common.quest.Quest;
import dev.toma.questing.common.trigger.ResponseType;
import net.minecraft.world.World;

public class NoDamageGivenCondition extends ConditionProvider<NoDamageGivenCondition.Instance> {

    public static final Codec<NoDamageGivenCondition> CODEC = Codec.STRING.comapFlatMap(ResponseType::fromString, Enum::name)
            .optionalFieldOf("onFail", ResponseType.PASS).codec()
            .xmap(NoDamageGivenCondition::new, ConditionProvider::getDefaultFailureResponse);

    public NoDamageGivenCondition(ResponseType response) {
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

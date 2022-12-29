package dev.toma.questing.common.condition;

import com.mojang.serialization.Codec;
import dev.toma.questing.common.init.QuestingRegistries;
import dev.toma.questing.common.trigger.Events;
import dev.toma.questing.common.trigger.ResponseType;
import net.minecraft.entity.LivingEntity;

public class NoDamageTakenCondition extends AbstractDefaultCondition {

    public static final Codec<NoDamageTakenCondition> CODEC = Codec.STRING.comapFlatMap(ResponseType::fromString, Enum::name)
            .optionalFieldOf("onFail", ResponseType.PASS).codec()
            .xmap(NoDamageTakenCondition::new, AbstractDefaultCondition::getDefaultFailureResponse);

    public NoDamageTakenCondition(ResponseType response) {
        super(response);
    }

    @Override
    public ConditionType<?> getType() {
        return QuestingRegistries.NO_DAMAGE_TAKEN_CONDITION;
    }

    @Override
    public void registerTriggerResponders(ConditionRegisterHandler registerHandler) {
        registerHandler.register(Events.DAMAGE_EVENT, (eventData, quest) -> {
            LivingEntity entity = eventData.getEntity();
            if (Condition.checkIfEntityIsPartyMember(entity, quest.getParty())) {
                return this.getDefaultFailureResponse();
            }
            return ResponseType.SKIP;
        });
    }

    @Override
    public Condition copy() {
        return new NoDamageTakenCondition(this.getDefaultFailureResponse());
    }
}

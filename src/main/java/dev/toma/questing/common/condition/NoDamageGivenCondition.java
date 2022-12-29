package dev.toma.questing.common.condition;

import com.mojang.serialization.Codec;
import dev.toma.questing.common.init.QuestingRegistries;
import dev.toma.questing.common.party.Party;
import dev.toma.questing.common.trigger.Events;
import dev.toma.questing.common.trigger.ResponseType;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;

public class NoDamageGivenCondition extends AbstractDefaultCondition {

    public static final Codec<NoDamageGivenCondition> CODEC = Codec.STRING.comapFlatMap(ResponseType::fromString, Enum::name)
            .optionalFieldOf("onFail", ResponseType.PASS).codec()
            .xmap(NoDamageGivenCondition::new, AbstractDefaultCondition::getDefaultFailureResponse);

    public NoDamageGivenCondition(ResponseType response) {
        super(response);
    }

    @Override
    public ConditionType<?> getType() {
        return QuestingRegistries.NO_DAMAGE_GIVEN_CONDITION;
    }

    @Override
    public void registerTriggerResponders(ConditionRegisterHandler registerHandler) {
        registerHandler.register(Events.DAMAGE_EVENT, (eventData, quest) -> {
            Party party = quest.getParty();
            DamageSource damageSource = eventData.getSource();
            Entity sourceEntity = damageSource.getEntity();
            if (Condition.checkIfEntityIsPartyMember(sourceEntity, party)) {
                return this.getDefaultFailureResponse();
            }
            return ResponseType.SKIP;
        });
    }

    @Override
    public Condition copy() {
        return new NoDamageGivenCondition(this.getDefaultFailureResponse());
    }
}

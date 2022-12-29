package dev.toma.questing.common.condition;

import com.mojang.serialization.Codec;
import dev.toma.questing.common.init.QuestingRegistries;
import dev.toma.questing.common.trigger.Events;
import dev.toma.questing.common.trigger.ResponseType;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;

public class ExplodeCondition extends AbstractDefaultCondition {

    public static final Codec<ExplodeCondition> CODEC = Codec.STRING.comapFlatMap(ResponseType::fromString, Enum::name)
            .optionalFieldOf("onFail", ResponseType.PASS)
            .xmap(ExplodeCondition::new, AbstractDefaultCondition::getDefaultFailureResponse)
            .codec();

    public ExplodeCondition(ResponseType defaultFailureResponse) {
        super(defaultFailureResponse);
    }

    @Override
    public ConditionType<?> getType() {
        return QuestingRegistries.EXPLODE_CONDITION;
    }

    @Override
    public void registerTriggerResponders(ConditionRegisterHandler registerHandler) {
        registerHandler.register(Events.DEATH_EVENT, (eventData, quest) -> {
            DamageSource source = eventData.getSource();
            Entity owner = source.getEntity();
            if (Condition.checkIfEntityIsPartyMember(owner, quest.getParty())) {
                return source.isExplosion() ? ResponseType.OK : this.getDefaultFailureResponse();
            }
            return ResponseType.SKIP;
        });
    }

    @Override
    public Condition copy() {
        return new ExplodeCondition(this.getDefaultFailureResponse());
    }
}

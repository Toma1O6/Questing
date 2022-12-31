package dev.toma.questing.common.component.condition.instance;

import com.mojang.serialization.Codec;
import dev.toma.questing.common.component.condition.provider.NoDamageTakenConditionProvider;
import dev.toma.questing.common.component.trigger.Events;
import dev.toma.questing.common.component.trigger.ResponseType;
import dev.toma.questing.common.quest.ConditionRegisterHandler;
import dev.toma.questing.utils.Utils;
import net.minecraft.entity.LivingEntity;

public class NoDamageTakenCondition implements Condition {

    public static final Codec<NoDamageTakenCondition> CODEC = NoDamageTakenConditionProvider.CODEC
            .xmap(NoDamageTakenCondition::new, t -> t.provider).fieldOf("provider").codec();
    private final NoDamageTakenConditionProvider provider;

    public NoDamageTakenCondition(NoDamageTakenConditionProvider provider) {
        this.provider = provider;
    }

    @Override
    public NoDamageTakenConditionProvider getProvider() {
        return provider;
    }

    @Override
    public void registerTriggerResponders(ConditionRegisterHandler registerHandler) {
        registerHandler.register(Events.DAMAGE_EVENT, (eventData, level, quest) -> {
            LivingEntity entity = eventData.getEntity();
            if (Utils.checkIfEntityIsPartyMember(entity, quest.getParty())) {
                return this.provider.getDefaultFailureResponse();
            }
            return ResponseType.SKIP;
        });
    }
}

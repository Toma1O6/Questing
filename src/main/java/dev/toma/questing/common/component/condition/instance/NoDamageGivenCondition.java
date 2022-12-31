package dev.toma.questing.common.component.condition.instance;

import com.mojang.serialization.Codec;
import dev.toma.questing.common.component.condition.ConditionRegisterHandler;
import dev.toma.questing.common.component.condition.provider.NoDamageGivenConditionProvider;
import dev.toma.questing.common.component.trigger.Events;
import dev.toma.questing.common.component.trigger.ResponseType;
import dev.toma.questing.common.party.Party;
import dev.toma.questing.utils.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;

public class NoDamageGivenCondition implements Condition {

    public static final Codec<NoDamageGivenCondition> CODEC = NoDamageGivenConditionProvider.CODEC
            .xmap(NoDamageGivenCondition::new, t -> t.provider).fieldOf("provider").codec();
    private final NoDamageGivenConditionProvider provider;

    public NoDamageGivenCondition(NoDamageGivenConditionProvider provider) {
        this.provider = provider;
    }

    @Override
    public NoDamageGivenConditionProvider getProvider() {
        return provider;
    }

    @Override
    public void registerTriggerResponders(ConditionRegisterHandler registerHandler) {
        registerHandler.register(Events.DAMAGE_EVENT, (eventData, quest) -> {
            Party party = quest.getParty();
            DamageSource damageSource = eventData.getSource();
            Entity sourceEntity = damageSource.getEntity();
            if (Utils.checkIfEntityIsPartyMember(sourceEntity, party)) {
                return this.provider.getDefaultFailureResponse();
            }
            return ResponseType.SKIP;
        });
    }
}

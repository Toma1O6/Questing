package dev.toma.questing.common.component.condition.instance;

import com.mojang.serialization.Codec;
import dev.toma.questing.common.component.condition.ConditionRegisterHandler;
import dev.toma.questing.common.component.condition.provider.ExplodeConditionProvider;
import dev.toma.questing.common.component.trigger.Events;
import dev.toma.questing.common.component.trigger.ResponseType;
import dev.toma.questing.utils.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;

public class ExplodeCondition implements Condition {

    public static final Codec<ExplodeCondition> CODEC = ExplodeConditionProvider.CODEC
            .xmap(ExplodeCondition::new, t -> t.provider).fieldOf("provider").codec();
    private final ExplodeConditionProvider provider;

    public ExplodeCondition(ExplodeConditionProvider provider) {
        this.provider = provider;
    }

    @Override
    public ExplodeConditionProvider getProvider() {
        return this.provider;
    }

    @Override
    public void registerTriggerResponders(ConditionRegisterHandler registerHandler) {
        registerHandler.register(Events.DEATH_EVENT, (eventData, quest) -> {
            DamageSource source = eventData.getSource();
            Entity owner = source.getEntity();
            if (Utils.checkIfEntityIsPartyMember(owner, quest.getParty())) {
                return source.isExplosion() ? ResponseType.OK : this.provider.getDefaultFailureResponse();
            }
            return ResponseType.SKIP;
        });
    }
}

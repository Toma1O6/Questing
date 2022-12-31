package dev.toma.questing.common.component.condition.instance;

import com.mojang.serialization.Codec;
import dev.toma.questing.common.component.condition.ConditionRegisterHandler;
import dev.toma.questing.common.component.condition.provider.DistanceConditionProvider;
import dev.toma.questing.common.component.condition.provider.ConditionProvider;
import dev.toma.questing.common.component.trigger.Events;
import dev.toma.questing.common.component.trigger.ResponseType;
import dev.toma.questing.utils.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;

public class DistanceCondition implements Condition {

    public static final Codec<DistanceCondition> CODEC = DistanceConditionProvider.CODEC
            .xmap(DistanceCondition::new, t -> t.provider).fieldOf("provider").codec();
    private final DistanceConditionProvider provider;

    public DistanceCondition(DistanceConditionProvider provider) {
        this.provider = provider;
    }

    @Override
    public DistanceConditionProvider getProvider() {
        return this.provider;
    }

    @Override
    public void registerTriggerResponders(ConditionRegisterHandler registerHandler) {
        registerHandler.register(Events.DEATH_EVENT, (eventData, quest) -> {
            DamageSource source = eventData.getSource();
            Entity origin = source.getEntity();
            if (Utils.checkIfEntityIsPartyMember(origin, quest.getParty())) {
                Entity victim = eventData.getEntity();
                double distance = Utils.getEntityDistance3(origin, victim);
                double min = this.provider.getMin() < 0 ? 0 : this.provider.getMin();
                double max = this.provider.getMax() < 0 ? Double.MAX_VALUE : this.provider.getMax();
                return distance >= min && distance <= max ? ResponseType.OK : this.provider.getDefaultFailureResponse();
            }
            return ResponseType.SKIP;
        });
    }
}

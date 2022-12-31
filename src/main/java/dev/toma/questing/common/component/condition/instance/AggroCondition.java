package dev.toma.questing.common.component.condition.instance;

import com.mojang.serialization.Codec;
import dev.toma.questing.common.component.condition.ConditionRegisterHandler;
import dev.toma.questing.common.component.condition.provider.AggroConditionProvider;
import dev.toma.questing.common.component.trigger.Events;
import dev.toma.questing.common.component.trigger.ResponseType;
import dev.toma.questing.common.component.trigger.event.DeathEvent;
import dev.toma.questing.common.party.Party;
import dev.toma.questing.common.quest.Quest;
import dev.toma.questing.utils.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;

public class AggroCondition implements Condition {

    public static final Codec<AggroCondition> CODEC = AggroConditionProvider.CODEC
            .xmap(AggroCondition::new, t -> t.provider).fieldOf("provider").codec();
    private final AggroConditionProvider provider;

    public AggroCondition(AggroConditionProvider provider) {
        this.provider = provider;
    }

    @Override
    public AggroConditionProvider getProvider() {
        return provider;
    }

    @Override
    public void registerTriggerResponders(ConditionRegisterHandler registerHandler) {
        registerHandler.register(Events.DEATH_EVENT, this::validate);
        registerHandler.register(Events.DAMAGE_EVENT, this::validate);
    }

    protected ResponseType validate(DeathEvent event, Quest quest) {
        DamageSource source = event.getSource();
        Entity origin = source.getEntity();
        Party party = quest.getParty();
        if (Utils.checkIfEntityIsPartyMember(origin, party)) {
            AggroConditionProvider.AggroTarget aggroTarget = this.provider.getAggroTarget();
            LivingEntity victim = event.getEntity();
            if (victim instanceof MobEntity) {
                MobEntity mobEntity = (MobEntity) victim;
                LivingEntity target = mobEntity.getTarget();
                if (!aggroTarget.isValidTarget(target, (PlayerEntity) origin)) {
                    return this.provider.getDefaultFailureResponse();
                }
                return ResponseType.OK;
            }
            return ResponseType.PASS;
        }
        return ResponseType.SKIP;
    }
}

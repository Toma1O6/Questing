package dev.toma.questing.common.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.toma.questing.common.init.QuestingRegistries;
import dev.toma.questing.common.party.Party;
import dev.toma.questing.common.quest.Quest;
import dev.toma.questing.common.trigger.Events;
import dev.toma.questing.common.trigger.ResponseType;
import dev.toma.questing.common.trigger.event.DeathEvent;
import dev.toma.questing.utils.Codecs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;

import javax.annotation.Nullable;

public class AggroCondition extends AbstractDefaultCondition {

    public static final Codec<AggroCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.comapFlatMap(ResponseType::fromString, Enum::name).optionalFieldOf("onFail", ResponseType.PASS).forGetter(AbstractDefaultCondition::getDefaultFailureResponse),
            Codecs.enumCodec(AggroTarget.class, String::toUpperCase).fieldOf("target").forGetter(t -> t.aggroTarget)
    ).apply(instance, AggroCondition::new));

    private final AggroTarget aggroTarget;

    public AggroCondition(ResponseType defaultFailureResponse, AggroTarget aggroTarget) {
        super(defaultFailureResponse);
        this.aggroTarget = aggroTarget;
    }

    @Override
    public ConditionType<?> getType() {
        return QuestingRegistries.AGGRO_CONDITION;
    }

    @Override
    public void registerTriggerResponders(ConditionRegisterHandler registerHandler) {
        registerHandler.register(Events.DEATH_EVENT, this::validate);
        registerHandler.register(Events.DAMAGE_EVENT, this::validate);
    }

    @Override
    public Condition copy() {
        return new AggroCondition(this.getDefaultFailureResponse(), this.aggroTarget);
    }

    private ResponseType validate(DeathEvent event, Quest quest) {
        DamageSource source = event.getSource();
        Entity origin = source.getEntity();
        Party party = quest.getParty();
        if (Condition.checkIfEntityIsPartyMember(origin, party)) {
            AggroTarget.TargetValidator validator = aggroTarget.validator;
            LivingEntity victim = event.getEntity();
            if (victim instanceof MobEntity) {
                MobEntity mobEntity = (MobEntity) victim;
                LivingEntity target = mobEntity.getTarget();
                if (!validator.isValid(target, (PlayerEntity) origin)) {
                    return this.getDefaultFailureResponse();
                }
                return ResponseType.OK;
            }
            return ResponseType.PASS;
        }
        return ResponseType.SKIP;
    }

    public enum AggroTarget {

        ANY((target, src) -> target != null),
        TRIGGER_PLAYER((target, src) -> target.is(src)),
        NOT_TRIGGER_PLAYER((target, src) -> !target.is(src)),
        NO_TARGET((target, src) -> target == null);

        private final TargetValidator validator;

        AggroTarget(TargetValidator validator) {
            this.validator = validator;
        }

        private interface TargetValidator {
            boolean isValid(@Nullable LivingEntity target, PlayerEntity src);
        }
    }
}

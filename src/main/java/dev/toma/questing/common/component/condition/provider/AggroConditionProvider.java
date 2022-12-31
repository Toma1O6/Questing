package dev.toma.questing.common.component.condition.provider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.toma.questing.common.component.condition.ConditionType;
import dev.toma.questing.common.component.condition.instance.AggroCondition;
import dev.toma.questing.common.component.trigger.ResponseType;
import dev.toma.questing.common.init.QuestingRegistries;
import dev.toma.questing.common.quest.Quest;
import dev.toma.questing.utils.Codecs;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

import javax.annotation.Nullable;

public class AggroConditionProvider extends AbstractDefaultConditionProvider<AggroCondition> {

    public static final Codec<AggroConditionProvider> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.comapFlatMap(ResponseType::fromString, Enum::name).optionalFieldOf("onFail", ResponseType.PASS).forGetter(AbstractDefaultConditionProvider::getDefaultFailureResponse),
            Codecs.enumCodec(AggroTarget.class, String::toUpperCase).optionalFieldOf("target", AggroTarget.ANY).forGetter(t -> t.aggroTarget)
    ).apply(instance, AggroConditionProvider::new));

    private final AggroTarget aggroTarget;

    public AggroConditionProvider(ResponseType defaultFailureResponse, AggroTarget aggroTarget) {
        super(defaultFailureResponse);
        this.aggroTarget = aggroTarget;
    }

    @Override
    public AggroCondition createCondition(Quest quest) {
        return new AggroCondition(this);
    }

    @Override
    public ConditionType<AggroCondition, ?> getType() {
        return QuestingRegistries.AGGRO_CONDITION;
    }

    public AggroTarget getAggroTarget() {
        return aggroTarget;
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

        public boolean isValidTarget(@Nullable LivingEntity target, PlayerEntity damageOrigin) {
            return this.validator.isValid(target, damageOrigin);
        }

        private interface TargetValidator {
            boolean isValid(@Nullable LivingEntity target, PlayerEntity src);
        }
    }
}

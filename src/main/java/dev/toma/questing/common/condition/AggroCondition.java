package dev.toma.questing.common.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.toma.questing.common.init.QuestingRegistries;
import dev.toma.questing.common.quest.Quest;
import dev.toma.questing.common.trigger.ResponseType;
import dev.toma.questing.utils.Codecs;
import net.minecraft.world.World;

public class AggroCondition extends ConditionProvider<AggroCondition.Instance> {

    public static final Codec<AggroCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.comapFlatMap(ResponseType::fromString, Enum::name).optionalFieldOf("onFail", ResponseType.PASS).forGetter(ConditionProvider::getDefaultFailureResponse),
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
    public Instance createConditionInstance(World world, Quest quest) {
        return new Instance(this);
    }

    public enum AggroTarget {
        ANY,
        TRIGGER_PLAYER,
        NOT_TRIGGER_PLAYER,
        NO_TARGET
    }

    static final class Instance extends Condition {

        public Instance(AggroCondition provider) {
            super(provider);
        }

        @Override
        public void registerTriggerResponders(ConditionRegisterHandler registerHandler) {

        }
    }
}

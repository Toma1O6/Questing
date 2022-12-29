package dev.toma.questing.common.condition;

import com.mojang.serialization.Codec;
import dev.toma.questing.common.init.QuestingRegistries;
import dev.toma.questing.common.quest.Quest;
import dev.toma.questing.common.trigger.Events;
import dev.toma.questing.common.trigger.ResponseType;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class ExplodeCondition extends ConditionProvider<ExplodeCondition.Instance> {

    public static final Codec<ExplodeCondition> CODEC = Codec.STRING.comapFlatMap(ResponseType::fromString, Enum::name)
            .optionalFieldOf("onFail", ResponseType.PASS)
            .xmap(ExplodeCondition::new, ConditionProvider::getDefaultFailureResponse)
            .codec();

    public ExplodeCondition(ResponseType defaultFailureResponse) {
        super(defaultFailureResponse);
    }

    @Override
    public ConditionType<?> getType() {
        return QuestingRegistries.EXPLODE_CONDITION;
    }

    @Override
    public Instance createConditionInstance(World world, Quest quest) {
        return new Instance(this);
    }

    static final class Instance extends Condition {

        private static final Codec<Instance> CODEC = ExplodeCondition.CODEC
                .xmap(Instance::new, t -> (ExplodeCondition) t.getProvider())
                .fieldOf("provider").codec();

        public Instance(ExplodeCondition provider) {
            super(provider);
        }

        @Override
        public Codec<? extends Condition> codec() {
            return CODEC;
        }

        @Override
        public void registerTriggerResponders(ConditionRegisterHandler registerHandler) {
            registerHandler.register(Events.DEATH_EVENT, (eventData, quest) -> {
                DamageSource source = eventData.getSource();
                Entity owner = source.getEntity();
                if (checkIfEntityIsPartyMember(owner, quest.getParty())) {
                    return source.isExplosion() ? ResponseType.OK : this.getProvider().getDefaultFailureResponse();
                }
                return ResponseType.SKIP;
            });
        }
    }
}

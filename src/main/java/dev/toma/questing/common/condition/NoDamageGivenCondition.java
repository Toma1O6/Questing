package dev.toma.questing.common.condition;

import com.mojang.serialization.Codec;
import dev.toma.questing.common.init.QuestingRegistries;
import dev.toma.questing.common.party.Party;
import dev.toma.questing.common.quest.Quest;
import dev.toma.questing.common.trigger.Events;
import dev.toma.questing.common.trigger.ResponseType;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class NoDamageGivenCondition extends ConditionProvider<NoDamageGivenCondition.Instance> {

    public static final Codec<NoDamageGivenCondition> CODEC = Codec.STRING.comapFlatMap(ResponseType::fromString, Enum::name)
            .optionalFieldOf("onFail", ResponseType.PASS).codec()
            .xmap(NoDamageGivenCondition::new, ConditionProvider::getDefaultFailureResponse);

    public NoDamageGivenCondition(ResponseType response) {
        super(response);
    }

    @Override
    public ConditionType<?> getType() {
        return QuestingRegistries.NO_DAMAGE_GIVEN_CONDITION;
    }

    @Override
    public Instance createConditionInstance(World world, Quest quest) {
        return new Instance(this);
    }

    static final class Instance extends Condition {

        private static final Codec<Instance> CODEC = NoDamageGivenCondition.CODEC
                .xmap(Instance::new, t -> (NoDamageGivenCondition) t.getProvider())
                .fieldOf("provider").codec();

        public Instance(NoDamageGivenCondition provider) {
            super(provider);
        }

        @Override
        public Codec<? extends Condition> codec() {
            return CODEC;
        }

        @Override
        public void registerTriggerResponders(ConditionRegisterHandler registerHandler) {
            registerHandler.register(Events.DAMAGE_EVENT, (eventData, quest) -> {
                Party party = quest.getParty();
                DamageSource damageSource = eventData.getSource();
                Entity sourceEntity = damageSource.getEntity();
                if (checkIfEntityIsPartyMember(sourceEntity, party)) {
                    return this.getProvider().getDefaultFailureResponse();
                }
                return ResponseType.SKIP;
            });
        }
    }
}

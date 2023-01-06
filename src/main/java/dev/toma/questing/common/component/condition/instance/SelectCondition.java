package dev.toma.questing.common.component.condition.instance;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.toma.questing.common.component.condition.ConditionType;
import dev.toma.questing.common.component.condition.provider.SelectConditionProvider;
import dev.toma.questing.common.party.Party;
import dev.toma.questing.common.quest.ConditionRegisterHandler;
import dev.toma.questing.common.quest.instance.Quest;
import dev.toma.questing.utils.Utils;
import net.minecraft.world.World;

import java.util.List;

public class SelectCondition implements Condition {

    public static final Codec<SelectCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            SelectConditionProvider.CODEC.fieldOf("provider").forGetter(SelectCondition::getProvider),
            ConditionType.CONDITION_CODEC.listOf().fieldOf("conditions").forGetter(t -> t.conditions)
    ).apply(instance, SelectCondition::new));
    private final SelectConditionProvider provider;
    private final List<Condition> conditions;

    private SelectCondition(SelectConditionProvider provider, List<Condition> conditions) {
        this.provider = provider;
        this.conditions = conditions;
    }

    public SelectCondition(SelectConditionProvider provider, Quest quest) {
        this(provider, Utils.getConditions(provider.getConditions(), quest));
    }

    @Override
    public SelectConditionProvider getProvider() {
        return this.provider;
    }

    @Override
    public void registerTriggerResponders(ConditionRegisterHandler registerHandler) {
        this.conditions.forEach(condition -> condition.registerTriggerResponders(registerHandler));
    }

    @Override
    public void onConditionConstructing(Party party, Quest quest, World world) {
        this.conditions.forEach(condition -> condition.onConditionConstructing(party, quest, world));
    }
}

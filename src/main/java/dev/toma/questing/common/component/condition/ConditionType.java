package dev.toma.questing.common.component.condition;

import com.mojang.serialization.Codec;
import dev.toma.questing.common.component.condition.instance.Condition;
import dev.toma.questing.common.component.condition.provider.ConditionProvider;
import dev.toma.questing.common.init.QuestingRegistries;
import dev.toma.questing.utils.IdentifierHolder;
import net.minecraft.util.ResourceLocation;

public class ConditionType<C extends Condition, P extends ConditionProvider<C>> implements IdentifierHolder {

    public static final Codec<ConditionProvider<?>> PROVIDER_CODEC = QuestingRegistries.CONDITION.dispatch("type", ConditionProvider::getType, c -> c.providerCodec);
    public static final Codec<Condition> CONDITION_CODEC = QuestingRegistries.CONDITION.dispatch("type", c -> c.getProvider().getType(), c -> c.conditionCodec);
    private final ResourceLocation identifier;
    private final Codec<P> providerCodec;
    private final Codec<C> conditionCodec;

    public ConditionType(ResourceLocation identifier, Codec<P> providerCodec, Codec<C> conditionCodec) {
        this.identifier = identifier;
        this.providerCodec = providerCodec;
        this.conditionCodec = conditionCodec;
    }

    @Override
    public ResourceLocation getIdentifier() {
        return identifier;
    }
}

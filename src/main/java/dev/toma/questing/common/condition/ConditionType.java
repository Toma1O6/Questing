package dev.toma.questing.common.condition;

import com.mojang.serialization.Codec;
import dev.toma.questing.common.init.QuestingRegistries;
import dev.toma.questing.utils.IdentifierHolder;
import net.minecraft.util.ResourceLocation;

public class ConditionType<C extends Condition> implements IdentifierHolder {

    public static final Codec<Condition> CODEC = QuestingRegistries.CONDITION.dispatch("type", Condition::getType, c -> c.codec);
    private final ResourceLocation identifier;
    private final Codec<C> codec;

    public ConditionType(ResourceLocation identifier, Codec<C> codec) {
        this.identifier = identifier;
        this.codec = codec;
    }

    @Override
    public ResourceLocation getIdentifier() {
        return identifier;
    }
}

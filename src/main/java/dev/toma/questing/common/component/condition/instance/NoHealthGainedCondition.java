package dev.toma.questing.common.component.condition.instance;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.toma.questing.common.component.condition.provider.NoHealthGainedConditionProvider;
import dev.toma.questing.utils.Codecs;
import net.minecraft.entity.player.PlayerEntity;

import java.util.Map;
import java.util.UUID;

public class NoHealthGainedCondition extends AbstractIntStatusCondition {

    public static final Codec<NoHealthGainedCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            NoHealthGainedConditionProvider.CODEC.fieldOf("provider").forGetter(t -> t.provider),
            Codec.unboundedMap(Codecs.UUID_STRING, Codec.INT).fieldOf("statusMap").forGetter(AbstractIntStatusCondition::getStatusMap)
    ).apply(instance, NoHealthGainedCondition::new));
    private final NoHealthGainedConditionProvider provider;

    public NoHealthGainedCondition(NoHealthGainedConditionProvider provider) {
        this.provider = provider;
    }

    public NoHealthGainedCondition(NoHealthGainedConditionProvider provider, Map<UUID, Integer> map) {
        super(map);
        this.provider = provider;
    }

    @Override
    public int getValue(PlayerEntity player) {
        return Math.round(player.getHealth());
    }

    @Override
    public NoHealthGainedConditionProvider getProvider() {
        return provider;
    }
}

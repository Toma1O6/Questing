package dev.toma.questing.common.component.condition.instance;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.toma.questing.common.component.condition.provider.NoFoodConsumedConditionProvider;
import dev.toma.questing.utils.Codecs;
import net.minecraft.entity.player.PlayerEntity;

import java.util.Map;
import java.util.UUID;

public class NoFoodConsumedCondition extends AbstractIntStatusCondition {

    public static final Codec<NoFoodConsumedCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            NoFoodConsumedConditionProvider.CODEC.fieldOf("provider").forGetter(t -> t.provider),
            Codec.unboundedMap(Codecs.UUID_STRING, Codec.INT).fieldOf("statusMap").forGetter(AbstractIntStatusCondition::getStatusMap)
    ).apply(instance, NoFoodConsumedCondition::new));
    private final NoFoodConsumedConditionProvider provider;

    public NoFoodConsumedCondition(NoFoodConsumedConditionProvider provider) {
        this.provider = provider;
    }

    public NoFoodConsumedCondition(NoFoodConsumedConditionProvider provider, Map<UUID, Integer> map) {
        super(map);
        this.provider = provider;
    }

    @Override
    public int getValue(PlayerEntity player) {
        return player.getFoodData().getFoodLevel();
    }

    @Override
    public NoFoodConsumedConditionProvider getProvider() {
        return provider;
    }
}

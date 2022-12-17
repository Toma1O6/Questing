package dev.toma.questing.reward;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.toma.questing.init.QuestingRegistries;
import dev.toma.questing.quest.Quest;
import dev.toma.questing.utils.Codecs;
import net.minecraft.entity.player.PlayerEntity;

public class RewardCountTransformer implements RewardTransformer<Integer> {

    public static final Codec<RewardCountTransformer> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Codecs.enumCodec(Operation.class).fieldOf("operation").forGetter(ins -> ins.operation),
            Codec.FLOAT.fieldOf("value").forGetter(ins -> ins.value)
    ).apply(builder, RewardCountTransformer::new));

    private final Operation operation;
    private final float value;

    public RewardCountTransformer(Operation operation, float value) {
        this.operation = operation;
        this.value = value;
    }

    @Override
    public Integer adjust(Integer originalValue, PlayerEntity player, Quest quest) {
        return this.operation.op.combine(originalValue, value);
    }

    @Override
    public RewardTransformerType<?, ?> getType() {
        return QuestingRegistries.REWARD_COUNT_TRANSFORMER;
    }

    public enum Operation {

        SET((a, b) -> (int) b),
        ADD((a, b) -> (int) (a + b)),
        SUBTRACT((a, b) -> (int) (a - b)),
        MULTIPLY((a, b) -> (int) (a * b)),
        DIVIDE((a, b) -> (int) (a / b));

        private final NumberCombinationOperator op;

        Operation(NumberCombinationOperator op) {
            this.op = op;
        }
    }

    @FunctionalInterface
    public interface NumberCombinationOperator {

        int combine(int baseValue, float in);
    }
}

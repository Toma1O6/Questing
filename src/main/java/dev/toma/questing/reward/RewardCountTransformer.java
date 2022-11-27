package dev.toma.questing.reward;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import dev.toma.questing.quest.Quest;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.JSONUtils;

public class RewardCountTransformer implements RewardTransformer<Integer> {

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

    public static final class Serializer implements RewardTransformerType.Serializer<Integer, RewardCountTransformer> {

        @Override
        public RewardCountTransformer transformerFromJson(JsonObject data) {
            String opName = JSONUtils.getAsString(data, "operation");
            Operation operation;
            try {
                operation = Operation.valueOf(opName.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new JsonSyntaxException("Unknown operation type: " + opName);
            }
            float value = JSONUtils.getAsFloat(data, "value");
            return new RewardCountTransformer(operation, value);
        }
    }
}

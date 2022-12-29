package dev.toma.questing.common.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.toma.questing.common.init.QuestingRegistries;
import dev.toma.questing.common.quest.Quest;
import dev.toma.questing.common.trigger.Events;
import dev.toma.questing.common.trigger.ResponseType;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class DistanceCondition extends ConditionProvider<DistanceCondition.Instance> {

    public static final Codec<DistanceCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.comapFlatMap(ResponseType::fromString, Enum::name).optionalFieldOf("onFail", ResponseType.PASS).forGetter(ConditionProvider::getDefaultFailureResponse),
            Codec.DOUBLE.fieldOf("minDistance").forGetter(t -> t.min),
            Codec.DOUBLE.fieldOf("maxDistance").forGetter(t -> t.max)
    ).apply(instance, DistanceCondition::new));
    private final double min;
    private final double max;

    public DistanceCondition(ResponseType defaultFailureResponse, double min, double max) {
        super(defaultFailureResponse);
        this.min = min;
        this.max = max;
    }

    @Override
    public ConditionType<?> getType() {
        return QuestingRegistries.DISTANCE_CONDITION;
    }

    @Override
    public Instance createConditionInstance(World world, Quest quest) {
        return new Instance(this);
    }

    public static double getEntityDistance2(Entity entity1, Entity entity2) {
        return getDistance2(entity1.getX(), entity1.getZ(), entity2.getX(), entity2.getZ());
    }

    public static double getEntityDistance3(Entity entity1, Entity entity2) {
        return getDistance3(entity1.getX(), entity1.getY(), entity1.getZ(), entity2.getX(), entity2.getY(), entity2.getZ());
    }

    public static double getDistance2(double x1, double z1, double x2, double z2) {
        double x = x1 - x2;
        double z = z1 - z2;
        return Math.sqrt(x * x + z * z);
    }

    public static double getDistance3(double x1, double y1, double z1, double x2, double y2, double z2) {
        double x = x1 - x2;
        double y = y1 - y2;
        double z = z1 - z2;
        return Math.sqrt(x * x + y * y + z * z);
    }

    static final class Instance extends Condition {

        public Instance(DistanceCondition provider) {
            super(provider);
        }

        @Override
        public void registerTriggerResponders(ConditionRegisterHandler registerHandler) {
            registerHandler.register(Events.DEATH_EVENT, (eventData, quest) -> {
                DamageSource source = eventData.getSource();
                Entity origin = source.getEntity();
                if (checkIfEntityIsPartyMember(origin, quest.getParty())) {
                    Entity victim = eventData.getEntity();
                    double distance = DistanceCondition.getEntityDistance3(origin, victim);
                    DistanceCondition distanceCondition = (DistanceCondition) this.getProvider();
                    double min = distanceCondition.min < 0 ? 0 : distanceCondition.min;
                    double max = distanceCondition.max < 0 ? Double.MAX_VALUE : distanceCondition.max;
                    return distance >= min && distance <= max ? ResponseType.OK : distanceCondition.getDefaultFailureResponse();
                }
                return ResponseType.SKIP;
            });
        }
    }
}

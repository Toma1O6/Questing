package dev.toma.questing.area.spawner.processor;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import dev.toma.questing.area.Area;
import dev.toma.questing.area.spawner.Spawner;
import dev.toma.questing.init.QuestingRegistries;
import dev.toma.questing.quest.Quest;
import dev.toma.questing.utils.JsonHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Arrays;
import java.util.function.Supplier;

public class SetEffectsProcessor extends LivingEntityProcessor {

    private final Supplier<EffectInstance>[] effectsProvider;

    public SetEffectsProcessor(Supplier<EffectInstance>[] effectsProvider) {
        this.effectsProvider = effectsProvider;
    }

    @Override
    public void processEntitySpawn(LivingEntity entity, Spawner spawner, World world, Quest quest, Area area) {
        Arrays.stream(effectsProvider)
                .map(Supplier::get)
                .forEach(entity::addEffect);
    }

    @Override
    public SpawnerProcessorType<?> getType() {
        return QuestingRegistries.EFFECTS_SPAWNER_PROCESSOR;
    }

    public static final class Serializer implements SpawnerProcessorType.Serializer<SetEffectsProcessor> {

        @SuppressWarnings("unchecked")
        @Override
        public SetEffectsProcessor processorFromJson(JsonObject data) {
            JsonArray array = JSONUtils.getAsJsonArray(data, "effects");
            Supplier<EffectInstance>[] providers = JsonHelper.mapArray(array, Supplier[]::new, e -> {
                JsonObject effectData = JsonHelper.requireObject(e);
                ResourceLocation id = new ResourceLocation(JSONUtils.getAsString(effectData, "effect"));
                if (!ForgeRegistries.POTIONS.containsKey(id)) {
                    throw new JsonSyntaxException("Unknown effect: " + id);
                }
                Effect effect = ForgeRegistries.POTIONS.getValue(id);
                int duration = JSONUtils.getAsInt(effectData, "duration", 600);
                int amplifier = JSONUtils.getAsInt(effectData, "amplifier", 0);
                boolean ambient = JSONUtils.getAsBoolean(effectData, "ambient", false);
                boolean visible = JSONUtils.getAsBoolean(effectData, "visible", true);
                boolean showIcon = JSONUtils.getAsBoolean(effectData, "showIcon", visible);
                return () -> new EffectInstance(effect, duration, amplifier, ambient, visible, showIcon);
            });
            return new SetEffectsProcessor(providers);
        }
    }
}

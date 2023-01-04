package dev.toma.questing.common.engine;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import dev.toma.questing.Questing;
import dev.toma.questing.file.DataFileManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public final class QuestEngineManager implements DataFileManager.DataHandler<Map<ResourceLocation, CompoundNBT>> {

    public static final Marker MARKER = MarkerManager.getMarker("QuestEngine");
    public static final Codec<Map<ResourceLocation, CompoundNBT>> CODEC = Codec.unboundedMap(ResourceLocation.CODEC, CompoundNBT.CODEC);
    private final Map<ResourceLocation, Entry<?>> engineEntries = new HashMap<>();
    private final Map<ResourceLocation, QuestEngine> engines = new HashMap<>();

    public <E extends QuestEngine> void registerQuestEngine(ResourceLocation identifier, Codec<E> engineCodec, Supplier<E> factory) {
        if (this.engineEntries.put(identifier, new Entry<>(engineCodec, factory)) != null) {
            throw new IllegalArgumentException("Duplicate quest engine registered: " + identifier);
        }
    }

    @SuppressWarnings("unchecked")
    public <E extends QuestEngine> E getQuestEngine(ResourceLocation identifier) {
        E engine = (E) this.engines.get(identifier);
        if (engine == null) {
            throw new UnsupportedOperationException("Engine " + identifier + " is not loaded yet");
        }
        return engine;
    }

    @Override
    public void loadData(Map<ResourceLocation, CompoundNBT> data) {
        for (Map.Entry<ResourceLocation, Entry<?>> entry : this.engineEntries.entrySet()) {
            this.loadEngineData(entry.getKey(), entry.getValue(), data);
        }
    }

    @Override
    public Map<ResourceLocation, CompoundNBT> getSaveData() {
        Map<ResourceLocation, CompoundNBT> map = new HashMap<>();
        for (Map.Entry<ResourceLocation, QuestEngine> entry : this.engines.entrySet()) {
            ResourceLocation location = entry.getKey();
            CompoundNBT result = this.serializeData(entry.getValue());
            map.put(location, result);
        }
        return map;
    }

    private <E extends QuestEngine> void loadEngineData(ResourceLocation location, Entry<E> entry, Map<ResourceLocation, CompoundNBT> data) {
        CompoundNBT nbt = data.get(location);
        if (nbt != null) {
            Codec<E> codec = entry.codec;
            DataResult<E> dataResult = codec.parse(NBTDynamicOps.INSTANCE, nbt);
            Optional<E> optional = dataResult.resultOrPartial(err -> Questing.LOGGER.error(MARKER, "Unable to process engine data {} - {}", location, err));
            optional.ifPresent(engine -> {
                this.engines.put(location, engine);
                engine.onLoaded();
            });
        } else {
            E engine = entry.factory.get();
            this.engines.put(location, engine);
            engine.onLoaded();
        }
    }

    @SuppressWarnings("unchecked")
    private <E extends QuestEngine> CompoundNBT serializeData(E engine) {
        ResourceLocation engineId = engine.getIndentifier();
        Entry<E> entry = (Entry<E>) this.engineEntries.get(engineId);
        DataResult<INBT> result = entry.codec.encodeStart(NBTDynamicOps.INSTANCE, engine);
        Optional<INBT> optional = result.result();
        return optional.map(inbt -> (CompoundNBT) inbt).orElse(new CompoundNBT());
    }

    private static final class Entry<E extends QuestEngine> {

        private final Codec<E> codec;
        private final Supplier<E> factory;

        public Entry(Codec<E> codec, Supplier<E> factory) {
            this.codec = codec;
            this.factory = factory;
        }
    }
}

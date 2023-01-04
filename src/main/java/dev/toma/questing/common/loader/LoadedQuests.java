package dev.toma.questing.common.loader;

import dev.toma.questing.common.quest.provider.QuestProvider;
import net.minecraft.util.ResourceLocation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class LoadedQuests {

    private final Map<ResourceLocation, QuestProvider<?>> map = new HashMap<>();

    public void storeQuest(QuestProvider<?> provider) {
        map.put(provider.getIdentifier(), provider);
    }

    public QuestProvider<?> getQuest(ResourceLocation id) {
        return map.get(id);
    }

    public Collection<QuestProvider<?>> getAllLoadedQuests() {
        return this.map.values();
    }
}

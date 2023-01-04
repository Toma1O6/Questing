package dev.toma.questing.common.loader;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import dev.toma.questing.Questing;
import dev.toma.questing.common.quest.QuestType;
import dev.toma.questing.common.quest.provider.QuestProvider;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Optional;

public final class QuestLoader {

    public static final Marker MARKER = MarkerManager.getMarker("Loader");
    private final LoadedQuests loadedQuestStorage = new LoadedQuests();
    private final File questDirectory = new File("./questing/quests");

    public void loadQuests() {
        questDirectory.mkdirs();
        for (File file : questDirectory.listFiles()) {
            try {
                this.loadFile(file);
            } catch (IOException e) {
                Questing.LOGGER.error(MARKER, "Unable to load {} file due to error {}", file.getPath(), e.getMessage());
            }
        }
    }

    private void loadFile(File file) throws IOException {
        if (file.isDirectory()) {
            for (File nested : file.listFiles()) {
                loadFile(nested);
            }
        } else {
            try (FileReader reader = new FileReader(file)) {
                JsonElement element = new JsonParser().parse(reader);
                DataResult<QuestProvider<?>> dataResult = QuestType.PROVIDER_CODEC.parse(JsonOps.INSTANCE, element);
                Optional<QuestProvider<?>> providerOptional = dataResult.resultOrPartial(err -> Questing.LOGGER.error(MARKER, err));
                providerOptional.ifPresent(this.loadedQuestStorage::storeQuest);
            }
        }
    }
}

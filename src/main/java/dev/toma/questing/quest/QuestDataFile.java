package dev.toma.questing.quest;

import dev.toma.questing.Questing;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.ReportedException;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.io.File;
import java.io.IOException;

@SuppressWarnings("ResultOfMethodCallIgnored")
public final class QuestDataFile {

    public static final Marker MARKER = MarkerManager.getMarker("IO");
    private static final QuestDataFile INSTANCE = new QuestDataFile();
    private static final String QUEST_DATA_FILE = "quests.dat";
    private final Object ioLock = new Object();

    public static QuestDataFile getFile() {
        return INSTANCE;
    }

    // File IO operations
    public void loadData(File worldDir) {
        synchronized (ioLock) {
            try {
                File dataFile = new File(worldDir, QUEST_DATA_FILE);
                if (!dataFile.exists()) {
                    Questing.LOGGER.info(MARKER, "Found no quest data file, skipping loading attempt");
                    return;
                }
                CompoundNBT nbt = CompressedStreamTools.readCompressed(dataFile);
                // TODO process the data
            } catch (IOException e) {
                Questing.LOGGER.fatal(MARKER, "Unable to load quest data");
                throw new ReportedException(CrashReport.forThrowable(e, "Quest data load failed"));
            }
        }
    }

    public void saveData(File worldDir) {
        synchronized (ioLock) {
            try {
                File dataFile = new File(worldDir, QUEST_DATA_FILE);
                if (!dataFile.exists()) {
                    dataFile.createNewFile();
                }
                CompoundNBT nbt = new CompoundNBT(); // TODO save actual data
                CompressedStreamTools.writeCompressed(nbt, dataFile);
            } catch (IOException e) {
                Questing.LOGGER.fatal(MARKER, "Unable to save quest data");
                throw new ReportedException(CrashReport.forThrowable(e, "Quest data save failed"));
            }
        }
    }

    private QuestDataFile() {}
}

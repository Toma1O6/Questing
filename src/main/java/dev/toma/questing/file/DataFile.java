package dev.toma.questing.file;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import dev.toma.questing.Questing;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraftforge.fml.loading.FMLLoader;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class DataFile<T> {

    public static final Marker MARKER = MarkerManager.getMarker("DataFiles");
    private final Object lock = new Object();
    private final Codec<T> codec;
    private final String filename;
    private File currentWorldDir;

    public DataFile(Codec<T> codec, String filename) {
        this.codec = codec;
        this.filename = filename;
    }

    public T readData() {
        File file = new File(Objects.requireNonNull(currentWorldDir, "Current world dir is not set for " + filename), filename);
        if (!file.exists())
            return null;
        try {
            INBT inbt = CompressedStreamTools.readCompressed(file);
            DataResult<T> dataResult = codec.parse(NBTDynamicOps.INSTANCE, inbt);
            return dataResult.resultOrPartial(this::logError).orElse(null);
        } catch (IOException e) {
            Questing.LOGGER.fatal(MARKER, "Unable to read data from file {} due to error {}", filename, e.getMessage());
            if (!FMLLoader.isProduction()) {
                Questing.LOGGER.fatal(MARKER, "Error stacktrace", e);
            }
            return null;
        }
    }

    public CompletableFuture<T> readDataAsync() {
        return CompletableFuture.supplyAsync(this::readData, IOHandler.INSTANCE.service);
    }

    public void writeData(T data) {
        Questing.LOGGER.debug(MARKER, "Preparing data write for {} file", filename);
        DataResult<INBT> result = codec.encodeStart(NBTDynamicOps.INSTANCE, data);
        Optional<INBT> optional = result.resultOrPartial(this::logError);
        optional.ifPresent(inbt -> {
            if (!(inbt instanceof CompoundNBT)) {
                Questing.LOGGER.error(MARKER, "Saved NBT must be Map! File: {}, Data: {}", filename, inbt);
                return;
            }
            Objects.requireNonNull(currentWorldDir, "Current world dir is not set for " + filename);
            File file = new File(currentWorldDir, filename);
            synchronized (lock) {
                Questing.LOGGER.debug(MARKER, "Writing data for file {}", filename);
                try {
                    if (!file.exists()) {
                        boolean dirs = file.getParentFile().mkdirs();
                        boolean create = file.createNewFile();
                        if (dirs)
                            Questing.LOGGER.debug(MARKER, "Initialized directory tree for {} file", file.getPath());
                        if (create)
                            Questing.LOGGER.debug(MARKER, "Created new data file {}", file.getPath());
                    }
                    CompoundNBT nbt = (CompoundNBT) inbt;
                    CompressedStreamTools.writeCompressed(nbt, file);
                    Questing.LOGGER.debug(MARKER, "Data saving finished for file {}", filename);
                } catch (IOException e) {
                    Questing.LOGGER.fatal(MARKER, "Unable to write data to {} file due to error {}", filename, e.getMessage());
                    if (!FMLLoader.isProduction()) {
                        Questing.LOGGER.fatal(MARKER, "Error stacktrace", e);
                    }
                }
            }
        });
    }

    public CompletableFuture<?> writeDataAsync(T data) {
        return CompletableFuture.runAsync(() -> writeData(data), IOHandler.INSTANCE.service);
    }

    public void setCurrentWorldDir(File currentWorldDir) {
        this.currentWorldDir = currentWorldDir;
    }

    private void logError(String message) {
        Questing.LOGGER.error(MARKER, message);
    }
}

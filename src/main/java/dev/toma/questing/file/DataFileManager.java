package dev.toma.questing.file;

import com.mojang.serialization.Codec;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.function.Supplier;

public final class DataFileManager<T> implements Supplier<T> {

    private static final List<DataFileManager<?>> MANAGERS = new ArrayList<>();
    private final DataFile<T> file;
    private T value;

    private DataFileManager(DataFile<T> file, T value) {
        this.file = file;
        this.value = value;
    }

    public static void setCurrentWorldDirectory(File file) {
        MANAGERS.forEach(mng -> mng.setTargetDir(file));
    }

    public static void forceWrite() {
        MANAGERS.forEach(DataFileManager::write);
    }

    public static void forceRead() {
        MANAGERS.forEach(DataFileManager::read);
    }

    public static <T> DataFileManager<T> create(String filename, Codec<T> codec, Supplier<T> value) {
        DataFileManager<T> fileManager = new DataFileManager<>(new DataFile<>(codec, filename), value.get());
        MANAGERS.add(fileManager);
        return fileManager;
    }

    public void read() {
        value = file.readData();
    }

    public Future<T> readAsync() {
        return IOHandler.INSTANCE.service.submit(() -> {
            T t = file.readData();
            value = t;
            return t;
        });
    }

    public void write() {
        file.writeData(value);
    }

    public Future<?> writeAsync() {
        return file.writeDataAsync(value);
    }

    @Override
    public T get() {
        return this.value;
    }

    private void setTargetDir(File dir) {
        file.setCurrentWorldDir(dir);
    }
}

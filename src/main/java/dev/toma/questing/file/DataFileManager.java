package dev.toma.questing.file;

import com.mojang.serialization.Codec;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public final class DataFileManager<V, T extends DataFileManager.DataHandler<V>> implements Supplier<T> {

    private static final List<DataFileManager<?, ?>> MANAGERS = new ArrayList<>();
    private final DataFile<V> file;
    private T value;

    private DataFileManager(DataFile<V> file, T value) {
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

    public static <V, T extends DataHandler<V>> DataFileManager<V, T> create(String filename, Codec<V> codec, Supplier<T> value) {
        DataFileManager<V, T> fileManager = new DataFileManager<>(new DataFile<>(codec, filename), value.get());
        MANAGERS.add(fileManager);
        return fileManager;
    }

    public void read() {
        V v = file.readData();
        this.value.loadData(v);
    }

    public CompletableFuture<?> readAsync() {
        return CompletableFuture.runAsync(() -> {
            V v = file.readData();
            value.loadData(v);
        }, IOHandler.INSTANCE.service);
    }

    public void write() {
        file.writeData(value.getSaveData());
    }

    public CompletableFuture<?> writeAsync() {
        return file.writeDataAsync(value.getSaveData());
    }

    @Override
    public T get() {
        return this.value;
    }

    private void setTargetDir(File dir) {
        file.setCurrentWorldDir(dir);
    }

    public interface DataHandler<V> {

        void loadData(V data);

        V getSaveData();
    }
}

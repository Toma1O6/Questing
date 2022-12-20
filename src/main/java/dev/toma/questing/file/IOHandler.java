package dev.toma.questing.file;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class IOHandler {

    public static final IOHandler INSTANCE = new IOHandler();
    public final ExecutorService service;

    private IOHandler() {
        this.service = Executors.newSingleThreadExecutor(runnable -> new Thread(runnable, "IO"));
    }
}

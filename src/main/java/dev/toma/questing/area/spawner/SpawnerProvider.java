package dev.toma.questing.area.spawner;

import java.util.function.Supplier;

public interface SpawnerProvider<S extends Spawner> extends Supplier<S> {
}

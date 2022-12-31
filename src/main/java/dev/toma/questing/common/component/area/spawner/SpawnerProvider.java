package dev.toma.questing.common.component.area.spawner;

import java.util.function.Supplier;

public interface SpawnerProvider<S extends Spawner> extends Supplier<S> {
}

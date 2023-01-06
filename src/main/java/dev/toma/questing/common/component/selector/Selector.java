package dev.toma.questing.common.component.selector;

import java.util.List;

public interface Selector<T> {

    SelectorType<?, ?> getType();

    List<T> getElements();
}

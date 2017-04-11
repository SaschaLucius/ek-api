package com.github.saschawiegleb.ek.api;

import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Parameter;

@Immutable
abstract class Category {

    static Category of(int id, String name) {
        return ImmutableCategory.of(id, name);
    }

    @Parameter(order = 1)
    abstract int id();

    @Parameter(order = 2)
    abstract String name();
}

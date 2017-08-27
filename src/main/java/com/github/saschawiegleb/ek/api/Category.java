package com.github.saschawiegleb.ek.api;

import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Parameter;

@Immutable(builder = false)
public abstract class Category {
    private static final Category NONE = Category.of(0, "Alle Kategorien");

    public static Category none() {
        return NONE;
    }

    public static Category of(int id, String name) {
        return ImmutableCategory.of(id, name);
    }

    @Parameter(order = 1)
    public abstract int id();

    @Parameter(order = 2)
    public abstract String name();
}

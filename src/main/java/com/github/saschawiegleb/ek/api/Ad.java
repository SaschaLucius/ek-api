package com.github.saschawiegleb.ek.api;

import java.net.URL;
import java.time.ZonedDateTime;

import org.immutables.value.Value.Default;
import org.immutables.value.Value.Immutable;

import javaslang.collection.HashMap;
import javaslang.collection.List;
import javaslang.collection.Map;
import javaslang.control.Either;

@Immutable
public abstract class Ad {

    @Default
    public Map<String, String> additionalDetails() {
        return HashMap.empty();
    }

    @Default
    public Category category() {
        return Category.of(0, "All");
    }

    @Default
    public String description() {
        return "";
    }

    @Default
    public String headline() {
        return "";
    }

    public abstract long id();

    @Default
    public List<String> images() {
        return List.empty();
    }

    public final URL link(Configuration configuration) {
        return configuration.resolvePath("s-anzeige/" + id()).get();
    }

    @Default
    public String location() {
        return "";
    }

    @Default
    public String price() {
        return "";
    }

    public final String searchString() {
        return headline().toLowerCase() + " " + description().toLowerCase();
    }

    @Default
    public Either<String, ZonedDateTime> time() {
        return Either.left("no time set");
    }

    @Default
    public String vendorId() {
        return "";
    }

    @Default
    public String vendorName() {
        return "";
    }
}

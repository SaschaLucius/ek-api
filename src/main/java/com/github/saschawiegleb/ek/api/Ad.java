package com.github.saschawiegleb.ek.api;

import java.net.URL;
import java.time.LocalDateTime;

import org.immutables.value.Value.Default;
import org.immutables.value.Value.Immutable;

import javaslang.collection.Map;
import javaslang.collection.Seq;
import javaslang.control.Either;

@Immutable
abstract class Ad {

    abstract Map<String, String> additionalDetails();

    abstract String category();

    @Default
    String description() {
        return "";
    }

    @Default
    String headline() {
        return "";
    }

    abstract long id();

    abstract Seq<String> images();

    final URL link() {
        // TODO side effect, must be removed
        return Configuration.defaults().resolvePath("s-anzeige/" + id()).get();
    }

    abstract String location();

    abstract String price();

    final String searchString() {
        return headline().toLowerCase() + " " + description().toLowerCase();
    }

    abstract Either<Throwable, LocalDateTime> time();

    abstract String vendorId();

    abstract String vendorName();
}

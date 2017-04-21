package com.github.saschawiegleb.ek.api;

import org.immutables.value.Value.Default;
import org.immutables.value.Value.Immutable;

@Immutable
abstract class Selector {

    static Selector of() {
        return ImmutableParserSettings.builder().build();
    }

    @Default
    String adEntryElement() {
        return ".aditem";
    }

    @Default
    String adEntryId() {
        return "data-adid";
    }
}

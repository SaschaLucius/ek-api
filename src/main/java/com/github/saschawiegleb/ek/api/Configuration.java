package com.github.saschawiegleb.ek.api;

import java.net.MalformedURLException;
import java.net.URL;

import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Parameter;

import javaslang.control.Try;

@Immutable
abstract class Configuration {

    private static final String baseUrl = new String(new byte[] {
        104, 116, 116, 112, 115, 58, 47, 47,
        119, 119, 119, 46, 101, 98, 97, 121,
        45, 107, 108, 101, 105, 110, 97, 110,
        122, 101, 105, 103, 101, 110, 46, 100,
        101, 47
    });

    static Configuration defaults() {
        return of(baseUrl);
    }

    static Configuration of(String baseUrl) {
        try {
            return of(new URL(baseUrl));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    static Configuration of(URL baseUrl) {
        return ImmutableConfiguration.of(baseUrl);
    }

    @Parameter
    abstract URL baseUrl();

    final Try<URL> resolvePath(String path) {
        return Try.of(() -> new URL(baseUrl(), path));
    }
}

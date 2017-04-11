package com.github.saschawiegleb.ek.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.NoSuchElementException;

import org.junit.Test;

import javaslang.control.Try;

public class ConfigurationTest implements DefaultConfiguration {

    @Test
    public void testDefaults_resolveCategory_failure() {
        assertThat(defaultConfiguration.category(999).getCause())
            .isInstanceOf(NoSuchElementException.class)
            .hasMessage("No value present");
    }

    @Test
    public void testDefaults_resolveCategory_success() {
        assertThat(defaultConfiguration.category(123))
            .isEqualTo(Try.of(() -> Category.of(123, "Sozialer Sektor & Pflege")));
    }
}

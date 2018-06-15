package com.github.saschawiegleb.ek.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.NoSuchElementException;

import org.junit.Test;

import com.github.saschawiegleb.ek.DefaultConfiguration;

public class ConfigurationTest implements DefaultConfiguration {

    @Test
    public void testDefaults_resolveCategory_failure() {
        // TODO
        assertThat(defaultConfiguration.category(999).getCause())
            .isInstanceOf(NoSuchElementException.class)
            .hasMessage("No value present");
    }
}

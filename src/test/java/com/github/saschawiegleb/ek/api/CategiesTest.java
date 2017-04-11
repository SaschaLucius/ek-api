package com.github.saschawiegleb.ek.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.Test;

public class CategiesTest {
	@Test
	public void testCategoriesForEnum() throws IOException {
		assertThat(Category.byId(123).getName()).isEqualTo("Sozialer Sektor & Pflege");
		Category.refreshCache();
	}
}

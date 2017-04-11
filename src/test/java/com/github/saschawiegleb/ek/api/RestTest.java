package com.github.saschawiegleb.ek.api;

import static org.assertj.core.api.Assertions.assertThat;

import org.jsoup.nodes.Document;
import org.junit.Test;

public class RestTest {

	@Test
	public void getTest() throws Exception {
		assertThat(Rest.get(Key.decrypt())).isNotEqualTo(new Document(Key.decrypt()));
	}
}

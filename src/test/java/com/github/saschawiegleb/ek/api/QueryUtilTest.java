package com.github.saschawiegleb.ek.api;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jsoup.nodes.Element;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class QueryUtilTest {

	Map<Long, Ad> list = new ConcurrentHashMap<Long, Ad>();

	@Test
	public void categoryTest() throws IOException {
		// URL
		String url = UrlHelper.getPageURL(Category.byId(245), 1, null);
		Map<Long, Element> listOfElements = QueryUtil.mapOfElements(url);
		assertThat(listOfElements).hasSize(27);
		List<Ad> listOfAds = QueryUtil.listOfAds(listOfElements);
		assertTrue(listOfAds.size() == 27);
	}

	@Test
	public void topTest() throws IOException {
		// URL
		String url = UrlHelper.getTopPageURL(Category.byId(245), 1);
		Map<Long, Element> listOfElements = QueryUtil.mapOfElements(url);
		assertThat(listOfElements.size()).isBetween(1, 27);
		List<Ad> listOfAds = QueryUtil.listOfAds(listOfElements);
		assertTrue(listOfAds.size() > 0);
	}
}
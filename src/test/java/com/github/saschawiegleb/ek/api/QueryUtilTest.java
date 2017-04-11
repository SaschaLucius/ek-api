package com.github.saschawiegleb.ek.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.jsoup.nodes.Element;
import org.junit.Test;

public class QueryUtilTest {

    @Test
    public void categoryTest() throws IOException {
        // URL
        URL url = UrlHelper.getPageURL(Category.byId(245), 1, null).get();
        Map<Long, Element> listOfElements = QueryUtil.mapOfElements(url);
        assertThat(listOfElements).hasSize(27);
        List<Ad> listOfAds = QueryUtil.listOfAds(listOfElements);
        assertTrue(listOfAds.size() == 27);
    }

    @Test
    public void topTest() throws IOException {
        // URL
        URL url = UrlHelper.getTopPageURL(Category.byId(245), 1).get();
        Map<Long, Element> listOfElements = QueryUtil.mapOfElements(url);
        assertThat(listOfElements.size()).isBetween(1, 27);
        List<Ad> listOfAds = QueryUtil.listOfAds(listOfElements);
        assertTrue(listOfAds.size() > 0);
    }
}

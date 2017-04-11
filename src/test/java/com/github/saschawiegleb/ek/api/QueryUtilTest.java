package com.github.saschawiegleb.ek.api;

import static org.assertj.core.api.Assertions.assertThat;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;

import javaslang.collection.Map;
import javaslang.collection.Seq;

public class QueryUtilTest implements DefaultConfiguration {

    @Test
    public void categoryTest() {
        Category category = defaultConfiguration.category(245).get();
        Document document = defaultConfiguration.pageDocument(category, 1).get();
        Map<Long, Element> listOfElements = QueryUtil.mapOfElements(document);
        assertThat(listOfElements).hasSize(27);
        Seq<Ad> listOfAds = QueryUtil.listOfAds(listOfElements);
        assertThat(listOfAds).hasSize(27);
    }

    @Test
    public void topPageTest() {
        Category category = defaultConfiguration.category(245).get();
        Document document = defaultConfiguration.topPageDocument(category, 1).get();
        Map<Long, Element> listOfElements = QueryUtil.mapOfElements(document);
        assertThat(listOfElements.size()).isBetween(1, 27);
        Seq<Ad> listOfAds = QueryUtil.listOfAds(listOfElements);
        assertThat(listOfAds.size()).isGreaterThan(0);
    }
}

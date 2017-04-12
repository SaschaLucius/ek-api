package com.github.saschawiegleb.ek.api;

import static org.assertj.core.api.Assertions.assertThat;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;

import javaslang.Tuple2;
import javaslang.collection.Map;

public class AdTest implements DefaultConfiguration {

    @Test
    public void testAdById() {
        Category category = defaultConfiguration.category(245).get();
        Document document = defaultConfiguration.pageDocument(category, 1).get();
        Map<Long, Element> listOfElements = Parser.parseElements(document);
        Tuple2<Long, Element> entry = listOfElements.head();
        Ad ad = Ad.byId(entry._1, entry._2);
        // TODO test
    }

    @Test
    public void testAdByIdExpired() {
        Ad ad = Ad.byId(428021741L, null);
        assertThat(ad.toString()).startsWith("Ad [headline=Artikel bereits verkauft., id=428021741, getLink()=");
    }

}

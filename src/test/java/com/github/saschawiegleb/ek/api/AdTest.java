package com.github.saschawiegleb.ek.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;

import org.jsoup.nodes.Element;
import org.junit.Test;

public class AdTest implements DefaultConfiguration {

    @Test
    public void testAdById() {
        Category category = defaultConfiguration.category(245).get();
        URL pageUrl = defaultConfiguration.pageUrl(category, 1).get();
        Map<Long, Element> listOfElements = QueryUtil.mapOfElements(pageUrl);
        Entry<Long, Element> entry = listOfElements.entrySet().iterator().next();
        Ad ad = Ad.byId(entry.getKey(), entry.getValue());
        // TODO test
    }

    @Test
    public void testAdByIdExpired() {
        Ad ad = Ad.byId(428021741L, null);
        assertThat(ad.toString()).startsWith("Ad [headline=Artikel bereits verkauft., id=428021741, getLink()=");
    }

}

package com.github.saschawiegleb.ek.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;

import org.jsoup.nodes.Element;
import org.junit.Test;

import javaslang.control.Try;

public class AdTest {

    @Test
    public void testAdById() {
        Try<URL> url = UrlHelper.getPageURL(Category.byId(245), 1, null);
        Map<Long, Element> listOfElements = QueryUtil.mapOfElements(url.get());
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

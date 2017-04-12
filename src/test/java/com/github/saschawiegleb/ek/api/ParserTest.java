package com.github.saschawiegleb.ek.api;

import static org.assertj.core.api.Assertions.assertThat;

import org.jsoup.nodes.Document;
import org.junit.Test;

import javaslang.collection.Seq;

public class ParserTest implements DefaultConfiguration {

    @Test
    public void ads_pageDocument() {
        Category category = defaultConfiguration.category(245).get();
        Document document = defaultConfiguration.pageDocument(category, 1).get();
        Seq<Ad> ads = Parser.ads(document);
        assertThat(ads).hasSize(27);
    }

    @Test
    public void ads_topPageDocument() {
        Category category = defaultConfiguration.category(245).get();
        Document document = defaultConfiguration.topPageDocument(category, 1).get();
        Seq<Ad> ads = Parser.ads(document);
        assertThat(ads.size()).isGreaterThan(0);
    }
}

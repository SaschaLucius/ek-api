package com.github.saschawiegleb.ek.parser;

import static org.assertj.core.api.Assertions.assertThat;

import org.jsoup.nodes.Document;
import org.junit.Test;

import com.github.saschawiegleb.ek.DefaultConfiguration;
import com.github.saschawiegleb.ek.entity.Ad;
import com.github.saschawiegleb.ek.entity.Category;
import com.github.saschawiegleb.ek.parser.Parser;

import javaslang.collection.Seq;

public class ParserTest implements DefaultConfiguration {

    @Test
    public void ads_pageDocument() {
        Category category = defaultConfiguration.category(245).get();
        Document document = defaultConfiguration.categoryDocument(category, 1).get();
        Seq<Ad> ads = Parser.of(defaultConfiguration).ads(document);
        assertThat(ads).hasSize(27);
    }

    @Test
    public void ads_readAdLeightweight() {
        Category category = defaultConfiguration.category(245).get();
        Document document = defaultConfiguration.categoryDocument(category, 1).get();
        Seq<Ad> ads = Parser.of(defaultConfiguration).adsLightweight(document);
        assertThat(ads.size()).isGreaterThanOrEqualTo(25);
    }

    @Test
    public void ads_topPageDocument() {
        Category category = defaultConfiguration.category(245).get();
        Document document = defaultConfiguration.topPageDocument(category, 1).get();
        Seq<Ad> ads = Parser.of(defaultConfiguration).ads(document);
        assertThat(ads.size()).isGreaterThan(0);
    }
}

package com.github.saschawiegleb.ek.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.jsoup.nodes.Document;
import org.junit.Test;

import javaslang.Tuple2;
import javaslang.collection.Map;
import javaslang.control.Either;

public class AdTest implements DefaultConfiguration {

    @Test
    public void testAdById() {
        Category category = defaultConfiguration.category(245).get();
        Document document = defaultConfiguration.pageDocument(category, 1).get();
        Map<Long, Either<String, LocalDateTime>> listOfElements = Parser.parseAdEntries(document);
        Tuple2<Long, Either<String, LocalDateTime>> entry = listOfElements.head();
        Ad ad = Parser.of(defaultConfiguration).readAd(entry._1, entry._2);
        System.err.println(ad);
        // TODO test
    }

    @Test
    public void testAdByIdAdditionalDetails() {
        Category category = defaultConfiguration.category(216).get();
        Document document = defaultConfiguration.pageDocument(category, 1).get();
        Map<Long, Either<String, LocalDateTime>> listOfElements = Parser.parseAdEntries(document);
        Tuple2<Long, Either<String, LocalDateTime>> entry = listOfElements.head();
        Ad ad = Parser.of(defaultConfiguration).readAd(entry._1, entry._2);
        System.err.println(ad);
        // TODO test
    }

    @Test
    public void testAdByIdExpired() {
        Ad ad = Parser.of(defaultConfiguration).readAd(428021741L, Either.left("no time set"));
        assertThat(ad).isEqualTo(ImmutableAd.builder().id(428021741L).headline("no longer available").build());
    }

}

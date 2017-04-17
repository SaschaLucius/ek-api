package com.github.saschawiegleb.ek.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

    @Test
    public void testRandomAdById() {
        List<Ad> ads = new ArrayList<>();
        long x = 600000000L;
        long y = 634606820L;
        Random r = new Random();

        for (int i = 0; i < 100; i++) {
            long number = x + (long) (r.nextDouble() * (y - x));
            Ad ad = Parser.of(defaultConfiguration).readAd(number, Either.left("no time set"));
            ads.add(ad);
        }

        List<Ad> availebleAds = new ArrayList<>();
        for (Ad ad : ads) {
            if (!ad.headline().isEmpty()) {
                availebleAds.add(ad);
            }
        }
        assertThat(availebleAds).isNotEmpty();
    }

}

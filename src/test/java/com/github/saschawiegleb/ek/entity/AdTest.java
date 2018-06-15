package com.github.saschawiegleb.ek.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.jsoup.nodes.Document;
import org.junit.Test;

import com.github.saschawiegleb.ek.DefaultConfiguration;
import com.github.saschawiegleb.ek.entity.Ad;
import com.github.saschawiegleb.ek.entity.Category;
import com.github.saschawiegleb.ek.parser.Parser;

import javaslang.Tuple2;
import javaslang.collection.Map;
import javaslang.control.Either;

public class AdTest implements DefaultConfiguration {

    @Test
    public void testAdByIdAdditionalDetails() {
        // Cars
        Category category = defaultConfiguration.category(216).get();
        Document document = defaultConfiguration.categoryDocument(category, 1).get();
        Map<Long, Either<String, ZonedDateTime>> listOfElements = Parser.of(defaultConfiguration).parseAdEntries(document);
        Tuple2<Long, Either<String, ZonedDateTime>> entry = listOfElements.head();
        Ad ad = Parser.of(defaultConfiguration).readAd(entry._1, entry._2);
        assertThat(ad.additionalDetails().keySet()).contains("Marke:", "Modell:");
    }

    @Test
    public void testAdByIdExpired() {
        Ad ad = Parser.of(defaultConfiguration).readAd(1L, Either.left("no time set"));
        assertThat(ad.headline()).isEqualTo("no longer available");
    }

    @Test
    public void testAdByIdOfOldestAd() {
        Ad ad = Parser.of(defaultConfiguration).readAd(7256, Either.left("no time set"));
        assertThat(ad.additionalDetails()).isNotNull();
        assertThat(ad.category().id()).isEqualTo(261);
        assertThat(ad.description()).startsWith("Karate und Selbstverteidigung sowie Stockkampf");
        assertThat(ad.headline()).isEqualTo("Karate, Stockkampf Arnis, Bo-Jutsu, Tai-Chi, Jiu-Jitsu, Iaido");
        assertThat(ad.id()).isEqualTo(7256L);
        assertThat(ad.images().size()).isEqualTo(6);
        assertThat(ad.location()).isEqualTo("79108 Baden-Württemberg - Freiburg");
        assertThat(ad.price()).isNotEmpty();
        assertThat(ad.searchString()).isNotEmpty();
        assertThat(ad.time()).isNotEmpty();
        assertThat(ad.vendorId()).isEqualTo("158583");
        assertThat(ad.vendorName()).isEqualTo("Adrian Kempf");
    }

    @Test
    public void testRandomAdById() {
        // get latest add
        Document document = defaultConfiguration.categoryDocument(Category.none(), 1).get();
        Map<Long, Either<String, ZonedDateTime>> listOfElements = Parser.of(defaultConfiguration).parseAdEntries(document);
        Tuple2<Long, Either<String, ZonedDateTime>> entry = listOfElements.head();
        Ad latest = Parser.of(defaultConfiguration).readAd(entry._1, entry._2);

        List<Ad> ads = new ArrayList<>();
        long x = latest.id() - 5000;
        long y = latest.id();
        Random r = new Random();

        for (int i = 0; i < 5; i++) {
            long number = x + (long) (r.nextDouble() * (y - x));
            Ad ad = Parser.of(defaultConfiguration).readAd(number, Either.left("no time set"));
            ads.add(ad);
        }

        List<Ad> availebleAds = new ArrayList<>();
        for (Ad ad : ads) {
            if (!ad.headline().equals("no longer available")) {
                availebleAds.add(ad);
            }
        }
        assertThat(availebleAds).isNotEmpty();
    }
}

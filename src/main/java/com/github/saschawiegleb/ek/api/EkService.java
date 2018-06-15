package com.github.saschawiegleb.ek.api;

import org.jsoup.nodes.Document;

import com.github.saschawiegleb.ek.entity.Ad;
import com.github.saschawiegleb.ek.entity.Category;
import com.github.saschawiegleb.ek.entity.Configuration;
import com.github.saschawiegleb.ek.parser.Parser;

import javaslang.collection.List;
import javaslang.control.Either;

public class EkService {
    private Configuration defaultConfiguration = null;

    public EkService() {
        defaultConfiguration = Configuration.defaults();
    }

    public EkService(Configuration configuration) {
        defaultConfiguration = configuration;
    }

    public Ad getAd(long id) {
        return Parser.of(defaultConfiguration).readAd(id, Either.left("no time set"));
    }

    public List<Ad> getAds(Category category, int... pages) {
        List<Ad> ads = List.empty();
        if (pages == null || pages.length == 0) {
            for (int i = 50; i > 0; i--) {
                Document document = defaultConfiguration.categoryDocument(category, i).get();
                ads = ads.pushAll(defaultConfiguration.parse().ads(document));
            }
            return ads;
        }
        for (int i : pages) {
            Document document = defaultConfiguration.categoryDocument(category, i).get();
            ads = ads.pushAll(defaultConfiguration.parse().ads(document));
        }
        return ads;
    }

    public List<Ad> getAdsLightweight(Category category, int... pages) {
        List<Ad> ads = List.empty();
        if (pages == null || pages.length == 0) {
            for (int i = 50; i > 0; i--) {
                Document document = defaultConfiguration.categoryDocument(category, i).get();
                ads = ads.pushAll(defaultConfiguration.parse().adsLightweight(document));
            }
            return ads;
        }
        for (int i : pages) {
            Document document = defaultConfiguration.categoryDocument(category, i).get();
            ads = ads.pushAll(defaultConfiguration.parse().adsLightweight(document));
        }
        return ads;
    }

    public Category getCategory(int id) {
        return defaultConfiguration.category(id).get();
    }

    public Category getCategory(String name) {
        return defaultConfiguration.category(name).get();
    }

    public List<Category> getCategorys() {
        return defaultConfiguration.categories().get();
    }

    public List<Ad> getLatestAds(Category category, long lowerBound) {
        List<Ad> ads = List.empty();
        Document document = defaultConfiguration.categoryDocument(category, 1).get();
        ads = ads.pushAll(defaultConfiguration.parse().ads(document, lowerBound));
        return ads;
    }
}

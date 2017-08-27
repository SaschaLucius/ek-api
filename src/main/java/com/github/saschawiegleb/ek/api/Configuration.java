package com.github.saschawiegleb.ek.api;

import java.net.MalformedURLException;
import java.net.URL;

import org.immutables.value.Value.Default;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Lazy;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javaslang.collection.List;
import javaslang.control.Option;
import javaslang.control.Try;

@Immutable
abstract class Configuration {

    private static final String baseUrl = new String(new byte[] {
        104, 116, 116, 112, 115, 58, 47, 47,
        119, 119, 119, 46, 101, 98, 97, 121,
        45, 107, 108, 101, 105, 110, 97, 110,
        122, 101, 105, 103, 101, 110, 46, 100,
        101, 47
    });

    private static final String categoriesPath = new String(new byte[] {
        115, 45, 107, 97, 116, 101, 103, 111,
        114, 105, 101, 110, 46, 104, 116, 109,
        108
    });

    private static final int pageLimit = 50;

    static Configuration defaults() {
        return of(baseUrl, pageLimit);
    }

    static Configuration of(String baseUrl, int pagelimit) {
        try {
            return of(new URL(baseUrl), pagelimit);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    static Configuration of(URL base, int pagelimit) {
        return ImmutableConfiguration.builder()
            .baseUrl(base)
            .categoriesUrl(resolvePath(base, categoriesPath))
            .pageLimit(pagelimit)
            .build();
    }

    static URL resolvePath(URL base, String path) {
        try {
            return new URL(base, path);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    abstract URL baseUrl();

    @Lazy
    Try<List<Category>> categories() {
        return categoriesDocument().map(doc -> {
            List<Category> cats = List.empty();

            for (Element element : doc.select(".a-span-8 a")) {
                String cat[] = element.attr("href").split("/");
                String key = cat[cat.length - 1].substring(1);
                String value1 = cat[1].substring(2);
                String value2 = element.ownText();

                String test = value2.replaceAll(", ", "-");
                test = test.replaceAll(" & ", "-");
                test = test.replaceAll("ö", "oe");
                test = test.replaceAll("Ö", "Oe");
                test = test.replaceAll("ä", "ae");
                test = test.replaceAll("Ä", "Ae");
                test = test.replaceAll("ü", "ue");
                test = test.replaceAll("Ü", "Ue");
                test = test.replaceAll("--", "-");
                test = test.replaceAll("--", "-");
                test = test.replaceAll("  ", " ");
                test = test.replaceAll("  ", " ");
                test = test.replaceAll(" ", "-");

                if (!value1.equalsIgnoreCase(test) && !test.toLowerCase().contains(value1)) {
                    value2 = value1 + ": " + value2;
                }
                cats = cats.append(Category.of(Integer.valueOf(key), value2));
            }
            return cats;
        });
    }

    private Try<Document> categoriesDocument() {
        return Reader.requestDocument(categoriesUrl());
    }

    abstract URL categoriesUrl();

    final Try<Category> category(int id) {
        return categories().map(cs -> cs.find(c -> c.id() == id).get());
    }

    final Try<URL> globalSearchUrl(String search, int page) {
        return resolvePath("/s-seite:" + page + "/" + search + "/k0");
    }

    final Try<Document> pageDocument(Category category, int pageNumber) {
        return pageUrl(category, pageNumber, Option.none()).flatMap(url -> Reader.requestDocument(url));
    }

    final Try<Document> pageDocument(Category category, int pageNumber, String searchString) {
        return pageUrl(category, pageNumber, Option.of(searchString)).flatMap(url -> Reader.requestDocument(url));
    }

    abstract int pageLimit();

    private final Try<URL> pageUrl(Category category, int pageNumber, Option<String> searchString) {
        StringBuilder path = new StringBuilder();
        String add = "";
        if (pageNumber > 1) {
            if (pageNumber > pageLimit()) {
                pageNumber = pageLimit();
            }
            add += "seite:" + Integer.toString(pageNumber) + "/";
        }
        if (!searchString.getOrElse("").isEmpty()) {
            add += searchString + "/";
        }
        path.append(add).append("c").append(category.id());
        return resolvePath(path.toString());
    }

    final Try<URL> resolvePath(String path) {
        return Try.of(() -> new URL(baseUrl(), path));
    }

    @Default
    Selector selector() {
        return Selector.of();
    }

    final Try<Document> topPageDocument(Category category, int page) {
        return topPageUrl(category, page).flatMap(url -> Reader.requestDocument(url));
    }

    private final Try<URL> topPageUrl(Category category, int page) {
        StringBuilder path = new StringBuilder();
        String add = "topAds/";
        if (page > 1) {
            if (page > pageLimit()) {
                page = pageLimit();
            }
            add += "seite:" + Integer.toString(page) + "/";
        }
        path.append(add).append("c").append(category.id());
        return Configuration.defaults().resolvePath(path.toString());
    }
}

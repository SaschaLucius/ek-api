package com.github.saschawiegleb.ek.api;

import java.net.MalformedURLException;
import java.net.URL;

import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Lazy;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javaslang.collection.List;
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

    private static final String categoriesPath = new String(new byte[] { 115, 45, 107, 97, 116, 101, 103, 111, 114, 105, 101, 110, 46, 104, 116, 109, 108 });

    static Configuration defaults() {
        return of(baseUrl);
    }

    static Configuration of(String baseUrl) {
        try {
            return of(new URL(baseUrl));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    static Configuration of(URL base) {
        return ImmutableConfiguration.builder()
            .baseUrl(base)
            .categoriesUrl(resolvePath(base, categoriesPath))
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
            for (Element element : doc.getElementsByClass("l-row l-container-row").first().getElementsByTag("a")) {
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

    final Try<URL> resolvePath(String path) {
        return Try.of(() -> new URL(baseUrl(), path));
    };
}

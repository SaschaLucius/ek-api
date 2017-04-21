package com.github.saschawiegleb.ek.api;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.github.saschawiegleb.ek.api.ImmutableAd.Builder;

import javaslang.collection.HashMap;
import javaslang.collection.List;
import javaslang.collection.Map;
import javaslang.collection.Seq;
import javaslang.control.Either;

final class Parser {

    private static final Element defaultElement = new Element("DEFAULT");
    private static Elements defaultElements = new Elements();

    private final Configuration configuration;

    private Parser(Configuration configuration) {
        this.configuration = Objects.requireNonNull(configuration);
    }

    static Parser of(Configuration configuration) {
        return new Parser(configuration);
    }

    private static Elements selectAll(Element element, String selector) {
        if (element != null && selector != null) {
            Elements elements = element.select(selector);
            if (elements.size() > 0) {
                return elements;
            }
        }
        return defaultElements;
    }

    private static Element selectFirst(Element element, String selector) {
        Elements elements = selectAll(element, selector);
        if (elements.size() == 1) {
            return elements.first();
        }
        return defaultElement;
    }

    private static void setAdditionslDetails(Builder builder, Document document) {
        Elements keys = selectAll(document, "#viewad-details > section > dl dt");
        Elements values = selectAll(document, "#viewad-details > section > dl dd");

        Map<String, String> details = HashMap.empty();
        for (int i = 3; i < keys.size(); i++) {
            String key = keys.get(i).ownText();
            String value = values.get(i).ownText().trim();
            if (value.isEmpty() && values.get(i).child(0) != null) {
                value = values.get(i).child(0).ownText();
            }
            if (value.isEmpty() && values.get(i).child(0).child(0) != null) {
                value = values.get(i).child(0).child(0).ownText();
            }
            if (value.replaceAll(",", "").trim().isEmpty()) {
                value = "";
                for (Element e : selectAll(values.get(i), "a")) {
                    value += e.ownText() + ",";
                }
            }
            details = details.put(key, !value.isEmpty() ? value : values.get(i).child(0).ownText());
        }
        builder.additionalDetails(details);
    }

    private static void setDescription(Builder builder, Document document) {
        builder.description(selectFirst(document, "#viewad-description-text").ownText());
    }

    private static void setHeadline(Builder builder, Document document) {
        builder.headline(selectFirst(document, "#viewad-title").ownText());
    }

    private static void setImages(Builder builder, Document document) {
        if (!selectFirst(document, "#viewad-thumbnails").equals(defaultElement)) {
            Elements elements = selectAll(document, "#viewad-thumbnail-list img");
            List<String> images = List.empty();
            for (Element img : elements) {
                String link = img.attr("data-imgsrc").replaceFirst("_72", "_57");
                images = images.append(link);
            }
            builder.images(images);
        }
    }

    private static void setLocation(Builder builder, Document document) {
        builder.location(selectFirst(document, "#viewad-locality").ownText());
    }

    private static void setPrice(Builder builder, Document document) {

        Element element = selectFirst(document, "#viewad-price");
        if (!element.equals(defaultElement)) {
            builder.price(element.ownText().replaceAll("Preis: ", ""));
            return;
        }

        String category = selectFirst(document, "#viewad-main > meta").attr("content");
        if (category.equals("Tauschen") || category.equals("Zu verschenken") || category.equals("Verleihen")) {
            builder.price(category);
            return;
        }

        String jobs = selectFirst(document, "#vap-brdcrmb > a:nth-child(2) > span").ownText();
        if (jobs.equals("Jobs")) {
            builder.price("job");
            return;
        }

        builder.price("unknown");
    }

    private static Either<String, LocalDateTime> time(Element element) {
        try {
            // not available in TopAds
            List<String> time = List.of(element.select(".aditem-addon").first().ownText().split(","));
            LocalDateTime dateTime;
            if (time.head().equals("Heute")) {
                LocalTime t = LocalTime.parse(time.tail().head().trim());
                dateTime = t.atDate(LocalDate.now());
            } else if (time.head().equals("Gestern")) {
                LocalTime t = LocalTime.parse(time.tail().head().trim());
                dateTime = t.atDate(LocalDate.now().minusDays(1));
            } else {
                dateTime = LocalDate.parse(time.head().trim()).atStartOfDay();
            }
            return Either.right(dateTime);
        } catch (RuntimeException e) {
            return Either.left(e.getMessage());
        }
    }

    Seq<Ad> ads(Document document) {
        Map<Long, Either<String, LocalDateTime>> elementsById = parseAdEntries(document);
        return elementsById.map(entry -> readAd(entry._1, entry._2));
    }

    private boolean isAvailable(Document doc) {
        return doc.getElementById("viewad-adexpired") == null && doc.getElementById("home") == null;
    }

    private URL linkById(long id) {
        return configuration.resolvePath("s-anzeige/" + id).get();
    }

    private URL linkByUserId(String id) {
        if (id.contains("shop")) {
            return configuration.resolvePath(id).get();
        }
        return configuration.resolvePath("s-bestandsliste.html?userId=" + id).get();
    }

    Map<Long, Either<String, LocalDateTime>> parseAdEntries(Document document) {
        Map<Long, Either<String, LocalDateTime>> map = HashMap.empty();
        for (Element element : document.select(configuration.selector().adEntryElement())) {
            long id = Long.parseLong(element.attr(configuration.selector().adEntryId()));
            Either<String, LocalDateTime> time = time(element);
            map = map.put(id, time);
        }
        return map;
    }

    Ad readAd(long id, Either<String, LocalDateTime> time) {
        ImmutableAd.Builder builder = ImmutableAd.builder();
        builder.id(id);
        builder.time(time);
        // TODO side effect, must be removed
        Document doc = Reader.requestDocument(linkById(id)).get();
        if (!isAvailable(doc)) {
            builder.headline("no longer available");
            return builder.build();
        }

        setCategory(builder, doc);
        setHeadline(builder, doc);
        setPrice(builder, doc);
        setImages(builder, doc);
        setDescription(builder, doc);
        setVendor(builder, doc);
        setLocation(builder, doc);
        setAdditionslDetails(builder, doc);
        return builder.build();
    }

    Ad readAdSmall(long id, Either<String, LocalDateTime> time, Element element) {
        ImmutableAd.Builder builder = ImmutableAd.builder();
        builder.id(id);
        builder.time(time);
        builder.headline(selectFirst(element, "article > section.aditem-main > h2 > a").ownText());
        builder.description(selectFirst(element, "article > section.aditem-main > p:nth-child(2)").ownText());
        builder.price(selectFirst(element, "article > section.aditem-details > strong").ownText());
        builder.location(selectFirst(element, "article > section.aditem-details").ownText());
        builder.time(time(element));
        // String img = selectFirst(element, "article > section.aditem-image
        // > div > img").attr("data-imgsrc");
        return builder.build();
    }

    private void setCategory(Builder builder, Document document) {
        String category[] = selectFirst(document, "#vap-brdcrmb > a:nth-last-child(1)").attr("href").split("/");
        if (category.length > 0 && !category[category.length - 1].isEmpty()) {
            int id = Integer.parseInt(category[category.length - 1].substring(1));
            builder.category(configuration.category(id).get());
        }
    }

    private void setVendor(Builder builder, Document document) {
        Element link = selectFirst(document, "#viewad-contact > div > ul > li:nth-child(1) > span > span.text-bold.text-bigger.text-force-linebreak > a");
        if (!link.equals(defaultElement)) {
            builder.vendorId(link.attr("href").replaceAll("/s-bestandsliste\\.html\\?userId=", ""));
            builder.vendorName(link.ownText());
            return;
        }
        link = selectFirst(document, "#poster-other-ads-link");
        if (!link.equals(defaultElement)) {
            String id = link.attr("href").replaceAll("/s-bestandsliste\\.html\\?userId=", "");
            builder.vendorId(id);
            Document userPage = Reader.requestDocument(linkByUserId(id)).get();
            Element user = selectFirst(userPage, "#site-content > div.l-splitpage > div.l-splitpage-navigation > section > header > h2");
            builder.vendorName(user.ownText());
            return;
        }
        System.out.println(document.baseUri());
    }

}

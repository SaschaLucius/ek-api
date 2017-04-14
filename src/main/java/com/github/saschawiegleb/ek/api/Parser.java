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
    private final Configuration configuration;

    private Parser(Configuration configuration) {
        this.configuration = Objects.requireNonNull(configuration);
    }

    static Parser of(Configuration configuration) {
        return new Parser(configuration);
    }

    static Map<Long, Either<String, LocalDateTime>> parseAdEntries(Document document) {
        Map<Long, Either<String, LocalDateTime>> map = HashMap.empty();
        for (Element element : document.select(".aditem")) {
            long id = Long.parseLong(element.attr("data-adid"));
            Either<String, LocalDateTime> time = time(element);
            map = map.put(id, time);
        }
        return map;
    }

    private static void setAdditionslDetails(Builder builder, Document document) {
        Elements keys = document.select("#viewad-details > section > dl dt");
        Elements values = document.select("#viewad-details > section > dl dd");

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
                for (Element e : values.get(i).select("a")) {
                    value += e.ownText() + ",";
                }
            }
            details.put(key, !value.isEmpty() ? value : values.get(i).child(0).ownText());
        }
        builder.additionalDetails(details);
    }

    private static void setDescription(Builder builder, Document document) {
        builder.description(document.select("#viewad-description-text").first().ownText());
    }

    private static void setHeadline(Builder builder, Document document) {
        builder.headline(document.select("#viewad-title").first().ownText());
    }

    private static void setImages(Builder builder, Document document) {
        if (document.select("#viewad-thumbnails") != null) {
            Elements elements = document.select("#viewad-thumbnail-list img");
            List<String> images = List.empty();
            for (Element img : elements) {
                String link = img.attr("data-imgsrc").replaceFirst("_72", "_57");
                images = images.append(link);
            }
            builder.images(images);
        }
    }

    private static void setLocation(Builder builder, Document document) {
        builder.location(document.select("#viewad-locality").first().ownText());
    }

    private static void setPrice(Builder builder, Document document) {
        builder.price(document.select("#viewad-price").first().ownText().replaceAll("Preis: ", ""));
    }

    private static void setVendor(Builder builder, Document document) {
        Element link = document.select("#viewad-contact > div > ul > li:nth-child(1) > span > span.text-bold.text-bigger.text-force-linebreak > a").first();
        builder.vendorId(link.attr("href").replaceAll("/s-bestandsliste\\.html\\?userId=", ""));
        builder.vendorName(link.ownText());
    }

    private static Either<String, LocalDateTime> time(Element element) {
        try {
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

    private void setCategory(Builder builder, Document document) {
        String category[] = document.select("#vap-brdcrmb > a:nth-last-child(1)").first().attr("href").split("/");
        int id = Integer.parseInt(category[category.length - 1].substring(1));
        builder.category(configuration.category(id).get());
    }

}

package com.github.saschawiegleb.ek.api;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatterBuilder;

import org.immutables.value.Value.Default;
import org.immutables.value.Value.Immutable;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.github.saschawiegleb.ek.api.ImmutableAd.Builder;

import javaslang.collection.List;
import javaslang.collection.Seq;
import javaslang.control.Either;

@Immutable
abstract class Ad {

    static Ad byId(long id, Element element) {
        ImmutableAd.Builder builder = ImmutableAd.builder();
        builder.id(id);
        // TODO side effect, must be removed
        Document doc = Reader.requestDocument(linkById(id)).get();

        if (doc.getElementById("viewad-adexpired") != null || doc.getElementById("home") != null) {
            builder.headline("no longer available");
            return builder.build();
        }

        setCategory(builder, doc);
        setHeadline(builder, doc);
        setPrice(builder, doc);
        setImages(builder, doc);
        setDescription(builder, doc);
        setVendor(builder, doc);

        Element details = getDetails(doc);
        // String price = details.getElementsByAttributeValue("itemprop",
        // "price").first().attr("content");
        // String currency = details.getElementsByAttributeValue("itemprop",
        // "currency").first().attr("content");
        // details.getElementsByClass("attributelist-striped").first().getElementsByTag("dt")
        setLocation(builder, details);
        setDate(element, builder, details);
        // String id =
        // details.getElementsByClass("attributelist-striped").first().getElementsByTag("dd").get(2).ownText();
        // details.getElementsByAttributeValue("itemprop", "price");

        return builder.build();
    }

    private static Element getDetails(Document document) {
        return document.getElementById("viewad-details").getElementsByClass("l-container").first();
    }

    static URL linkById(long id) {
        // TODO side effect, must be remove
        return Configuration.defaults().resolvePath("s-anzeige/" + id).get();
    }

    private static void setCategory(Builder builder, Document document) {
        String category[] = document.getElementById("vap-brdcrmb").getElementsByClass("breadcrump-link").last().attr("href").split("/");
        builder.category(category[category.length - 1].substring(1));
    }

    private static void setDate(Element adFromList, Builder builder, Element details) {
        try {
            String date = details.getElementsByClass("attributelist-striped").first().getElementsByTag("dd").get(1).ownText();
            String time[] = adFromList.getElementsByClass("aditem-addon").first().ownText().split(",");
            String timee = time[time.length - 1].trim();
            LocalDateTime dateTime = LocalDateTime.parse(date + " " + timee, new DateTimeFormatterBuilder().appendPattern("dd.MM.yyyy HH:mm").toFormatter());
            builder.time(Either.right(dateTime));
        } catch (Exception e) {
            builder.time(Either.left(e));
        }
    }

    private static void setDescription(Builder builder, Document document) {
        builder.description(document.getElementById("viewad-description-text").ownText());
    }

    private static void setHeadline(Builder builder, Document document) {
        Element title = document.getElementById("viewad-title");
        builder.headline(title.ownText());
    }

    private static void setImages(Builder builder, Document document) {
        if (document.getElementById("viewad-thumbnails") != null) {
            Elements elements = document.getElementById("viewad-thumbnails").getElementsByTag("img");
            List<String> images = List.empty();
            for (Element img : elements) {
                String link = img.attr("data-imgsrc").replaceFirst("_72", "_57");
                images = images.append(link);
            }
            builder.images(images);
        }
    }

    private static void setLocation(Builder builder, Element details) {
        String ort = details
            .getElementsByClass("attributelist-striped").first()
            .getElementsByTag("dd").first()
            .getElementById("viewad-locality")
            .ownText();
        builder.location(ort);
    }

    private static void setPrice(Builder builder, Document document) {
        Element price = document.getElementById("viewad-price");
        builder.price(price.ownText());
    }

    private static void setVendor(Builder builder, Document document) {
        Element link = document
            .getElementById("viewad-contact")
            .getElementsByClass("iconlist-text").first()
            .getElementsByTag("a").first();
        builder.vendorId(link.attr("href").replaceAll("/s-bestandsliste\\.html\\?userId=", ""));
        builder.vendorName(link.ownText());
    }

    abstract String category();

    @Default
    String description() {
        return "";
    }

    @Default
    String headline() {
        return "";
    }

    abstract long id();

    abstract Seq<String> images();

    final URL link() {
        return linkById(id());
    }

    abstract String location();

    abstract String price();

    final String searchString() {
        return headline().toLowerCase() + " " + description().toLowerCase();
    }

    abstract Either<Throwable, LocalDateTime> time();

    abstract String vendorId();

    abstract String vendorName();
}

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
        setLocation(builder, doc);
        setDate(element, builder, doc);
        return builder.build();
    }

    private static Elements getDetails(Document document) {
        return document.select("#viewad-details > section > dl");
    }

    static URL linkById(long id) {
        // TODO side effect, must be remove
        return Configuration.defaults().resolvePath("s-anzeige/" + id).get();
    }

    private static void setCategory(Builder builder, Document document) {
        String category[] = document.select("#vap-brdcrmb").first().getElementsByClass("breadcrump-link").last().attr("href").split("/");
        builder.category(category[category.length - 1].substring(1));
    }

    private static void setDate(Element adFromList, Builder builder, Document document) {
        try {
            String date = document.select("#viewad-details > section > dl > dd:nth-child(4)").first().ownText();
            String time[] = adFromList.getElementsByClass("aditem-addon").first().ownText().split(",");
            String timee = time[time.length - 1].trim();
            LocalDateTime dateTime = LocalDateTime.parse(date + " " + timee, new DateTimeFormatterBuilder().appendPattern("dd.MM.yyyy HH:mm").toFormatter());
            builder.time(Either.right(dateTime));
        } catch (Exception e) {
            builder.time(Either.left(e));
        }
    }

    private static void setDescription(Builder builder, Document document) {
        builder.description(document.select("#viewad-description-text").first().ownText());
    }

    private static void setHeadline(Builder builder, Document document) {
        Element title = document.select("#viewad-title").first();
        builder.headline(title.ownText());
    }

    private static void setImages(Builder builder, Document document) {
        if (document.select("#viewad-thumbnails") != null) {
            Elements elements = document.select("#viewad-thumbnails").first().getElementsByTag("img");
            List<String> images = List.empty();
            for (Element img : elements) {
                String link = img.attr("data-imgsrc").replaceFirst("_72", "_57");
                images = images.append(link);
            }
            builder.images(images);
        }
    }

    private static void setLocation(Builder builder, Document document) {
        String ort = document.select("#viewad-locality").first().ownText();
        builder.location(ort);
    }

    private static void setPrice(Builder builder, Document document) {
        Element price = document.select("#viewad-price").first();
        builder.price(price.ownText().replaceAll("Preis: ", ""));
    }

    private static void setVendor(Builder builder, Document document) {
        Element link = document.select("#viewad-contact > div > ul > li:nth-child(1) > span > span.text-bold.text-bigger.text-force-linebreak > a").first();
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

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

    Seq<Ad> ads(Document adList) {
        Map<Long, Either<String, LocalDateTime>> elementsById = parseAdEntries(adList);
        return elementsById.map(entry -> readAd(entry._1, entry._2));
    }

    private boolean isAvailable(Document adPage) {
        return adPage.select(configuration.selector().adPageExpiredMessage()).isEmpty() && adPage.select(configuration.selector().homeContent()).isEmpty();
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

    Map<Long, Either<String, LocalDateTime>> parseAdEntries(Document adList) {
        Map<Long, Either<String, LocalDateTime>> map = HashMap.empty();
        for (Element adListEntry : adList.select(configuration.selector().adListEntryElement())) {
            long id = Long.parseLong(adListEntry.attr(configuration.selector().adListEntryId()));
            Either<String, LocalDateTime> time = time(adListEntry);
            map = map.put(id, time);
        }
        return map;
    }

    Ad readAd(long id, Either<String, LocalDateTime> time) {
        ImmutableAd.Builder builder = ImmutableAd.builder();
        builder.id(id);
        builder.time(time);
        // TODO side effect, must be removed
        Document adPage = Reader.requestDocument(linkById(id)).get();
        if (!isAvailable(adPage)) {
            builder.headline("no longer available");
            return builder.build();
        }

        setCategory(builder, adPage);
        setHeadline(builder, adPage);
        setPrice(builder, adPage);
        setImages(builder, adPage);
        setDescription(builder, adPage);
        setVendor(builder, adPage);
        setLocation(builder, adPage);
        setAdditionalDetails(builder, adPage);
        return builder.build();
    }

    Ad readAdSmall(long id, Either<String, LocalDateTime> time, Element adListEntry) {
        ImmutableAd.Builder builder = ImmutableAd.builder();
        builder.id(id);
        builder.time(time);
        builder.headline(selectFirst(adListEntry, configuration.selector().adListEntryHeadline()).ownText());
        builder.description(selectFirst(adListEntry, configuration.selector().adListEntryDescription()).ownText());
        builder.price(selectFirst(adListEntry, configuration.selector().adListEntryPrice()).ownText());
        builder.location(selectFirst(adListEntry, configuration.selector().adListEntryLocation()).ownText());
        builder.time(time(adListEntry));
        // String img = selectFirst(adListEntry, "article > section.aditem-image
        // > div > img").attr("data-imgsrc");
        return builder.build();
    }

    private void setAdditionalDetails(Builder builder, Document adPage) {
        Elements keys = selectAll(adPage, configuration.selector().adPageAdditionalDetailsKeys());
        Elements values = selectAll(adPage, configuration.selector().adPageAdditionalDetailsValues());

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
                for (Element listEntry : selectAll(values.get(i), "a")) {
                    value += listEntry.ownText() + ",";
                }
            }
            details = details.put(key, !value.isEmpty() ? value : values.get(i).child(0).ownText());
        }
        builder.additionalDetails(details);
    }

    private void setCategory(Builder builder, Document adPage) {
        String category[] = selectFirst(adPage, configuration.selector().adPageCategory()).attr(configuration.selector().adPageCategoryLinkAttribute()).split("/");
        if (category.length > 0 && !category[category.length - 1].isEmpty()) {
            int id = Integer.parseInt(category[category.length - 1].substring(1));
            builder.category(configuration.category(id).get());
        }
    }

    private void setDescription(Builder builder, Document adPage) {
        builder.description(selectFirst(adPage, configuration.selector().adPageDescription()).ownText());
    }

    private void setHeadline(Builder builder, Document adPage) {
        builder.headline(selectFirst(adPage, configuration.selector().adPageHeadline()).ownText());
    }

    private void setImages(Builder builder, Document adPage) {
        if (!selectFirst(adPage, configuration.selector().adPageImagesAvailable()).equals(defaultElement)) {
            Elements images = selectAll(adPage, configuration.selector().adPageImages());
            List<String> imageLinks = List.empty();
            for (Element img : images) {
                String link = img.attr(configuration.selector().adPageImagesLinkAttribute()).replaceFirst("_72", "_57");
                imageLinks = imageLinks.append(link);
            }
            builder.images(imageLinks);
        }
    }

    private void setLocation(Builder builder, Document adPage) {
        builder.location(selectFirst(adPage, configuration.selector().adPageLocation()).ownText());
    }

    private void setPrice(Builder builder, Document adPage) {
        Element price = selectFirst(adPage, configuration.selector().adPagePrice());
        if (!price.equals(defaultElement)) {
            builder.price(price.ownText().replaceAll("Preis: ", ""));
            return;
        }

        String category = selectFirst(adPage, "#viewad-main > meta").attr("content");
        if (category.equals("Tauschen") || category.equals("Zu verschenken") || category.equals("Verleihen")) {
            builder.price(category);
            return;
        }

        String jobs = selectFirst(adPage, "#vap-brdcrmb > a:nth-child(2) > span").ownText();
        if (jobs.equals("Jobs")) {
            builder.price("job");
            return;
        }

        builder.price("unknown");
    }

    private void setVendor(Builder builder, Document adPage) {
        Element userPageLink = selectFirst(adPage, configuration.selector().adPageVendor());
        if (!userPageLink.equals(defaultElement)) {
            builder.vendorId(userPageLink.attr(configuration.selector().adPageVendorLinkAttribute()).replaceAll("/s-bestandsliste\\.html\\?userId=", ""));
            builder.vendorName(userPageLink.ownText());
            return;
        }
        userPageLink = selectFirst(adPage, configuration.selector().adPageVendorOtherAds());
        if (!userPageLink.equals(defaultElement)) {
            String id = userPageLink.attr(configuration.selector().adPageVendorLinkAttribute()).replaceAll("/s-bestandsliste\\.html\\?userId=", "");
            builder.vendorId(id);
            // TODO side effect, must be removed
            Document userPage = Reader.requestDocument(linkByUserId(id)).get();
            Element user = selectFirst(userPage, configuration.selector().userPageUsername());
            builder.vendorName(user.ownText());
            return;
        }
        userPageLink = selectFirst(adPage, configuration.selector().adPageVendorShopOtherAds());
        if (!userPageLink.equals(defaultElement)) {
            String id = userPageLink.attr(configuration.selector().adPageVendorLinkAttribute()).replaceAll("/s-bestandsliste\\.html\\?userId=", "");
            builder.vendorId(id);
            // TODO side effect, must be removed
            Document userPage = Reader.requestDocument(linkByUserId(id)).get();
            Element user = selectFirst(userPage, configuration.selector().userPageShopName());
            builder.vendorName(user.ownText().trim());
            return;
        }
        System.out.println(adPage.baseUri());
    }

    private Either<String, LocalDateTime> time(Element adListEntry) {
        try {
            // not available in TopAds
            List<String> time = List.of(adListEntry.select(configuration.selector().adListEntryTime()).first().ownText().split(","));
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

}

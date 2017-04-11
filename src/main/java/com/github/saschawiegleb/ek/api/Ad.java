package com.github.saschawiegleb.ek.api;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Ad {

    public static Ad byElement(String id, Element backupElement) {
        Ad element = new Ad();
        element.setId(id);
        return element;
    }

    public static Ad byId(Long id, Element element) {
        return byId(id.toString(), element);
    }

    public static Ad byId(String id, Element backupElement) {
        id = id.split("/")[id.split("/").length - 1]; // works for "id" and
                                                      // "user-xxx-id"
        Ad element = new Ad();
        element.setId(id);
        Document doc = Reader.requestDocument(linkById(id)).get();

        if (doc.getElementById("viewad-adexpired") != null || doc.getElementById("home") != null) {
            System.out.println("already expired");
            if (backupElement != null) {
                return byElement(id, backupElement);
            } else {
                element.setHeadline("Artikel bereits verkauft.");
                return element;
            }
        }

        setCategory(element, doc);
        setHeadline(element, doc);
        setPrice(element, doc);
        setImages(element, doc);
        setDescription(element, doc);
        setVendor(element, doc);

        Element details = getDetails(doc);
        // String price = details.getElementsByAttributeValue("itemprop",
        // "price").first().attr("content");
        // String currency = details.getElementsByAttributeValue("itemprop",
        // "currency").first().attr("content");
        // details.getElementsByClass("attributelist-striped").first().getElementsByTag("dt")
        setLocation(element, details);
        setDate(backupElement, element, details);
        // String id =
        // details.getElementsByClass("attributelist-striped").first().getElementsByTag("dd").get(2).ownText();
        // details.getElementsByAttributeValue("itemprop", "price");

        return element;
    }

    private static Element getDetails(Document document) {
        return document.getElementById("viewad-details").getElementsByClass("l-container").first();
    }

    public static String linkById(String id) {
        return Key.decrypt() + "s-anzeige/" + id;
    }

    private static void setCategory(Ad ad, Document document) {
        String category[] = document.getElementById("vap-brdcrmb").getElementsByClass("breadcrump-link").last().attr("href")
            .split("/");
        ad.setCategory(category[category.length - 1].substring(1));
    }

    private static void setDate(Element adFromList, Ad ad, Element details) {
        String date = details.getElementsByClass("attributelist-striped").first().getElementsByTag("dd").get(1).ownText();
        String time[] = adFromList.getElementsByClass("aditem-addon").first().ownText().split(",");
        String timee = time[time.length - 1].trim();
        ad.setTime(date + " " + timee);
    }

    private static void setDescription(Ad ad, Document document) {
        ad.setDescription(document.getElementById("viewad-description-text").ownText());
    }

    private static void setHeadline(Ad ad, Document document) {
        Element title = document.getElementById("viewad-title");
        ad.setHeadline(title.ownText());
    }

    private static void setImages(Ad ad, Document document) {
        if (document.getElementById("viewad-thumbnails") != null) {
            Elements elements = document.getElementById("viewad-thumbnails").getElementsByTag("img");
            List<String> images = new ArrayList<>();
            for (Element img : elements) {
                String link = img.attr("data-imgsrc").replaceFirst("_72", "_57");
                images.add(link);
            }
            ad.setImages(images);
        }
    }

    private static void setLocation(Ad ad, Element details) {
        String ort = details.getElementsByClass("attributelist-striped").first().getElementsByTag("dd").get(0).getElementById("viewad-locality").ownText();
        ad.setLocation(ort);
    }

    private static void setPrice(Ad ad, Document document) {
        Element price = document.getElementById("viewad-price");
        ad.setPrice(price.ownText());
    }

<<<<<<< HEAD
    public static URL linkById(String id) {
        return Configuration.defaults().resolvePath("s-anzeige/" + id).get();
=======
    private static void setVendor(Ad ad, Document document) {
        ad.setVendorId(document.getElementById("viewad-contact").getElementsByClass("iconlist-text").first()
            .getElementsByTag("a").first().attr("href").replaceAll("/s-bestandsliste\\.html\\?userId=", ""));
        ad.setVendorName(document.getElementById("viewad-contact").getElementsByClass("iconlist-text").first()
            .getElementsByTag("a").first().ownText());
    }

    private String category;

    private String description = "";

    private String headline = "";

    private String id;

    private List<String> images;

    private String location;

    private String price;

    private boolean printed = false;

    private String subcategory;

    private String time;

    private String vendorId;

    private String vendorName;

    private Ad() {
>>>>>>> added more information to the Ads
    }

    public URL getLink() {
        return linkById(id);
    }

    public boolean isPrinted() {
        return printed;
    }

    public String searchString() {
        return headline.toLowerCase() + " " + description.toLowerCase();
    }

    private void setCategory(String category) {
        this.category = category;
    }

    private void setDescription(String description) {
        this.description = description;
    }

    private void setHeadline(String title) {
        this.headline = title;
    }

    private void setId(String id) {
        this.id = id;
    }

    private void setImages(List<String> images) {
        this.images = images;
    }

    private void setLocation(String location) {
        this.location = location;
    }

    private void setPrice(String price) {
        this.price = price;
    }

    public void setPrinted() {
        this.printed = true;
    }

    private void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
    }

    private void setTime(String time) {
        this.time = time;
    }

    private void setVendorId(String vendorId) {
        this.vendorId = vendorId;
    }

    private void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    @Override
    public String toString() {
        return "Ad [headline=" + headline + ", id=" + id + ", getLink()=" + getLink() + "]";
    }
}

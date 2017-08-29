package com.github.saschawiegleb.ek.api;

import org.immutables.value.Value.Default;
import org.immutables.value.Value.Immutable;

@Immutable
abstract class Selector {

    static Selector of() {
        return ImmutableSelector.builder().build();
    }

    @Default
    String adListEntryDescription() {
        return "article > section.aditem-main > p:nth-child(2)";
    }

    @Default
    String adListEntryElement() {
        return ".aditem";
    }

    @Default
    String adListEntryHeadline() {
        return "article > section.aditem-main > h2 > a";
    }

    @Default
    String adListEntryId() {
        return "data-adid";
    }

    @Default
    String adListEntryLocation() {
        return "article > section.aditem-details";
    }

    @Default
    String adListEntryPrice() {
        return "article > section.aditem-details > strong";
    }

    @Default
    String adListEntryTime() {
        return ".aditem-addon";
    }

    @Default
    String adPageAdditionalDetailsKeys() {
        return "#viewad-details > section > dl dt";
    }

    @Default
    String adPageAdditionalDetailsValues() {
        return "#viewad-details > section > dl dd";
    }

    @Default
    String adPageAttributes() {
        return ".attributelist-striped";
    }

    @Default
    String adPageCategory() {
        return "#vap-brdcrmb > a:nth-last-child(1)";
    }

    @Default
    String adPageCategoryLinkAttribute() {
        return "href";
    }

    @Default
    String adPageDescription() {
        return "#viewad-description-text";
    }

    @Default
    String adPageExpiredMessage() {
        return "#viewad-adexpired";
    }

    @Default
    String adPageHeadline() {
        return "#viewad-title";
    }

    @Default
    String adPageImages() {
        return "#viewad-thumbnail-list img";
    }

    @Default
    String adPageImagesAvailable() {
        return "#viewad-thumbnails";
    }

    @Default
    String adPageImagesLinkAttribute() {
        return "data-imgsrc";
    }

    @Default
    String adPageLocation() {
        return "#viewad-locality";
    }

    @Default
    String adPagePrice() {
        return "#viewad-price";
    }

    @Default
    String adPageVendor() {
        return "#viewad-contact > div > ul > li:nth-child(1) > span > span.text-bold.text-bigger.text-force-linebreak > a";
    }

    @Default
    String adPageVendorLinkAttribute() {
        return "href";
    }

    @Default
    String adPageVendorOtherAds() {
        return "#poster-other-ads-link";
    }

    @Default
    String adPageVendorShopOtherAds() {
        return "#viewad-bizteaser--preview > a";
    }

    @Default
    String homeContent() {
        return "#home";
    }

    @Default
    String userPageShopName() {
        return "#store-front-info-details > div > header > div > header > h1";
    }

    @Default
    String userPageUsername() {
        return "#site-content > div.l-splitpage > div.l-splitpage-navigation > section > header > h2";
    }
}

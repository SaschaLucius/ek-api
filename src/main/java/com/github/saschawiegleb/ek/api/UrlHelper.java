package com.github.saschawiegleb.ek.api;

import java.net.URL;

import javaslang.control.Try;

public class UrlHelper {
    private static final int PAGE_LIMIT = 50;

    public static Try<URL> getGlobalSearchURL(String search, int seite) {
        return Configuration.defaults().resolvePath("/s-seite:" + Integer.toString(seite) + "/" + search + "/k0");
    }

    public static Try<URL> getPageURL(Category category, int pageNumber, String searchString) {
        StringBuilder path = new StringBuilder();
        String add = "";
        if (pageNumber > 1) {
            if (pageNumber > PAGE_LIMIT) {
                pageNumber = PAGE_LIMIT;
            }
            add += "seite:" + Integer.toString(pageNumber) + "/";
        }
        if (searchString != null && !searchString.isEmpty()) {
            add += searchString + "/";
        }

        path.append(add).append("c").append(category.getId());
        return Configuration.defaults().resolvePath(path.toString());
    }

    public static Try<URL> getTopPageURL(Category category, int seite) {
        StringBuilder path = new StringBuilder();
        String add = "topAds/";
        if (seite > 1) {
            if (seite > PAGE_LIMIT) {
                seite = PAGE_LIMIT;
            }
            add += "seite:" + Integer.toString(seite) + "/";
        }
        path.append(add).append("c").append(category.getId());
        return Configuration.defaults().resolvePath(path.toString());
    }
}

package com.github.saschawiegleb.ek.api;

import java.net.URL;

import javaslang.control.Try;

public class UrlHelper {
    private static final int PAGE_LIMIT = 50;

    public static Try<URL> getGlobalSearchURL(String search, int seite) {
        return Configuration.defaults().resolvePath("/s-seite:" + Integer.toString(seite) + "/" + search + "/k0");
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
        path.append(add).append("c").append(category.id());
        return Configuration.defaults().resolvePath(path.toString());
    }
}

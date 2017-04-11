package com.github.saschawiegleb.ek.api;

import java.net.URL;

import javaslang.control.Try;

public class UrlHelper {

    public static Try<URL> getGlobalSearchURL(String search, int seite) {
        return Configuration.defaults().resolvePath("/s-seite:" + Integer.toString(seite) + "/" + search + "/k0");
    }
}

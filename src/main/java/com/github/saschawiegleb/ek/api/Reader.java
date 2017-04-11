package com.github.saschawiegleb.ek.api;

import java.net.URL;

import org.jsoup.Connection.Response;
import org.jsoup.helper.HttpConnection;
import org.jsoup.nodes.Document;

import javaslang.control.Try;

final class Reader {

    private static Try<Response> request(URL url) {
        return Try.of(() -> HttpConnection.connect(url).execute());
    }

    static Try<Document> requestDocument(URL url) {
        return request(url).mapTry(response -> response.parse());
    }
}

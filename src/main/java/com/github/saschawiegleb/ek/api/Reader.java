package com.github.saschawiegleb.ek.api;

import org.jsoup.Connection.Response;
import org.jsoup.helper.HttpConnection;
import org.jsoup.nodes.Document;

import javaslang.control.Try;

final class Reader {

    static Try<Response> request(String url) {
        return Try.of(() -> HttpConnection.connect(url).execute());
    }

    static Try<Document> requestDocument(String url) {
        return request(url).flatMap(response -> {
            String prefix = "Unknown response";
            if (response.statusCode() >= 500) {
                prefix = "Server error response";
            } else if (response.statusCode() >= 400) {
                prefix = "Client error response";
            } else if (response.statusCode() >= 300) {
                prefix = "Redirection message";
            } else if (response.statusCode() >= 200) {
                return Try.of(() -> response.parse());
            }
            String message = String.format("%s: %s: %s", prefix, response.statusCode(), response.body());
            return Try.failure(new RuntimeException(message));
        });
    }
}

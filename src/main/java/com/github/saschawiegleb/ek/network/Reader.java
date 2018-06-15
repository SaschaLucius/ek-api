package com.github.saschawiegleb.ek.network;

import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jsoup.Connection.Response;
import org.jsoup.HttpStatusException;
import org.jsoup.helper.HttpConnection;
import org.jsoup.nodes.Document;

import javaslang.control.Try;

public final class Reader {
    private static final Logger logger = Logger.getLogger(Reader.class.getName());

    private static Try<Response> request(URL url) {
        try {
            Response execute = HttpConnection.connect(url).execute();
            return Try.of(() -> execute);
        } catch (HttpStatusException e) {
            int statusCode = e.getStatusCode();
            while (statusCode == 429) {
                try {
                    Thread.sleep(1000);
                    Response execute = HttpConnection.connect(url).execute();
                    return Try.of(() -> execute);
                } catch (HttpStatusException e1) {
                    statusCode = e1.getStatusCode();
                } catch (Exception e1) {
                    logger.log(Level.WARNING, e.getMessage(), e);
                    return Try.failure(e);
                }
            }
            return Try.failure(e);
        } catch (Exception e) {
            logger.log(Level.WARNING, e.getMessage(), e);
            return Try.failure(e);
        }
    }

    public static Try<Document> requestDocument(URL url) {
        return request(url).mapTry(response -> response.parse());
    }
}

package com.github.saschawiegleb.ek.api;

import java.io.IOException;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Rest {
    static Document get(String url) {
        Document doc = new Document(url);
        try {
            Connection.Response response = Jsoup.connect(url).execute();
            if (response.statusCode() == 200) {
                doc = response.parse();
            } else {
                System.err.println("Error Code: " + response.statusCode());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return doc;
    }
}

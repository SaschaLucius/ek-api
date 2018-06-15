package com.github.saschawiegleb.ek.network;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.MalformedURLException;
import java.net.URL;

import org.jsoup.HttpStatusException;
import org.jsoup.nodes.Document;
import org.junit.Test;

import com.github.saschawiegleb.ek.network.Reader;

public class ReaderTest {

    @Test
    public void readDocument_200() throws MalformedURLException {
        URL url = new URL("http://ccc.de/");
        assertThat(Reader.requestDocument(url).get()).isInstanceOf(Document.class);
    }

    @Test
    public void readDocument_404() throws MalformedURLException {
        URL url = new URL("http://ccc.de/not-found");
        assertThat(Reader.requestDocument(url).getCause()).isInstanceOf(HttpStatusException.class).hasMessage("HTTP error fetching URL");
    }
}

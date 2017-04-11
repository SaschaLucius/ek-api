package com.github.saschawiegleb.ek.api;

import static org.assertj.core.api.Assertions.assertThat;

import org.jsoup.nodes.Document;
import org.junit.Test;

public class ReaderTest {

    @Test
    public void readDocument() {
        assertThat(Reader.requestDocument(Key.decrypt()).get()).isNotEqualTo(new Document(Key.decrypt()));
    }
}

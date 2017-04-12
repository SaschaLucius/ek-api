package com.github.saschawiegleb.ek.api;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javaslang.collection.HashMap;
import javaslang.collection.Map;
import javaslang.collection.Seq;

final class Parser {

    static Seq<Ad> ads(Document document) {
        Map<Long, Element> elementsById = parseElements(document);
        return elementsById.map(entry -> Ad.byId(entry._1, entry._2));
    }

    static Map<Long, Element> parseElements(Document document) {
        HashMap<Long, Element> map = HashMap.empty();
        for (Element element : document.select(".aditem")) {
            map = map.put(Long.parseLong(element.attr("data-adid")), element);
        }
        return map;
    }
}

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
        for (Element element : document.getElementsByClass("aditem")) {
            String split1[] = element.getElementsByAttribute("data-href").first().attr("data-href").split("/");
            String split2[] = split1[split1.length - 1].split("-");
            map = map.put(Long.parseLong(split2[0]), element);
        }
        return map;
    }
}

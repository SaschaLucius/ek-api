package com.github.saschawiegleb.ek.api;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javaslang.collection.HashMap;
import javaslang.collection.Map;
import javaslang.collection.Seq;

public class QueryUtil {

    public static String getTime() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(cal.getTime());
    }

    public static Seq<Ad> listOfAds(Map<Long, Element> ids) {
        return ids.map(entry -> Ad.byId(entry._1, entry._2));
    }

    static Map<Long, Element> mapOfElements(Document document) {
        HashMap<Long, Element> map = HashMap.empty();
        for (Element element : document.getElementsByClass("aditem")) {
            String split1[] = element.getElementsByAttribute("data-href").first().attr("data-href").split("/");
            String split2[] = split1[split1.length - 1].split("-");
            map = map.put(Long.parseLong(split2[0]), element);
        }
        return map;
    }
}

package com.github.saschawiegleb.ek.api;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class QueryUtil {

    public static String getTime() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(cal.getTime());
    }

    public static List<Ad> listOfAds(Map<Long, Element> ids) {
        // Elements
        List<Ad> ad = new ArrayList<>();
        for (Long id : ids.keySet()) {
            ad.add(Ad.byId(id, ids.get(id)));
        }
        return ad;
    }

    public static Map<Long, Element> mapOfElements(String url) {
        Map<Long, Element> elementList = new HashMap<>();
        Document doc = Reader.requestDocument(url).get();
        // list of Elements
        Elements elements = doc.getElementsByClass("aditem");
        // TODO Lists.newArrayList(Iterable)
        for (Element element : elements) {
            String split1[] = element.getElementsByAttribute("data-href").first().attr("data-href").split("/");
            String split2[] = split1[split1.length - 1].split("-");
            elementList.put(Long.parseLong(split2[0]), element);
        }
        return elementList;
    }
}

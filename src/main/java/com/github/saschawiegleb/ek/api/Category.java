package com.github.saschawiegleb.ek.api;

import java.util.HashMap;
import java.util.Map;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class Category {

    private static Map<Integer, String> _cache = new HashMap<>();
    static {
        refreshCache();
    }

    private Integer id;
    private String name;

    private Category(Integer id, String name) {
        super();
        this.id = id;
        this.name = name;
    }

    public static Category byId(Integer id) {
        return new Category(id, _cache.get(id));
    }

    public static void refreshCache() {
        _cache.clear();
        Document doc = Rest.get(Key.decrypt() + "s-kategorien.html");

        for (Element ele : doc.getElementsByClass("l-row l-container-row").first().getElementsByTag("a")) {
            String cat[] = ele.attr("href").split("/");

            String key = cat[cat.length - 1].substring(1);
            String value1 = cat[1].substring(2);
            String value2 = ele.ownText();

            String test = value2.replaceAll(", ", "-");
            test = test.replaceAll(" & ", "-");
            test = test.replaceAll("ö", "oe");
            test = test.replaceAll("Ö", "Oe");
            test = test.replaceAll("ä", "ae");
            test = test.replaceAll("Ä", "Ae");
            test = test.replaceAll("ü", "ue");
            test = test.replaceAll("Ü", "Ue");
            test = test.replaceAll("--", "-");
            test = test.replaceAll("--", "-");
            test = test.replaceAll("  ", " ");
            test = test.replaceAll("  ", " ");
            test = test.replaceAll(" ", "-");

            if (!value1.equalsIgnoreCase(test) && !test.toLowerCase().contains(value1)) {
                value2 = value1 + ": " + value2;
            }

            _cache.put(Integer.valueOf(key), value2);
        }
    }

    public Map<Integer, String> getAllCategories() {
        return _cache;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

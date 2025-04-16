package com.tech.blog.crawling.carrot;

import com.tech.blog.crawling.JsoupConfig;
import org.jsoup.select.Elements;

public class CarrotCrawling {
    public String getCarrotData() throws Exception {
        String viewerUrl = "https://medium.com/daangn";
        String query = "a";
        Elements selects = JsoupConfig.getJsoupElements(null, viewerUrl, query);

        return selects.first().text();
    }
}

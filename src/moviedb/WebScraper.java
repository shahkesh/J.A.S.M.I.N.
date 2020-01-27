package moviedb;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class WebScraper {

    public static String scrape() throws IOException {


        Document doc = Jsoup.connect("https://funfactz.com/tv-facts/random/").get();
        Elements facts = doc.getElementsByClass("fact_text");
        String fact = "Sorry, no fact today";

        if ( facts != null){
            for (Element element : facts) {
                fact = element.text();
                return fact;
            }
        }
        return fact;
    }
}

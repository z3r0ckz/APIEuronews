package Utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class StringUtils {

    public static String extractUrlFromHtml(String messageBody) {
        Document doc = Jsoup.parse(messageBody);
        return doc.select("a").attr("href");
    }

    public static boolean idContainsNewsletterName(String name, String id) {
        String cleanedUpId = id.substring(0, id.length() - 9).replace("-", " ");
        return org.apache.commons.lang3.StringUtils.containsIgnoreCase(name, cleanedUpId);
    }
}

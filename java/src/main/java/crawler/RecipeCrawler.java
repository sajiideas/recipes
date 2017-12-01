package crawler;

import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.*;

import org.apache.http.Header;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

public class RecipeCrawler extends WebCrawler {

    private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|gif|jpg"
                                                           + "|png|mp3|mp4|zip|gz))$");

    private  List<Map.Entry<String,String>> sb = new LinkedList<>();
    Pattern pattern = Pattern.compile("\"recipeIngredient\":\\[(.*?)\\]");
    /**
     * This method receives two parameters. The first parameter is the page
     * in which we have discovered this new url and the second parameter is
     * the new url. You should implement this function to specify whether
     * the given url should be crawled or not (based on your crawling logic).
     * In this example, we are instructing the crawler to ignore urls that
     * have css, js, git, ... extensions and to only accept urls that start
     * with "http://www.ics.uci.edu/". In this case, we didn't need the
     * referringPage parameter to make the decision.
     */
     @Override
     public boolean shouldVisit(Page referringPage, WebURL url) {
         String href = url.getURL().toLowerCase();
         boolean shouldVisit = !FILTERS.matcher(href).matches()
                && href.startsWith("http://");
         if (referringPage.getParseData() instanceof HtmlParseData) {
             HtmlParseData htmlParseData = (HtmlParseData) referringPage.getParseData();
             String patternMatched = getMatchedString(htmlParseData.getHtml());
             //System.out.println("Matched Pattern "+patternMatched);
             //shouldVisit = (patternMatched != null && patternMatched.trim().length() > 0);

          }
         return shouldVisit;
     }

     public synchronized String getMatchedString(String html){
       String matched = "";
       Matcher matcher = pattern.matcher(html);
       boolean hasFound = matcher.find();
       System.out.println("getMatchedString Matchers "+hasFound);
       if(hasFound){
         matched = matcher.group(1);
         System.out.println(" getMatchedString Matchers "+matched);
      }

       return matched;

     }

     /**
      * This function is called when a page is fetched and ready
      * to be processed by your program.
      */
     @Override
     public void visit(Page page) {
         String url = page.getWebURL().getURL();
         System.out.println("URL: " + url);

         if (page.getParseData() instanceof HtmlParseData) {
             HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
             String text = htmlParseData.getText();
             String html = htmlParseData.getHtml();
             Set<WebURL> links = htmlParseData.getOutgoingUrls();

             String patternMatched = getMatchedString(htmlParseData.getHtml());
             if(patternMatched != null && patternMatched.trim().length() > 0){
                this.sb.add(new AbstractMap.SimpleEntry<>(url,patternMatched));
             }

             //System.out.println("Text length: " + text.length());
             //System.out.println("Html length: " + html.length());
             //System.out.println("Number of outgoing links: " + links.size());



             //System.out.println("items in Map: "+this.sb.getKey());
         }
    }
    @Override
    public Object getMyLocalData() {
        return this.sb;
    }
}

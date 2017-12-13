package crawler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class RecipeCrawlerController{

  public static void main(String[] args) throws Exception {

        if(args.length < 2)
          System.out.println("Usage :  RecipeCrawlerController <data folder> <url>");

        String crawlStorageFolder = args[0];
        int numberOfCrawlers = 7;

        System.out.println("Data Dir "+crawlStorageFolder);


        CrawlConfig config = new CrawlConfig();
        config.setCrawlStorageFolder(crawlStorageFolder);
        config.setMaxPagesToFetch(9999);
        config.setPolitenessDelay(1000);
        config.setMaxDepthOfCrawling(10);


        /*
         * Instantiate the controller for this crawl.
         */
        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

        /*
         * For each crawl, you need to add some seed urls. These are the first
         * URLs that are fetched and then the crawler starts following links
         * which are found in these pages
         */
    	    controller.addSeed(args[1]);

        /*
         * Start the crawl. This is a blocking operation, meaning that your code
         * will reach the line after this only when crawling is finished.
         */
        controller.start(RecipeCrawler.class, numberOfCrawlers);


        List<Object> data  = controller.getCrawlersLocalData();
        System.out.println("Crawler Data count "+data.size());
        System.out.println("Type of data "+data.get(0).getClass().getCanonicalName());
        if(!data.isEmpty()){

            for(Object Obj : data){
              LinkedList<Map.Entry<String,String>> listData = (LinkedList)Obj;
              for(Map.Entry<String,String> entry : listData){
                System.out.println(entry.toString());

                try {
                      String key = entry.getKey();
                      String pageName = ((key.indexOf("/") > 0)?key.substring(key.lastIndexOf("/")):key);
                      String name = ((pageName != null && pageName.length() > 0)?pageName:"")+"_"+UUID.randomUUID().toString();//entry.getKey().replaceAll(File.pathSeparator,"_");
                      System.out.println("Writing "+name);
                      File fl = new File(crawlStorageFolder,name);
                      BufferedWriter writer = new BufferedWriter(new FileWriter(fl));
                      writer.write(entry.getKey());
                      writer.write("\n");
                      writer.write(entry.getValue());
                      writer.flush();
                      writer.close();

                  } catch (IOException e) {
                      e.printStackTrace();
                  }

              }

              //LinkedList l = (LinkedList)Obj;

              /*for(Iterator it = l.iterator(); it.hasNext();){
                Map.Entry<String,String> entry = it.next();
              System.out.println(dat.toSting());
              if(dat != null)  {
                try {
                      String key = entry.getKey();
                      String pageName = ((key.indexOf("/") > 0)?key.substring(key.lastIndexOf("/")):key);
                      String name = ((pageName != null && pageName.length() > 0)?pageName:"")+UUID.randomUUID().toString();//entry.getKey().replaceAll(File.pathSeparator,"_");
                      System.out.println("Writing "+name);
                      File fl = new File(crawlStorageFolder,name);
                      BufferedWriter writer = new BufferedWriter(new FileWriter(fl));
                      writer.write(entry.getKey());
                      writer.write("\n");
                      writer.write(entry.getValue());
                      writer.flush();
                      writer.close();

                  } catch (IOException e) {
                      e.printStackTrace();
                  }
            }
            */



          }

        }

    }
}

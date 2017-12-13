package parser;

/**
 * Created by sajimathew on 12/6/17.
 */
public class Helper {
   public static String emptyIfNull(String str){
        return (str !=null && str.trim().length() > 0)?str.trim():"";
    }

}

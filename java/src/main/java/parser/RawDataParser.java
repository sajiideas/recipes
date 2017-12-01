package parser;

import java.io.*;
import java.util.Arrays;
import java.util.stream.IntStream;

public class RawDataParser{
    private final File inputFolder;

    public RawDataParser(String inputFolder){
        this.inputFolder = new File(inputFolder);

        if(!this.inputFolder.exists()){
            throw new RuntimeException("Rawdata folder does not exist.");

        };



    }

    public void parse(String outputFolder){
        File[] fileList  =  this.inputFolder.listFiles();

        //Arrays.stream(this.inputFolder.listFiles()).forEach(f -> {
        IntStream.range(0,10).forEach(x -> {
                    File f = this.inputFolder.listFiles()[x];
                    try {

                        BufferedReader reader = new BufferedReader((new FileReader(f)));

                        String pageUrl = reader.readLine();
                        String ingredient = reader.readLine();

                        if(ingredient != null && ingredient.trim().length() > 0) {
                            //System.out.println(ingredient);


                            String[] ingList = ingredient.split("\",\"");
                            for (String ing : ingList) {

                                //System.out.println(ing);

                                ing = standardize(cleanup(ing));
                                System.out.println("cleanded :"+ing);

/*
                                int notePos = -1;
                                if(ing.contains(","))  notePos = ing.indexOf(",");
                                else if(ing.contains("("))  notePos = ing.indexOf("(");

                                String note = (notePos > 0)?ing.substring(notePos):"";
                                String ingre = (notePos > 0)?ing.substring(0,notePos):ing;

                                String[] item = ingre.split(" ");
*/

                                //System.out.println(String.join(",", item));
                                //System.out.println("Note :"+note);
                            }
                            System.out.println("=================");
                        }

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    ;
                }
        );
    }
    private final String cleanup(String str){
        str = str.replaceAll("\"","");
        str = str.replaceAll("  "," ");
        str = str.replaceAll("\\\\","");
        str = str.replaceAll(" /","/");
        str = str.replaceAll("/ ","/");
        return str;
    }
    private final String standardize(String str){
        str = str.replaceAll(" c\\."," cup");
        str = str.replaceAll(" t\\."," teaspoon");
        str = str.replaceAll(" tsp"," teaspoon");
        str = str.replaceAll(" tbsp"," tablespoon");
        str = str.replaceAll(" cups"," cup");
        str = str.replaceAll("oz","ounce");

        return str;
    }

/*

Feature extraction
    - measurement, unit, style, item , note
    - measurement can be multiple units  n xxxx, n/m xxxx, n (dd-xx) xxx. n
    - units can be shot or long form
    - item can be multiple  words
    - note can be any length of text.

 */

    public static void main(String[] args){
        String outputDir = "";
        RawDataParser parser = new RawDataParser("/Users/sajimathew/personal/sajiideas/recipes/data/rawdata/");
        parser.parse(outputDir);

    }
}

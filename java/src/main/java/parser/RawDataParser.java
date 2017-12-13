package parser;

import java.io.*;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class RawDataParser {
    private final File inputFolder;

    enum PARSE_SECTIONS {
        LEFT,
        CENTER,
        RIGHT
    }

    ;


    private static final Set<String> MEASUREMENTS = new HashSet<>(Arrays.asList(new String[]{"teaspoon"
            , "teaspoons"
            , "tablespoon"
            , "tablespoons"
            , "packages"
            , "cup"
            , "large"
            , "slice"
            , "lb"
            , "pounds"
            , "dashes"
            , "ounce"
            , "clove"
            , "can"
    }));

    private static final Pattern COUNT_PATTERN = Pattern.compile("^[0-9 ]+");
    private static final Pattern HAS_HTML_TAG = Pattern.compile("<.*?>");
    //private static final Pattern UNIT_SECONDARY_PATTERN = Pattern.compile("\\([0-9-/a-z.A-Z].*\\)|[0-9/]+");

    private List<RecipeObject> recipeList = new LinkedList<>();
    private String generatedScript = "";

    public RawDataParser(String inputFolder) {
        this.inputFolder = new File(inputFolder);

        if (!this.inputFolder.exists()) {
            throw new RuntimeException("Rawdata folder does not exist.");

        }
        ;


    }

    public void parse(String outputFolder) {
        File[] fileList = this.inputFolder.listFiles();


        //Arrays.stream(this.inputFolder.listFiles()).forEach(f -> {
        IntStream.range(0, 100).forEach(x -> {
                    File f = this.inputFolder.listFiles()[x];
                    try {

                        System.out.println(f.getName());
                        BufferedReader reader = new BufferedReader((new FileReader(f)));

                        RecipeObject recipeObject = new RecipeObject();

                        String url = reader.readLine();
                        recipeObject.setUrl(Helper.emptyIfNull(url));

                        recipeObject.setName(getNameFromUrl(recipeObject.getUrl()));

                        String ingredient = reader.readLine();

                        if (ingredient != null && ingredient.trim().length() > 0) {

                            String[] ingList = ingredient.split("\",\"");
                            for (String ing : ingList) {

                                RecipeObject.Ingredient ingredientItem = new RecipeObject.Ingredient();
                                //System.out.println(ing);

                                ing = standardize(cleanup(ing));
                                //System.out.println("cleanded :"+ing);

                                Map<PARSE_SECTIONS, String> parsedStr = getParsedString(ing);

                                ingredientItem.setUnits(parsedStr.get(PARSE_SECTIONS.LEFT));

                                ingredientItem.setMeasurement(parsedStr.get(PARSE_SECTIONS.CENTER));
                                String items = parsedStr.get(PARSE_SECTIONS.RIGHT);
                                if (items != null) {
                                    // System.out.println(parsedStr);

                                    int notePos = 999;

                                    if (items.contains("(")) notePos = items.indexOf("(");
                                    if (items.contains(",")) notePos = Math.min(notePos, items.indexOf(","));

                                    if (notePos < 999) {
                                        ingredientItem.setItem(items.substring(0, notePos));
                                        ingredientItem.setNotes(items.substring(notePos + 1, items.length()));
                                    } else {
                                        ingredientItem.setItem(items);
                                    }
                                    Matcher matcher = COUNT_PATTERN.matcher(ingredientItem.getItem());
                                    if (matcher.find()) {
                                        String countItem = ingredientItem.getItem();
                                        ingredientItem.setUnits(countItem.substring(0, matcher.end()));
                                        ingredientItem.setItem(countItem.substring(matcher.end()));
                                    }
                                }

                                if (ingredientItem.getItem() != null && ingredientItem.getItem().trim().length() > 0) {
                                    recipeObject.getIngredients().add(ingredientItem);
                                } else {
                                    System.out.println("INVALID Ingredient " + ingredientItem);
                                    break;
                                }
                                // System.out.println("Ingredient :"+ingredientItem);


                            }
                            // System.out.println("=================");
                        }

                        recipeList.add(recipeObject);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    ;
                }
        );
        // System.out.println("Recipes :"+recipeList);

    }

    private String getNameFromUrl(String url) {
        String[] split = url.split("/");
        String lastElement = split[split.length - 1];
        String possibleName = (lastElement != null && lastElement.trim().length() > 0)?lastElement:split[split.length - 2];

        return possibleName.replaceAll("-"," ");

    }
    public void  generateScript(String scriptType){

        if("neo4j".equalsIgnoreCase(scriptType)){
            try {
                this.generatedScript = ScriptMakerFactory.getNeo4jScriptMaker().generateScript(this.recipeList);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    public void persistScript(String location){

        System.out.print(this.generatedScript);
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


    private String[] tokenize(String text, String delim){
        return text.split(delim);
    }

    private Map<PARSE_SECTIONS,String> getParsedString(String text){
        Map<PARSE_SECTIONS,String> parsedStr = null;

        if(text != null && text.length() > 0){

            String[] delimed = tokenize(text," ");
            int i = 0;
            for(i =0; i < delimed.length; i++){

                if(MEASUREMENTS.contains(delimed[i])){
                    parsedStr = new HashMap<>();
                    parsedStr.put(PARSE_SECTIONS.LEFT,String.join(" ",Arrays.copyOfRange(delimed,0,i)));
                    parsedStr.put(PARSE_SECTIONS.CENTER,delimed[i]);
                    parsedStr.put(PARSE_SECTIONS.RIGHT,String.join(" ",Arrays.copyOfRange(delimed,i+1,delimed.length)));
                    break;
                }
            }
        }
        if(parsedStr == null){
            parsedStr = new HashMap<>();

            text = text.replaceAll("<.*?>","");
            parsedStr.put(PARSE_SECTIONS.RIGHT,text);
        }
        return parsedStr;
    }
   /* private void setUnits(String text, Ingredients ingredient){
        StringBuilder units = new StringBuilder();

        String[] delimed = tokenize(text," ");

        if(delimed.length > 0){

            String item_1 = delimed[0].trim();
            String item_2 = delimed[1].trim();

            Matcher matcher = UNIT_PRIMARY_PATTERN.matcher(item_1);
            if(matcher.find()) units.append(item_1);

            Matcher matcher2 = UNIT_SECONDARY_PATTERN.matcher(item_2);
            if(matcher2.find()) units.append(" ").append(item_2);


        }
        ingredient.units = units.toString();
    }*/
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
        parser.generateScript("neo4j");
        parser.persistScript("");
    }


}

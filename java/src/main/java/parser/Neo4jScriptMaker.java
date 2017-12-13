package parser;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;

import java.sql.*;
import java.util.*;

import static parser.Helper.emptyIfNull;

/**
 * Created by sajimathew on 12/5/17.
 */
public class Neo4jScriptMaker extends DbScriptMaker<RecipeObject>{


    private String recipeScript = "CREATE ( %s :Recipe{name:\"%s\",url:\"%s\"}) RETURN %s";
    private String ingredientScript = "CREATE ( %s :Ingredient{name:\"%s\"})";
    private String matchNode = "MATCH (r:Recipe{name:\"%s\"}),(n:Ingredient{name:\"%s\"}) ";
    private String relationsScript = "CREATE (%s)-[:USED_BY{units:'%s',measurement:\"%s\",notes:\"%s\"}]->(%s)";

    private Driver neo4jDriver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j","neo4j"));

    public Neo4jScriptMaker() throws SQLException {

    }
    @Override
    public String generateScript(List<RecipeObject> obj) {

        StringBuffer script   = new StringBuffer();


        Map<String,String> nodeMap = new HashMap<>();
        obj.stream().forEach(x ->{

            List<String> recipe = runStatement("MATCH (n:Recipe{name:'"+x.getName()+"'}) RETURN n.name");

            System.out.println("=========================================================================");
            if(recipe.isEmpty()) {

                try {

                    if(x.getIngredients().size() > 2) {
                        String newRecipe = String.format(recipeScript, "r", x.getName(), x.getUrl(), "r");
                        runStatement(newRecipe);
                        List<String> ingredients = runStatement("MATCH (n:Ingredient) RETURN n.name");
                        Set<String> existingIngredient = new HashSet<>(ingredients);



                        x.getIngredients().stream().forEach(y -> {
                            String node = "n" + removeSpaces(y.getItem());
                            if (!existingIngredient.contains(y.getItem().trim())) {
                                runStatement(String.format(ingredientScript, "n", y.getItem().trim()));
                            }
                            runStatement(String.format(matchNode + relationsScript, x.getName().trim(), y.getItem().trim(), "r", emptyIfNull(y.getUnits()), emptyIfNull(y.getMeasurement()), emptyIfNull(y.getNotes()), "n"));

                        });
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
        return script.toString();
    }
    private String removeSpaces(String str){
        String returnStr = "";
        if(str != null && str.trim().length() > 0){
            returnStr = str.replaceAll(" ","");
            returnStr = returnStr.replaceAll("-","");
            returnStr = returnStr.replaceAll("\\\\","");
            returnStr = returnStr.replaceAll("/","");
            returnStr = returnStr.replaceAll(":","");
            returnStr = returnStr.replaceAll("\\+","");
            returnStr = returnStr.replaceAll("'","");
            returnStr = returnStr.replaceAll("\\.","");
        }

        return returnStr;
    }


    private List<String> runStatement(String nodeText){

        System.out.println("CQL "+nodeText);
        List<String> nodeList = new LinkedList<>();

        try(Connection con = DriverManager.getConnection(
                "jdbc:neo4j:bolt://localhost/?user=neo4j,password=neo4j,scheme=basic")){
            try(PreparedStatement creatStmt = con.prepareStatement(nodeText)){

                ResultSet rs = creatStmt.executeQuery();
                while(rs.next()){
                    nodeList.add(rs.getString(1).trim());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        return nodeList;
    }


}

package parser;

import java.sql.SQLException;

/**
 * Created by sajimathew on 12/6/17.
 */
public class ScriptMakerFactory {

    public static Neo4jScriptMaker getNeo4jScriptMaker() throws SQLException {
        return new Neo4jScriptMaker();
    }
}

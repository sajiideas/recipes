package parser;

import java.io.File;
import java.util.List;

/**
 * Created by sajimathew on 12/5/17.
 */
public abstract class DbScriptMaker<T> {

    private T t;
    abstract String generateScript(List<T> obj);

    void setObject(T t){
        this.t = t;
    }

    T getObject(){
        return this.t;
    }

}

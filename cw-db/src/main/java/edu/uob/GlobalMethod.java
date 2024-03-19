package edu.uob;

import java.util.Arrays;
import java.util.HashSet;

public class GlobalMethod {
    private static String curDatabaseName = null;
    private static String curTableName = null;
    protected static HashSet<String> symbols;
    protected static HashSet<String> keyWords;
    protected static HashSet<Boolean> booleanLiteral;

    protected static HashSet<String> conditionAll;

    static {
        symbols = new HashSet<String>(Arrays.asList("!", "#", "$","%","&","(",")","*","+",",","-",".","/", ":",";",
                ">","=","<","?","@","[","\\","]","^","_","`","{","}","~"));
        keyWords = new HashSet<String>(Arrays.asList("USE","CREATE","DROP","ALTER","INSERT","SELECT","UPDATE",
                "DELETE","JOIN","TRUE","FALSE","DATABASE","TABLE","INTO","VALUES","FROM","WHERE","SET","AND","ON","ADD",
                "OR", "NULL","LIKE")); //24
        booleanLiteral = new HashSet<Boolean>(Arrays.asList(true,false));

        conditionAll = new HashSet<String>(Arrays.asList("==" , ">" , "<" , ">=" , "<=" , "!=" , " LIKE ","AND","OR"));
    }
    // Using for store current database name.
    public static void setCurDatabaseName(String databaseName) {
        curDatabaseName = databaseName;
    }
    public static String getCurDatabaseName() {
        return curDatabaseName;
    }
    public static void setCurTableName(String tableName) {
        curTableName = tableName;
    }
    public static String getCurTableName() {
        return curTableName;
    }

}

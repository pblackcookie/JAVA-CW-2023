package edu.uob;

import java.util.Arrays;
import java.util.HashSet;

public class GlobalMethod {
    private static String curDatabaseName = null;
    private static String curTableName = null;
    protected static HashSet<String> symbols;
    protected static HashSet<String> keyWords;
    public static HashSet<String> digits;

    static {
        symbols = new HashSet<String>(Arrays.asList("!", "#", "$","%","&","(",")","*","+",",","-",".","/", ":",";",
                ">","=","<","?","@","[","\\","]","^","_","`","{","}","~"));
        keyWords = new HashSet<>(Arrays.asList("USE","CREATE","DROP","ALTER","INSERT","SELECT","UPDATE",
                "DELETE","JOIN","TRUE","FALSE","DATABASE","TABLE","INTO","VALUES","FROM","WHERE","SET","AND","ON","ADD",
                "OR", "NULL","LIKE")); //24
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

package edu.uob;

import java.util.Arrays;
import java.util.HashSet;

public class GlobalMethod {
    private static String curDatabaseName = null;
    private static String curTableName = null;
    protected static HashSet<String> symbols;
    protected static HashSet<String> keyWords;
    public static HashSet<Integer> digits;
    public static HashSet<Boolean> booleanLiteral;

    static {
        symbols = new HashSet<String>(Arrays.asList("!", "#", "$","%","&","(",")","*","+",",","-",".","/", ":",";",
                ">","=","<","?","@","[","\\","]","^","_","`","{","}","~"));
        keyWords = new HashSet<String>(Arrays.asList("USE","CREATE","DROP","ALTER","INSERT","SELECT","UPDATE",
                "DELETE","JOIN","TRUE","FALSE","DATABASE","TABLE","INTO","VALUES","FROM","WHERE","SET","AND","ON","ADD",
                "OR", "NULL","LIKE")); //24
        digits = new HashSet<Integer>(Arrays.asList(0,1,2,3,4,5,6,7,8,9));
        booleanLiteral = new HashSet<Boolean>(Arrays.asList(true,false));
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

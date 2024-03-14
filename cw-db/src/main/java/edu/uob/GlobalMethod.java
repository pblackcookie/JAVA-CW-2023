package edu.uob;

public class GlobalMethod {
    private static String curDatabaseName = null;
    private static String curTableName = null;
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

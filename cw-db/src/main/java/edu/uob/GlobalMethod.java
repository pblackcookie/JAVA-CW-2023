package edu.uob;

public class GlobalMethod {
    private static String curDatabaseName = null;
    // Using for store current database name.
    public static void setCurDatabaseName(String databaseName) {
        curDatabaseName = databaseName;
    }
    public static String getCurDatabaseName() {
        return curDatabaseName;
    }

}

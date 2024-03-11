package edu.uob;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DatabaseProcess {
    private String databasePath;
    //DBServer dbServer = new DBServer();
    public void createDatabase(String databaseName) throws IOException {
        DBServer dbServer = new DBServer();
        databasePath = dbServer.getStorageFolderPath() + File.separator + databaseName;
        try {
            Files.createDirectories(Path.of(databasePath));
        }catch (IOException e){
            throw new IOException("Can't able to create database storage folder " + databaseName);
        }
    }
    // Return the databasePath for FileProcess class to use.
    public String getDatabasePath(String databaseName) {
        DBServer dbServer = new DBServer();
        databasePath = dbServer.getStorageFolderPath() + File.separator + databaseName;
        return databasePath;}
}

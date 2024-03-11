package edu.uob;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DatabaseProcess {
    private String databasePath;
    DBServer dbServer = new DBServer();
    public void createDatabase(String databaseName) throws IOException {
        databasePath = dbServer.getStorageFolderPath() + File.separator + databaseName;
        try {
            Files.createDirectories(Path.of(databasePath));
        }catch (IOException ioe){
            throw new IOException("Can't able to create database storage folder " + databaseName);
        }
    }
    // Drop the database if it exists
    public void dropDatabase(String databaseName) throws IOException{
        databasePath = dbServer.getStorageFolderPath() + File.separator + databaseName;
        try{

            Files.deleteIfExists(Path.of(databasePath));
        }catch (IOException ioe){ // Record the error message rather than delete it.
            System.err.println("Error deleting database folder " + databaseName + ": " + ioe.getMessage());
            throw new IOException("Can't able to drop database folder " + databaseName);
        }
    }
    // Return the databasePath for FileProcess class to use.
    public String getDatabasePath(String databaseName) {
        databasePath = dbServer.getStorageFolderPath() + File.separator + databaseName;
        return databasePath;
    }
}

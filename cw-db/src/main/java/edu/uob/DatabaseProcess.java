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
    public void dropDatabase(String databaseName) throws IOException {
        databasePath = dbServer.getStorageFolderPath() + File.separator + databaseName;
        if (Files.exists(Path.of(databasePath))) {
            try {
                Files.walk(Path.of(databasePath))
                        // desc order -> in order to delete the files first, then delete the folder(database)
                        // make sure the folder is empty when deleting it.
                        .sorted((path1, path2) -> -path1.compareTo(path2))
                        .forEach(path -> {
                            try {
                                Files.deleteIfExists(path); // safer way to delete
                            } catch (IOException ioe) {
                                System.err.println("Failed to delete: " + path.toString());
                            }
                        });
                System.out.println("Delete the database: [" + databaseName + "] successfully.");
            } catch (IOException ioe) { // Record the error message rather than delete it.
                System.err.println("Error deleting database folder " + databaseName + ": " + ioe.getMessage());
                // throw new IOException("Can't able to drop database folder " + databaseName);
            }
        }else{
            System.out.println("Database: [" +  databaseName + "] doesn't exist.");
        }
    }
    // Return the databasePath for FileProcess class to use.
    public String getDatabasePath(String databaseName) {
        databasePath = dbServer.getStorageFolderPath() + File.separator + databaseName;
        return databasePath;
    }
}

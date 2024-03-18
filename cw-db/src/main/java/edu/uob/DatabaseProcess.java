package edu.uob;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DatabaseProcess {
    private String databasePath;
    private String currentDatabase;
    DBServer dbServer = new DBServer();

    public String createDatabase(String databaseName) {
        // All the lower case when create it....
        databaseName = databaseName.toLowerCase();
        databasePath = getCurDatabasePath(databaseName);
        try {
            if(Files.exists(Path.of(databasePath))) {
                //System.err.println("Database: " + databaseName + " already exists.");
                return "[ERROR]Database: " + databaseName + " already exists.";
            }else {
                Files.createDirectories(Path.of(databasePath));
                return "[OK]Create " + databaseName + " successfully";
            }
        }catch (IOException ioe){
            return "[ERROR]Can't able to create database storage folder " + databaseName;
            //throw new IOException("Can't able to create database storage folder " + databaseName);
        }
    }
    // Drop the database if it exists
    public String dropDatabase(String databaseName) {
        databasePath = getCurDatabasePath(databaseName);
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
                                throw new RuntimeException("[ERROR]The file which is need to delete doesn't exist."+ path,ioe);
                            }
                        });
                //System.out.println("Delete the database: [" + databaseName + "] successfully.");
                return "[OK]Delete the database: [" + databaseName + "] successfully.";
            } catch (IOException ioe) { // Record the error message rather than delete it.
                return "[ERROR]Can't able to drop database folder " + databaseName;
            }catch (RuntimeException rune){
                return rune.getMessage(); // folder doesn't exist
            }
        }else{
            return "[ERROR]Database: [" +  databaseName + "] doesn't exist.";
        }
    }

    // Select the exist database(folder) to using...
    public String useDatabase(String databaseName){
        databaseName = databaseName.toLowerCase();
        databasePath = getCurDatabasePath(databaseName);
        if (Files.exists(Path.of(databasePath))) {
            currentDatabase = setCurDatabase(databaseName);
            return "[OK]Database [" + databaseName + "] is selected.";
        }else{
            return "[ERROR]:Selected database: [" +  databaseName + "] doesn't exist.\nPlease create it first";
        }
    }


    // Return the databasePath for FileProcess class to use.
    public String getDatabasePath(String databaseName) {
        databasePath = dbServer.getStorageFolderPath() + File.separator + databaseName;
        return databasePath;
    }
    public String getCurDatabasePath(String databaseName){
        databasePath = dbServer.getStorageFolderPath() + File.separator + databaseName;
        return databasePath;
    }
    // Return current database name which is the user selecting
    public String setCurDatabase(String databaseName){
        this.currentDatabase = databaseName;
        return databaseName;
    }

    //public String getCurDatabase(){ return currentDatabase;}
}

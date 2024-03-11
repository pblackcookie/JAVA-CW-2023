package edu.uob;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DatabaseProcess {
    private String databasePath;
    public void createDatabase(String databaseName) throws IOException {
        DBServer dbServer = new DBServer();
        databasePath = dbServer.getStorageFolderPath() + File.separator + databaseName;
        try {
            Files.createDirectories(Path.of(databasePath));
        }catch (IOException e){
            System.out.println("Can't seem to create database storage folder " + databaseName);
        }
    }
}

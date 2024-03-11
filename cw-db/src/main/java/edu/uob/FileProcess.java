package edu.uob;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileProcess {
    private String databasePath;
    private String filePath;

    //DBServer dbServer = new DBServer();
    //DatabaseProcess curDatabasePath = new DatabaseProcess();

    // Try to display all files by importing java.io package and using it.
    public void displayFiles(String databaseName) throws IOException{
        //DBServer dbServer = new DBServer();
        DatabaseProcess curDatabasePath = new DatabaseProcess();
        databasePath = curDatabasePath.getDatabasePath(databaseName);
        System.out.println(databasePath);
        File documentsFolder = new File(databasePath);
        File[] documents = documentsFolder.listFiles();
        for (File document : documents) {
            if (document.isDirectory()) {
                System.out.println("Folder: " + document.getName());
            } else {
                System.out.println("File: " + document.getName());
            }
        }
    }

    // For reading the stored files in the databases.
    public void readFileContent (String fileName, String databaseName) throws IOException{
        //DBServer dbServer = new DBServer();
        DatabaseProcess curDatabasePath = new DatabaseProcess();
        databasePath = curDatabasePath.getDatabasePath(databaseName);
        filePath = databasePath + File.separator + fileName;
        try(BufferedReader reader = new BufferedReader(new FileReader(filePath))){
            String line;
            while((line = reader.readLine())  != null){
                System.out.println(line);
            }
        }catch (IOException e){
            // If server can't found the file, then will create it first.
            try {
                Files.createFile(Path.of(filePath));
            }catch(IOException ioe) {
                throw new IOException("Error reading file: " + ioe.getMessage());
            }
        }
    }

    public void storageFileContent (String fileName) throws IOException{
    }
}

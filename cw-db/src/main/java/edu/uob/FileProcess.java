package edu.uob;
import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class FileProcess {
    private String databasePath;
    DatabaseProcess curDatabasePath = new DatabaseProcess();
    //Create an empty table
    public void createFile(String fileName, String databaseName) throws IOException {
        String extension = ".tab";
        //fileName += extension;
        String filePath = curDatabasePath.getDatabasePath(databaseName) + File.separator + fileName + extension;
        //Only create it if this table isn't exist
        Path path = Path.of(filePath);
        try {
            Files.createFile(path);
            System.out.println("File created successfully.");
        } catch (FileAlreadyExistsException e) {
            System.out.println("File already exists.");
        } catch (IOException e) {
            System.out.println("An error occurred while creating the file: " + e.getMessage());
            e.printStackTrace();
        }
    }
    // Try to display all files by importing java.io package and using it.
    public void displayFiles(String databaseName) throws IOException{
        databasePath = curDatabasePath.getDatabasePath(databaseName);
        System.out.println(databasePath);
        File documentsFolder = new File(databasePath);
        File[] documents = documentsFolder.listFiles();
        if (documents != null) {
            for (File document : documents) {
                if (document.isDirectory()) {
                    System.out.println("Folder: " + document.getName());
                } else {
                    System.out.println("File: " + document.getName());
                }
            }
        }
    }

    // For reading the stored files in the databases.
    public void readFileContent (String fileName, String databaseName) throws IOException{
        databasePath = curDatabasePath.getDatabasePath(databaseName);
        String filePath = databasePath + File.separator + fileName;
        try{
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line;
            int lineNumber = 0;
            // expression to check if the tab is correctly used
            Pattern pattern = Pattern.compile("[\\w@]+(?:\\t[\\w@]+)*");
            lineNumber++;
            while((line = reader.readLine())  != null){
                System.out.println(line);
                Matcher matcher = pattern.matcher(line);
                try{
                    if (!matcher.matches()) {
                        System.out.println(line);
                        throw new IOException("Invalid format at line " + lineNumber);
                    }
                }catch(IOException ioe){
                    System.err.println("Invalid format at line " + lineNumber + ". Fixing...");
                    throw new IOException("Invalid format at line " + lineNumber);
                }
            }
        }catch (IOException ioe2){
            // If server can't found the file, then will create it first.
            try {
                Files.createFile(Path.of(filePath));
            }catch(IOException ioe3) {
                throw new IOException("Error reading file: " + ioe3.getMessage());
            }
        }
    }

    public void storageFileContent (String fileName) throws IOException{
    }
}

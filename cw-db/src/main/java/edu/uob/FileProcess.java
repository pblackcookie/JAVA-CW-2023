package edu.uob;
import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class FileProcess {
    private String databasePath;
    String extension = ".tab";
    String IdRecord = ".id";
    DatabaseProcess curDatabasePath = new DatabaseProcess();
    //Create an empty table
    public String createFile(String fileName, String databaseName) throws IOException {
        String filePath = curDatabasePath.getDatabasePath(databaseName) + File.separator + fileName + extension;
        String IdRecordPath = curDatabasePath.getDatabasePath(databaseName) + File.separator + fileName + IdRecord;
        //Only create it if this table isn't exist
        try {
            Files.createFile(Path.of(filePath));
            Files.createFile(Path.of(IdRecordPath));
            return "[OK]File created successfully.";
        } catch (FileAlreadyExistsException e) {
            System.out.println("File already exists.");
            return "[ERROR]File already exists.";
        } catch (IOException e) {
            //System.out.println("An error occurred while creating the file: " + e.getMessage());
            throw new RuntimeException("[ERROR]An error occurred while creating the file: " + e.getMessage());
        }catch (RuntimeException rune){
            return rune.getMessage();
        }
    }
    // when the table with attributes
    public String createFile(String fileName, String databaseName, ArrayList<String> attributes) throws IOException {
        String filePath = curDatabasePath.getDatabasePath(databaseName) + File.separator + fileName + extension;
        String IdRecordPath = curDatabasePath.getDatabasePath(databaseName) + File.separator + fileName + IdRecord;
        //Only create it if this table isn't exist
        Path path = Path.of(filePath);
        try {
            Files.createFile(Path.of(IdRecordPath));
            FileWriter writerId = new FileWriter(String.valueOf(Path.of(IdRecordPath)));
            writerId.write("1");
            writerId.close();
            Files.createFile(path);
            FileWriter writer = new FileWriter(String.valueOf(path));
            BufferedWriter buffer = new BufferedWriter(writer);
            // Need to add the file that records the id also
            // Always keep the id in the first column
            attributes.add(0, "id");
            // For loop for write the attributes each time
            for (int i = 0; i < attributes.size(); i++) {
                writer.write(attributes.get(i));
                if (i != attributes.size() - 1) {
                    writer.write("\t");
                }
            }
            //buffer.write(String.valueOf(attributes));
            buffer.close();
            return "[OK]File created with attributes successful.";
        } catch (FileAlreadyExistsException e) {
            System.out.println("File already exists.");
            return "[ERROR]File already exists.";
        } catch (IOException e) {
            //System.out.println("An error occurred while creating the file: " + e.getMessage());
            throw new RuntimeException("[ERROR]An error occurred while creating the file: " + e.getMessage());
        }catch (RuntimeException rune){
            return rune.getMessage();
        }
    }

    // Try to delete file when is selected
    public String dropFile(String fileName, String databaseName) throws IOException{
        String filePath = curDatabasePath.getDatabasePath(databaseName) + File.separator + fileName + extension;
        String IdRecordPath = curDatabasePath.getDatabasePath(databaseName) + File.separator + fileName + IdRecord;
        Path path = Path.of(filePath);
        try{
            Files.delete(path);
            Files.delete(Path.of(IdRecordPath)); // Need to delete the id record file at the same time
            System.out.println("File deleted successfully.");
            return "[OK]File: " + fileName + " deleted successfully.";
        }catch (NoSuchFileException e) {
            //System.out.println("File does not exist: " + e.getMessage());
            return "[ERROR]File" + fileName + " does not exist: " + e.getMessage();
        } catch (IOException e) {
            System.out.println("An error occurred: " + e.getMessage());
            return "[ERROR]An error occurred: " + e.getMessage();
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

    public String addFileContent(ArrayList<String> data, String path) throws IOException{
        DataProcess lineData = new DataProcess();
        return lineData.dataInsert(data, path);
    }
}

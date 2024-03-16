package edu.uob;
import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;

public class FileProcess {
    private String databasePath;
    String extension = ".tab";
    String IdRecord = ".id";
    DatabaseProcess curDatabasePath = new DatabaseProcess();
    DataProcess dataProcess;
    //Create an empty table
    public String createFile(String fileName, String databaseName) throws IOException {
        fileName = fileName.toLowerCase();
        String filePath = curDatabasePath.getDatabasePath(databaseName) + File.separator + fileName + extension;
        String IdRecordPath = curDatabasePath.getDatabasePath(databaseName) + File.separator + fileName + IdRecord;
        //Only create it if this table isn't exist
        Path idPath = Path.of(IdRecordPath);
        try {
            Files.createFile(Path.of(filePath));
            Files.createFile(idPath);
            FileWriter writerId = new FileWriter(String.valueOf(idPath));
            writerId.write("0");
            writerId.close();
            return "[OK]File created successfully.";
            //throw new IOException("File already exists.");
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
        fileName = fileName.toLowerCase();
        String filePath = curDatabasePath.getDatabasePath(databaseName) + File.separator + fileName + extension;
        String IdRecordPath = curDatabasePath.getDatabasePath(databaseName) + File.separator + fileName + IdRecord;
        //Only create it if this table isn't exist
        Path path = Path.of(filePath);
        Path idPath = Path.of(IdRecordPath);
        try {
            Files.createFile(idPath);
            FileWriter writerId = new FileWriter(String.valueOf(idPath));
            writerId.write("0");
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
    public ArrayList<String> displayFiles(String databaseName) throws IOException{
        databasePath = curDatabasePath.getDatabasePath(databaseName);
        ArrayList<String> files = new ArrayList<>();
        //System.out.println(databasePath);
        File documentsFolder = new File(databasePath);
        File[] documents = documentsFolder.listFiles();
        if (documents != null) {
            for (File document : documents) {
                if (!document.isDirectory()) {
                    files.add(document.getName());
                }
            }
        }
        return files;
    }

    // Insert the data line
    public String addFileContent(ArrayList<String> data, String path) throws IOException{
        dataProcess = new DataProcess();
        return dataProcess.dataInsert(data, path);
    }

    // Rewrite the displayFile to show the file content.
    public String showFileContent (String fileName, String databaseName) throws IOException{
        databasePath = curDatabasePath.getDatabasePath(databaseName);
        String filePath = databasePath + File.separator + fileName + extension;
        StringBuilder fileContent = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                fileContent.append(line).append("\n");
            }
        }
        return fileContent.toString();
    }
    // Write here for add one column when checking
    public String changeFileContent(String filePath, String curCommand, String attributeName) throws IOException{
        dataProcess = new DataProcess();
        if(curCommand.equalsIgnoreCase("ADD")){
            curCommand = dataProcess.attributeAdd(filePath, attributeName);
            return curCommand;
        }else if(curCommand.equalsIgnoreCase("DROP")){
            curCommand = dataProcess.attributeDrop(filePath, attributeName);
            return curCommand;
        }
        curCommand = "[ERROR]Failed to add or drop the attribute in table.";
        return  curCommand;
     }
}

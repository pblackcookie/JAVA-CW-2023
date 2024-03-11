package edu.uob;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileProcess {
    private String folderPath;
    private String fileName;
    // Try to display all files by importing java.io package and using it.
    public void displayFiles(){
        DBServer dbServer = new DBServer();
        folderPath = dbServer.getStorageFolderPath();
        File documentsFolder = new File(folderPath);
        File[] documents = documentsFolder.listFiles();
        for(File document : documents){
            if (document.isDirectory()) {
                System.out.println("Folder: " + document.getName());
            } else {
                System.out.println("File: " + document.getName());
            }
        }
    }
    // For reading the stored files in the databases.
    public void readFileContent (String fileName) throws IOException{
        DBServer dbServer = new DBServer();
        folderPath = dbServer.getStorageFolderPath();
        try(BufferedReader reader = new BufferedReader(new FileReader(folderPath + File.separator + fileName))){
            String line;
            while((line = reader.readLine())  != null){
                System.out.println(line);
            }
        }catch (IOException e){
            // If server can't found the file, then will create it first.
            try {
                Files.createFile(Path.of(folderPath + File.separator + fileName));
            }catch(IOException ioe) {
                throw new IOException("Error reading file: " + ioe.getMessage());
            }
        }
    }

    public void storageFileContent (String fileName) throws IOException{

    }
}

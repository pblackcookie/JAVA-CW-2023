package edu.uob;
import java.io.*;
public class FileProcess {
    private String folderPath = ".." + File.separator;
    // Try to display all files by importing java.io package and using it.
    public void displayFiles(){
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

    public void readFileContent(){

    }
}

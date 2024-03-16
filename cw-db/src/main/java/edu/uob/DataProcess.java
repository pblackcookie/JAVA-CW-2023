package edu.uob;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class DataProcess {
    // This class is used to process series of data when one specific table is selected
    private String dataStatus;
    private String commandStatus;

    private String firstElement = "id";
    private ArrayList<String> attributeList = new ArrayList<>();;

    public String dataInsert(ArrayList<String> data, String path) throws IOException {
        // In here process the process method
        FileWriter writer = new FileWriter(String.valueOf(path), true);
        BufferedWriter buffer = new BufferedWriter(writer);
        try {
            for (int i = 0; i < data.size(); i++) {
                buffer.write(data.get(i));
                if (i != data.size() - 1) {
                    buffer.write("\t");
                }else{
                    writer.write("\n");
                }
            }
            buffer.close();
            writer.close();
            dataStatus = "[OK]Insert data successfully.";
        }catch (IOException e){
            dataStatus = "[ERROR]Write data into table failed.";
        }
        return dataStatus;
    }
    // The function which will read the whole file contain and changing the attributes should write in here....
    public String attributeAdd(String filePath, String attributeName) throws IOException {
        // changing the file content in here -- add
        File file = new File(filePath);

        // file is empty
        if(file.length()==0){
            if(attributeName.equalsIgnoreCase("id")){
                commandStatus = "[ERROR]A duplicate id is added.";
                return commandStatus;
            }
            // no duplicate , so adding it into the file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                writer.write(firstElement);
                writer.write("\t");
                writer.write(attributeName);
                commandStatus = "Add the elements successfully";
                return commandStatus;
            } catch (IOException e) {
                commandStatus = "Error occur: " + e.getMessage();
                return commandStatus;
            }
        }else {// file isn't empty
            // read the file content and check the duplicate first
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String firstLine = reader.readLine();
                System.out.println("first line is : " + firstLine);
                attributeList.addAll(Arrays.asList(firstLine.split("\t")));
                for(String attribute: attributeList){
                    if(attribute.equalsIgnoreCase(attributeName)){
                        commandStatus = "[ERROR]Can not add the duplicate element: " + attributeName;
                        return commandStatus;
                    }
                }
                // No duplicate attribute name so add it.
                // 1. remove the \n in the first line
                // 2. add the new attribute name and \n
                firstLine = firstLine.trim(); // remove the \n
                firstLine += "\t" + attributeName;
                // Now the processing for the first line is ending.
                // But still needing to reading the left part for writing into the file.
                // write back to the file
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                    writer.write(firstLine);
                    commandStatus = "[OK]Add the elements successfully";
                    return commandStatus;
                } catch (IOException e) {
                    commandStatus = "[Error]Error writing to file: " + e.getMessage();
                    return commandStatus;
                }
            } catch (IOException e) {
                commandStatus = "[Error]Error occur: " + e.getMessage();
                return commandStatus;
            }
        }

    }

    public String attributeDrop(String filePath, String attributeName) throws IOException {
        // changing the file content in here -- drop
        commandStatus = "[OK]In drop now";
        return commandStatus;
    }
}

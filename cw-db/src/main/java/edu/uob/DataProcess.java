package edu.uob;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class DataProcess {
    // This class is used to process series of data when one specific table is selected
    private String dataStatus;
    private String commandStatus;

    private final String firstElement = "id";
    private ArrayList<String> attributeList = new ArrayList<>();

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
    public String attributeAdd(String filePath, String attributeName)  {
        // changing the file content in here -- add
        File file = new File(filePath);
        // file is empty
        if(file.length()==0){
            if(attributeName.equalsIgnoreCase("id")){
                commandStatus = "[ERROR]A duplicate id is added.";
                return commandStatus;
            }
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                    writer.write(firstElement);
                    writer.write("\t");
                    writer.write(attributeName);
                    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                        String line = reader.readLine();
                        // line = line.trim(); // remove \n if exists.
                    }catch (IOException e) {
                        commandStatus = "[Error]Error occur: " + e.getMessage();
                        return commandStatus;
                    }
                    commandStatus = "Add the elements successfully";
                    return commandStatus;
                } catch (IOException e) {
                    commandStatus = "Error occur: " + e.getMessage();
                    return commandStatus;
                }
        }else {// file isn't empty
            // read the file content and check the duplicate first
            StringBuilder contentBuilder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String line = reader.readLine();
                attributeList.addAll(Arrays.asList(line.split("\t")));
                for(String attribute: attributeList){
                    if(attribute.equalsIgnoreCase(attributeName)){
                        commandStatus = "[ERROR]Can not add the duplicate element: " + attributeName;
                        return commandStatus;
                    }
                }
                // No duplicate attribute name so add it. 1. remove the \n in the first line
                // 2. add the new attribute name and \n
                line = line.trim(); // remove the \n
                line += "\t" + attributeName;
                contentBuilder.append(line).append("\n");
                // Now the processing for the first line is ending.
                // But still needing to reading the left part for writing into the file.
                // From here starting reading the left line...
                while ((line = reader.readLine()) != null) {
                    contentBuilder.append(line).append("\tNULL\n");
                }
                // write back to the file
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                    writer.write(contentBuilder.toString());
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

    public String attributeDrop(String filePath, String attributeName) {
        int columnIndex = -1;
        File file = new File(filePath);
        if(file.length()==0){// file is empty
            commandStatus = "[ERROR]Can not drop the attribute" + attributeName + "from one empty file.";
            return commandStatus;
        }// the attribute name is id.
        if(attributeName.equalsIgnoreCase("id")){
            commandStatus = "[ERROR]id can not be dropped.";
            return commandStatus;
        }// Start operation for drop one column here...
        // First read the first line to see if it exists and then do operation.
        StringBuilder contentBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line = reader.readLine(); // first line operation
            attributeList.addAll(Arrays.asList(line.split("\t")));
            for (int i = 0; i < attributeList.size(); i++) {
                String attribute = attributeList.get(i);
                if (attribute.equalsIgnoreCase(attributeName)) {
                    columnIndex = i;
                    attributeList.remove(i); // remove the elements that need to delete
                    break;
                }
            }
            if(columnIndex == -1){
                commandStatus = "[ERROR]Can not drop the attribute:" + attributeName + ". Not found it.";
                return commandStatus;
            }
            for (int i = 0; i < attributeList.size(); i++) {
                String attribute = attributeList.get(i);
                if(i < attributeList.size()-1) {
                    contentBuilder.append(attribute).append("\t");
                }else{
                    contentBuilder.append(attribute).append("\n");
                }
            }
            attributeList.clear();
            //BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
            // if successful found it , then delete the column line according to columnIndex;
            while ((line = reader.readLine()) != null) {
                attributeList.addAll(Arrays.asList(line.split("\t")));
                System.out.println("Attribute list now is : " + attributeList);
                attributeList.remove(columnIndex);
                for (int i = 0; i < attributeList.size(); i++) {
                    String attribute = attributeList.get(i);
                    if(i < attributeList.size()-1) {
                        contentBuilder.append(attribute).append("\t");
                    }else{
                        contentBuilder.append(attribute).append("\n");
                    }
                }
                attributeList.clear();
            }
            // Now write back to the file
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
            //System.out.println("write back to file now is: " + contentBuilder);
            writer.write(contentBuilder.toString());
            writer.close();
            commandStatus = "[OK]Successfully drop.";
            return commandStatus;
        }catch (IOException e) {
            commandStatus = "[Error]Error occur: " + e.getMessage();
            return commandStatus;
        }
    }
}

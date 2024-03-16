package edu.uob;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class ParserAlterCommand extends DBParser{
    private String curToken;
    private String database;
    private  String filePath;
    private final HashSet<String> alterationType = new HashSet<>(Arrays.asList("ADD", "DROP"));
    //private HashSet<String> attributeList = new HashSet<>(Arrays.asList(firstLineData));
    private ArrayList<String> attributeList;
    private final String firstElement;

    private final DBServer server = new DBServer();
    protected ParserAlterCommand(String command, int index) {
        super(command);
        this.index = index;
        firstElement = "id";
        attributeList = new ArrayList<>();
    }

    // <Alter>  ::=  "ALTER " "TABLE " [TableName] " " <AlterationType> " " [AttributeName]
    protected String parserAlter(){
        if(token.tokens.size()!=6){
            curCommandStatus = "Invalid command length in Alter command.";
            return curCommandStatus;
        }
        database = GlobalMethod.getCurDatabaseName();
        if(database == null){
            curCommandStatus = "Please choosing the used database first.";
            return curCommandStatus;
        }
        curToken = token.tokens.get(index); //"TABLE"
        if(!curToken.equalsIgnoreCase("TABLE")){
            curCommandStatus = "The [TABLE] in command is missing or typo.";
            return curCommandStatus;
        }
        index++;
        curToken = token.tokens.get(index).toLowerCase(); //"TABLE NAME"
        // check if the table name valid
        curCommandStatus = nameCheck(curToken);
        if(curCommandStatus.contains("[ERROR]")){
            return curCommandStatus;
        }
        //check if the table exist
        filePath = server.getStorageFolderPath() + File.separator + database + File.separator + curToken + ".tab";
        File file = new File(filePath);
        if(!file.exists()){
            curCommandStatus = "The chosen file does not exists.";
            return curCommandStatus;
        }
        index++;
        curToken = token.tokens.get(index).toUpperCase(); // <AlterationType>
        if(!alterationType.contains(curToken)){
            curCommandStatus = "The key word 'ADD' or 'DROP' is missing.";
            return curCommandStatus;
        }
        if(curToken.equals("ADD")){
            index++;
            curToken = token.tokens.get(index).toLowerCase(); // [AttributeName]
            curCommandStatus = AlterAdd(curToken);
            return curCommandStatus;
        }else if(curToken.equals("DROP")){
            index++;
            curToken = token.tokens.get(index).toLowerCase(); // [AttributeName]
            curCommandStatus = AlterDrop(curToken);
            return curCommandStatus;
        }
        curCommandStatus = "[OK]Finishing Alter now.";
        return curCommandStatus;
    }


    // 1. check the attribute name is valid
    // 2. check there has no duplicate attributes name
    private String AlterAdd(String attributeName){
        curCommandStatus = nameCheck(attributeName);
        if(curCommandStatus.contains("[ERROR]")){
            return curCommandStatus;
        }
        File file = new File(filePath);
        // file is empty
        if(file.length()==0){
            if(attributeName.equalsIgnoreCase("id")){
                curCommandStatus = "[ERROR]A duplicate id is added.";
                return curCommandStatus;
            }
            // no duplicate , so adding it into the file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                writer.write(firstElement);
                writer.write("\t");
                writer.write(attributeName);
                curCommandStatus = "Add the elements successfully";
                return curCommandStatus;
            } catch (IOException e) {
                curCommandStatus = "Error occur: " + e.getMessage();
                return curCommandStatus;
            }
        }else {// file isn't empty
            // read the file content and check the duplicate first
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String line = reader.readLine();
                attributeList.addAll(Arrays.asList(line.split("\t")));
                for(String attribute: attributeList){
                    if(attribute.equalsIgnoreCase(attributeName)){
                        curCommandStatus = "[ERROR]Can not add the duplicate element: " + attributeName;
                        return curCommandStatus;
                    }
                }
                // No duplicate attribute name so add it.
                // 1. remove the \n in the first line
                // 2. add the new attribute name and \n
                line = line.trim(); // remove the \n
                line += "\t" + attributeName;
                // write back to the file
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                    writer.write(line);
                    curCommandStatus = "[OK]Add the elements successfully";
                    return curCommandStatus;
                } catch (IOException e) {
                    curCommandStatus = "[Error]Error writing to file: " + e.getMessage();
                    return curCommandStatus;
                }
            } catch (IOException e) {
                curCommandStatus = "[Error]Error occur: " + e.getMessage();
                return curCommandStatus;
            }
        }
    }

    private String AlterDrop(String attributeName){
        curCommandStatus = nameCheck(attributeName);
        if(curCommandStatus.contains("[ERROR]")){
            return curCommandStatus;
        }
        // Read the file and check if the attribute exists or not,
        curCommandStatus = "[OK]But haven't finished the drop function yet";
        return curCommandStatus;
    }
}

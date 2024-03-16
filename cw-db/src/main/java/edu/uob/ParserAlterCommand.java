package edu.uob;

import javax.swing.plaf.basic.BasicListUI;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class ParserAlterCommand extends DBParser{
    private String curToken;
    private String database;
    private  String filePath;
    private final HashSet<String> AlterationType = new HashSet<>(Arrays.asList("ADD", "DROP"));;
    private ArrayList<String> AttributeList = new ArrayList<>(List.of("id"));

    private final DBServer server = new DBServer();
    protected ParserAlterCommand(String command, int index) {
        super(command);
        this.index = index;
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
        if(!AlterationType.contains(curToken)){
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
            if(attributeName.equalsIgnoreCase("ID")){
                curCommandStatus = "[ERROR]A duplicate id is added.";
                return curCommandStatus;
            }
            // no duplicate , so adding it into the file
            AttributeList.add(attributeName);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                writer.write(AttributeList.get(0));
                writer.write("\t");
                writer.write(AttributeList.get(1));
                curCommandStatus = "Add the elements successfully";
                return curCommandStatus;
            } catch (IOException e) {
                curCommandStatus = "Error occur: " + e.getMessage();
                return curCommandStatus;
            }
        }else {// file isn't empty
            // read the file content and check the duplicate first
            AttributeList.remove(0); // the file isn't empty so the id already exist
            return curCommandStatus;
        }

        //return curCommandStatus;
    }

    private String AlterDrop(String attributeName){
        curCommandStatus = nameCheck(attributeName);
        if(curCommandStatus.contains("[ERROR]")){
            return curCommandStatus;
        }
        // Read the file and check if the attribute exists or not,
        return curCommandStatus;
    }
}

package edu.uob;

import java.io.*;
import java.util.Arrays;
import java.util.HashSet;

public class ParserAlterCommand extends DBParser{
    private  String filePath;
    private final HashSet<String> alterationType = new HashSet<>(Arrays.asList("ADD", "DROP"));
    private final DBServer server = new DBServer();
    protected ParserAlterCommand(String command, int index) {
        super(command);
        this.index = index;
    }

    // <Alter>  ::=  "ALTER " "TABLE " [TableName] " " <AlterationType> " " [AttributeName]
    protected String parserAlter() throws IOException {
        if(token.tokens.size()!=6){
            curCommandStatus = "Invalid command length in Alter command.";
            return curCommandStatus;
        }
        String database = GlobalMethod.getCurDatabaseName();
        if(database == null){
            curCommandStatus = "Please choosing the used database first.";
            return curCommandStatus;
        }
        String curToken = token.tokens.get(index); //"TABLE"
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
    private String AlterAdd(String attributeName) throws IOException {
        curCommandStatus = nameCheck(attributeName);
        if(curCommandStatus.contains("[ERROR]")){
            return curCommandStatus;
        }
        curCommandStatus = table.changeFileContent(filePath, "ADD", attributeName);
        return curCommandStatus;
    }

    private String AlterDrop(String attributeName) throws IOException {
        curCommandStatus = nameCheck(attributeName);
        if(curCommandStatus.contains("[ERROR]")){
            return curCommandStatus;
        }
        // Read the file and check if the attribute exists or not,
        curCommandStatus = table.changeFileContent(filePath, "DROP", attributeName);
        return curCommandStatus;
    }
}

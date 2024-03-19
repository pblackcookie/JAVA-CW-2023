package edu.uob;

import java.io.File;

public class ParserDeleteCommand extends DBParser{
    DBServer server = new DBServer();
    public ParserDeleteCommand(String command, int index) {
        super(command);
        this.index = index;
    }
    //<Delete> :: =  "DELETE " "FROM " [TableName] " WHERE " <Condition>
    protected String parserDelete(){
        if(token.tokens.size()<6){
            System.out.println(token.tokens.size());
            curCommandStatus = "[ERROR]Invalid length for delete command";
            return curCommandStatus;
        }
        String curToken;
        curToken = token.tokens.get(index); // should be from here
        if(!curToken.equalsIgnoreCase("FROM")){
            curCommandStatus = "[ERROR]Missing or typo 'from' command.";
            return curCommandStatus;
        }
        index++;
        curToken = token.tokens.get(index).toLowerCase();//table name now
        String filePath = server.getStorageFolderPath() + File.separator + GlobalMethod.getCurDatabaseName()
                + File.separator +curToken + ".tab";
        File file = new File(filePath);
        if(!file.exists()){
            curCommandStatus = "[ERROR]File does not exists.";
            return curCommandStatus;
        }
        index++; // should be where now
        curToken = token.tokens.get(index);
        if(!curToken.equalsIgnoreCase("WHERE")){
            curCommandStatus = "[ERROR]Missing or typo 'where' command.";
            return curCommandStatus;
        }
        index++; // should be condition now - attribute
        curToken = token.tokens.get(index);
        curCommandStatus = "[OK]In delete command now";
        return curCommandStatus;
    }
}

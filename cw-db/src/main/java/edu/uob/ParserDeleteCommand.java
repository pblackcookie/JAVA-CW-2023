package edu.uob;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ParserDeleteCommand extends DBParser{
    DBServer server = new DBServer();
    public ParserDeleteCommand(String command, int index) {
        super(command);
        this.index = index;
    }
    //<Delete> :: =  "DELETE " "FROM " [TableName] " WHERE " <Condition>
    protected String parserDelete() throws IOException {
        if(token.tokens.size()<6){
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
        conditionCheck(filePath); // using condition for checking
        if(curCommandStatus.contains("[ERROR]")){
            return curCommandStatus;
        }
        updateContent(curCommandStatus,filePath);
        curCommandStatus = "[OK]";
        return curCommandStatus;
    }

    private void updateContent(String content,String filePath) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
        writer.write(content);
        writer.close();
    }
    @Override
    protected String showTheContent (String operation, String demand){
        StringBuilder newString = new StringBuilder();
        int rowCount = 0;
        for (int i = 1; i < tableRow.size(); i++) {
            if(tableRow.get(i).equals(-1)){
                rowCount++;
            }
        }
            if(tableRow.size()-1 == rowCount){
                curCommandStatus = "[ERROR]Value does not exist";
                return curCommandStatus;
            }
        for (int i = 0; i < tableRow.size(); i++) {
            for (int j = 0; j < tableCol.size(); j++) {
                if(tableRow.get(i).equals(-1) || i == 0){
                    if(j == tableCol.size()-1) {
                        newString.append(tableContent.get(i * tableCol.size() + j));
                        newString.append("\n");
                    }else{
                        newString.append(tableContent.get(i * tableCol.size() + j));
                        newString.append("\t");
                    }
                }
            }
        }
        curCommandStatus = newString.toString().trim();
        return curCommandStatus;
    }
}

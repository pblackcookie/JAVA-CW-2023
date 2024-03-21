package edu.uob;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ParserUpdateCommand extends DBParser{
    ArrayList<String> setAttribute = new ArrayList<>();
    ArrayList<String> setValue = new ArrayList<>();
    public ParserUpdateCommand(String command, int index) {
        super(command);
        this.index = index;
    }
//<Update> ::=  "UPDATE " [TableName] " SET " <NameValueList> " WHERE " <Condition>
    protected String parserUpdate() throws IOException {
        String curToken = token.tokens.get(index).toLowerCase(); // table name now
        String filePath = server.getStorageFolderPath() + File.separator + GlobalMethod.getCurDatabaseName()
                + File.separator + curToken + ".tab";
        // check table exists
        File file = new File(filePath);
        if(!file.exists()){
            curCommandStatus = "[ERROR]File does not exists.";
            return curCommandStatus;
        }
        index++;
        curToken = token.tokens.get(index); // set now
        if(!curToken.equalsIgnoreCase("SET")){
            curCommandStatus = "[ERROR]Missing or typo 'SET' command.";
            return curCommandStatus;
        }
        // TODO：From here start NameValueList check
        // assume only one NameValueList first
        index++;
        curToken = token.tokens.get(index); // must be attributes now
        ArrayList<String> attributes = new ArrayList<>();
        attributes.add(curToken);
        curCommandStatus = attributeCheck(attributes);
        if(curCommandStatus.contains("[ERROR]")){
            curCommandStatus = "Attributes invalid.";
            return curCommandStatus;
        }
        curToken = curToken.toLowerCase();
        setAttribute.add(curToken);
        attributes.add(curToken);
        // update the row column
//        boolean attributeCheck = colIndexStorage(filePath,attributes);
//        if(!attributeCheck){
//            curCommandStatus = "[ERROR]Does not exist attribute.";
//            return curCommandStatus;
//        }
        index++; // must be = in here
        curToken =  token.tokens.get(index);
        if(!curToken.equalsIgnoreCase("=")){
            curCommandStatus = "[ERROR]Missing or typo symbol: = ";
            return curCommandStatus;
        }
        index++;
        curToken = token.tokens.get(index); // must be value here;
        curCommandStatus = valueCheck(curToken);
        if(curCommandStatus.contains("[ERROR]")){
            curCommandStatus = "[ERROR]Invalid value type.";
            return curCommandStatus;
        }
        curToken = curToken.toLowerCase();
        setValue.add(curToken);
        index++;
        curToken = token.tokens.get(index); // must be 'where' in here
        if(!curToken.equalsIgnoreCase("WHERE")){
            curCommandStatus = "[ERROR]Missing or typo 'WHERE'";
            return curCommandStatus;
        }
        index++; // from here the condition start
        // UPDATE marks SET age = 35 WHERE name == 'Simon';
        curCommandStatus = conditionCheck(filePath);
        curCommandStatus = readContend(filePath);
        updateContent(curCommandStatus,filePath);
        curCommandStatus = "[OK]";
        return curCommandStatus;
    }

    private void updateContent(String content,String filePath) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
        writer.write(content);
        writer.close();
    }

    private String readContend(String filePath){
        curCommandStatus = "";
        for (int i = 0; i < tableRow.size(); i++) {
            for (int j = 0; j < tableCol.size(); j++) {
                if(tableRow.get(i).equals(-1) || i == 0){
                    if(j == tableCol.size()-1) {
                        curCommandStatus += tableContent.get(i * tableCol.size() + j) + "\n";
                    }else{
                        curCommandStatus += tableContent.get(i * tableCol.size() + j) + "\t";
                    }
                }
                if(!tableRow.get(i).equals(-1) && i != 0){
                    if(tableContent.get(j).equalsIgnoreCase(setAttribute.get(0))){
                        if(j == tableCol.size()-1) {
                            curCommandStatus += setValue.get(0) + "\n";
                        }else{
                            curCommandStatus += setValue.get(0) + "\t";
                        }
                    }else{
                        if(j == tableCol.size()-1) {
                            curCommandStatus += tableContent.get(i * tableCol.size() + j) + "\n";
                        }else{
                            curCommandStatus += tableContent.get(i * tableCol.size() + j) + "\t";
                        }
                    }
                }
            }
        }
        return  curCommandStatus;
    }
}

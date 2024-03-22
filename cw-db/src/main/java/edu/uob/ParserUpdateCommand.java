package edu.uob;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class ParserUpdateCommand extends DBParser{
    ArrayList<String> setAttribute = new ArrayList<>();
    ArrayList<String> setValue = new ArrayList<>();
    public ParserUpdateCommand(String command, int index) {
        super(command);
        this.index = index;
    }
//<Update> ::=  "UPDATE " [TableName] " SET " <NameValueList> " WHERE " <Condition>
    protected String parserUpdate(){
        String curToken = token.tokens.get(index).toLowerCase(); // table name now
        filePath = server.getStorageFolderPath() + File.separator + GlobalMethod.getCurDatabaseName()
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
        try {
            ArrayList<Integer> rowIndex = MultipleConditionCheck();
            curCommandStatus = readContend(rowIndex);
            updateContent(curCommandStatus,filePath);
            curCommandStatus = "[OK]";
            return curCommandStatus;
        }catch (Exception e){
            curCommandStatus = "[ERROR]";
            return curCommandStatus;
        }
    }

    private void updateContent(String content,String filePath) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
        writer.write(content);
        writer.close();
    }

    private String readContend(ArrayList<Integer> rowIndex){
        StringBuilder newString = new StringBuilder();
        for (int i = 0; i < rowIndex.size(); i++) {
            for (int j = 0; j < tableCol.size(); j++) {
                if(rowIndex.get(i).equals(-1) || i == 0){
                    if(j == tableCol.size()-1) {
                        newString.append(tableContent.get(i * tableCol.size() + j));
                        newString.append("\n");
                    }else{
                        newString.append(tableContent.get(i * tableCol.size() + j));
                        newString.append("\t");
                    }
                }
                if(!rowIndex.get(i).equals(-1) && i != 0){
                    if(tableContent.get(j).equalsIgnoreCase(setAttribute.get(0))){
                        if(j == tableCol.size()-1) {
                            newString.append(setValue.get(0));
                            newString.append("\n");
                        }else{
                            newString.append(setValue.get(0));
                            newString.append("\t");
                        }
                    }else{
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
        }
        curCommandStatus = newString.toString().trim();
        return  curCommandStatus;
    }
}

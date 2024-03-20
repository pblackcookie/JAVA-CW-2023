package edu.uob;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ParserUpdateCommand extends DBParser{
    Map<String, String> keyValueMap = new HashMap<>();
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
        String key = curToken.toLowerCase();
        attributes.add(curToken);
        // update the row column
        boolean attributeCheck = colIndexStorage(filePath,attributes);
        if(!attributeCheck){
            curCommandStatus = "[ERROR]Does not exist attribute.";
            return curCommandStatus;
        }
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
        String value = curToken.toLowerCase();
        keyValueMap.put(key,value); // like age : 35
        index++;
        curToken = token.tokens.get(index); // must be 'where' in here
        if(!curToken.equalsIgnoreCase("WHERE")){
            curCommandStatus = "[ERROR]Missing or typo 'WHERE'";
            return curCommandStatus;
        }
        index++; // from here the condition start
        // UPDATE marks SET age = 35 WHERE name == 'Simon';
        curCommandStatus = conditionCheck();
        System.out.println("tableCol now: " +tableCol);
        System.out.println("tableRow now: " + tableRow);
        return curCommandStatus;
    }
}

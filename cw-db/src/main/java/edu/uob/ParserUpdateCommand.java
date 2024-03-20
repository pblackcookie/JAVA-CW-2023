package edu.uob;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;

public class ParserUpdateCommand extends DBParser{

    public ParserUpdateCommand(String command, int index) {
        super(command);
        this.index = index;
    }
//<Update> ::=  "UPDATE " [TableName] " SET " <NameValueList> " WHERE " <Condition>
    protected String parserUpdate(){
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
        attributes.add(curToken);
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
        index++;
        curToken = token.tokens.get(index); // must be 'where' in here
        if(!curToken.equalsIgnoreCase("WHERE")){
            curCommandStatus = "[ERROR]Missing or typo 'WHERE'";
            return curCommandStatus;
        }
        index++; // from here the condition start

        return curCommandStatus;
    }
}

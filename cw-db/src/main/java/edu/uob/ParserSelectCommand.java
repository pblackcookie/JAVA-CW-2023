package edu.uob;

import java.io.IOException;
import java.util.ArrayList;

import static edu.uob.GlobalMethod.getCurDatabaseName;

public class ParserSelectCommand extends DBParser{
    // The switch for control the show or not
    ArrayList<String> columnFlag;
    ArrayList<String> rowFlag;

    public ParserSelectCommand(String command, int index) {
        super(command);
        this.index = index;
        columnFlag = new ArrayList<>();
        rowFlag = new ArrayList<>();
    }

    protected String parserSelect() throws IOException {
        String curToken  = token.tokens.get(index);
        //length check
        if(token.tokens.size() < 5){
            curCommandStatus = "[ERROR]Invalid SELECT command - no completed.";
            return curCommandStatus;
        }
        if(token.tokens.size() == 5){
            //System.out.println("TEST: In the insert valid command ");
            if(curToken.equals("*")) { // if * show total content
                index++;
                curToken = token.tokens.get(index); // should be 'FROM' now
                if (!curToken.equalsIgnoreCase("FROM")) {
                    curCommandStatus = "[ERROR]Missing or typo FROM here.";
                    return curCommandStatus;
                }
                index++;
                curToken = token.tokens.get(index).toLowerCase(); // should be table name now
                // DONE: Check the file(table) exists first then check the valid name.
                curCommandStatus = nameCheck(curToken);
                ArrayList<String> existTables = table.displayFiles(getCurDatabaseName());
                for (String t: existTables){
                    if((curToken + ".tab").contains(t)){
                        if (curCommandStatus.contains("[ERROR]")) {
                            return curCommandStatus;
                        }
                        curCommandStatus = "[OK]\n" + table.showFileContent(curToken, getCurDatabaseName());
                        return curCommandStatus;
                    }
                }
                curCommandStatus = "[ERROR]Selected table dos not exist.";
                return curCommandStatus;
            }else {// when the length = 5, current token be one attribute name;
                curCommandStatus = nameCheck(curToken);
                if(curCommandStatus.contains("[ERROR]")){
                    return curCommandStatus;
                }
                // check the attribute name is exist or not. Using flag function...
                // need to know table name first
            }
        }
        // In this situation, the SELECT may add some conditions or the attribute name is more than one
        if(token.tokens.size() >5){
            return "[OK]In the condition now,or the attribute name more than one";

        }
        curCommandStatus = "[ERROR]Select error.";
        return curCommandStatus;
    }
}

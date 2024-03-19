package edu.uob;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static edu.uob.GlobalMethod.getCurDatabaseName;

public class ParserSelectCommand extends DBParser{
    // The switch for control the show or not
    DBServer server;

    public ParserSelectCommand(String command, int index) {
        super(command);
        this.index = index;
        server = new DBServer();
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
                ArrayList<String> attributeNames = new ArrayList<>();
                attributeNames.add(curToken); // add the attributes
                // need to know table name first
                index++;
                curToken = token.tokens.get(index); // should be 'FROM' now
                if (!curToken.equalsIgnoreCase("FROM")) {
                    curCommandStatus = "[ERROR]Missing or typo FROM here.";
                    return curCommandStatus;
                }
                index++;
                curToken = token.tokens.get(index).toLowerCase(); // should be table name now
                String filePath = server.getStorageFolderPath() + File.separator + GlobalMethod.getCurDatabaseName()
                        + File.separator +curToken + ".tab";
                boolean exist = colIndexStorage(filePath,attributeNames);
                if(!exist){
                    curCommandStatus = "[ERROR]Attribute Name does not exist.";
                    return curCommandStatus;
                }
                // show the content
                curCommandStatus = showTheContent();
                curCommandStatus = "[OK]\n" + curCommandStatus;
//                System.out.println("test :" + exist);
//                System.out.println("tableContent" + tableContent);
//                System.out.println("tableCol" + tableCol);
//                System.out.println("tableRow" + tableRow);
                return curCommandStatus;
            }
        }
        // In this situation, the SELECT may add some conditions or the attribute name is more than one
        if(token.tokens.size() >5){
            // Situation 1 : consider the attribute name more than one
            ArrayList<String> attributes = new ArrayList<>();
            if(!curToken.equals("*")){ // attributes
                while(!token.tokens.get(index).equalsIgnoreCase("FROM")){
                    attributes.add(token.tokens.get(index));
                    System.out.println("Current toke in loop: " + token.tokens.get(index));
                    index++; // until equals from
                }
                curCommandStatus = attributeCheck(attributes);
                if(curCommandStatus.contains("[ERROR]")){
                    curCommandStatus = "[ERROR]Attribute or format error";
                    return curCommandStatus;
                }
                System.out.println("attributes now:"+attributes);
                for (int i = 0; i < attributes.size(); i++) {
                    String attribute = attributes.get(i);
                    if(attribute.equals(",")){
                        attributes.remove(i);
                        i--; // after remove
                    }
                }
                index++; // should be table name now
                curToken = token.tokens.get(index).toLowerCase(); // should be table name now
                System.out.println("current token now:"+curToken);
                System.out.println("attributes now:"+attributes);
                String filePath = server.getStorageFolderPath() + File.separator + GlobalMethod.getCurDatabaseName()
                        + File.separator +curToken + ".tab";
                boolean exist = colIndexStorage(filePath,attributes);
                if(!exist){
                    curCommandStatus = "[ERROR]Attribute Name does not exist.";
                    return curCommandStatus;
                }
                // show the content
                curCommandStatus = showTheContent();
                curCommandStatus = "[OK]\n" + curCommandStatus;
                System.out.println("attributes now: " + attributes);
            }
            // Situation 2 : it brings some condition
            //if(curToken.equals("*"))


            //return "[OK]In the condition now,or the attribute name more than one";
            return curCommandStatus;

        }
        return curCommandStatus;
    }
}

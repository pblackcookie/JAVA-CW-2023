package edu.uob;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static edu.uob.GlobalMethod.getCurDatabaseName;

public class ParserSelectCommand extends DBParser{
    // The switch for control the show or not
    DBServer server;
    ArrayList<String> attributes = new ArrayList<>();

    public ParserSelectCommand(String command, int index) {
        super(command);
        this.index = index;
        server = new DBServer();
    }

    protected String parserSelect() {
        String curToken  = token.tokens.get(index);
        int len = token.tokens.size();
        //length check
        if(len < 5){
            curCommandStatus = "[ERROR]Invalid SELECT command - no completed.";
            return curCommandStatus;
        }else if(len == 5){
            curCommandStatus = parserSelectFixedLen();
        }else{
            curCommandStatus = parserSelectVariableLen();
        }
        return curCommandStatus;
    }
    // Assume that length is the fixed 5
    private String parserSelectFixedLen() {
        String curToken  = token.tokens.get(index);
        if(curToken.equals("*")) {
            index++;
            curCommandStatus = fixedLengthAsterisk();
        }else{ // Now is one attribute name
            curCommandStatus = fixedLengthAttribute();
        }
        return curCommandStatus;

    }
    // select * from tableName ; -> always 5 length
    private String fixedLengthAsterisk(){
        try {
            String curToken = token.tokens.get(index); // should be 'FROM' now
            if (!curToken.equalsIgnoreCase("FROM")) {
                curCommandStatus = "[ERROR]Missing or typo FROM here.";
                return curCommandStatus;
            }
            index++;
            curToken = token.tokens.get(index).toLowerCase(); // should be table name now
            // DONE: Check the file(table) exists first then check the valid name.
            curCommandStatus = nameCheck(curToken);
            ArrayList<String> existTables = table.displayFiles(getCurDatabaseName());
            for (String t : existTables) {
                if ((curToken + ".tab").contains(t)) {
                    if (curCommandStatus.contains("[ERROR]")) {
                        return curCommandStatus;
                    }
                    curCommandStatus = "[OK]\n" + table.showFileContent(curToken, getCurDatabaseName());
                    return curCommandStatus;
                }
            }
            curCommandStatus = "[ERROR]Selected table dos not exist.";
            if (getCurDatabaseName() == null) {
                curCommandStatus = "[ERROR]Database does not set.";
                return curCommandStatus;
            }
            return curCommandStatus;
        }catch (IOException e){
            curCommandStatus = "[ERROR]IOExpaction.";
            return curCommandStatus;
        }
    }

    private String fixedLengthAttribute(){
        String curToken = token.tokens.get(index); //current token -> attribute name;
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
        curToken = token.tokens.get(index).toLowerCase(); // should be table name now'
        if(getCurDatabaseName() == null){
            curCommandStatus = "[ERROR]Database does not set.";
            return curCommandStatus;
        }
        filePath = server.getStorageFolderPath() + File.separator + getCurDatabaseName()
                + File.separator +curToken + ".tab";
        try {
            ArrayList<Integer> rowIndex = new ArrayList<>();
            boolean exist = colIndexStorage(filePath, attributeNames, rowIndex);
            if (!exist) {
                curCommandStatus = "[ERROR]Attribute Name does not exist.";
                return curCommandStatus;
            }// show the content
            curCommandStatus = showTheContent(rowIndex);
            curCommandStatus = "[OK]\n" + curCommandStatus;
            return curCommandStatus;
        }catch(Exception e){
            curCommandStatus = "[ERROR]";
            return curCommandStatus;
        }
    }

    // multiple -> so the length is variable
    private String parserSelectVariableLen() {
        String curToken = token.tokens.get(index);
        if(curToken.equals("*")) {
            index++;
            curCommandStatus = variableLengthAsterisk();
        }else{ // Now is one attribute name
            curCommandStatus = variableLengthAttribute();
        }
        return curCommandStatus;
    }

    private String variableLengthAsterisk() {
        String curToken;
        // Situation 2 : it brings some condition
        // "SELECT " <WildAttribList> " FROM " [TableName] " WHERE " <Condition>
        // cur token now must be * ...
        index++;
        curToken = token.tokens.get(index);
        if (!curToken.equalsIgnoreCase("FROM")){
            curCommandStatus = "[ERROR]Missing or typo 'from'.";
            return curCommandStatus;
        }
        index++; // table name now
        curToken = token.tokens.get(index).toLowerCase();
        filePath = server.getStorageFolderPath() + File.separator + getCurDatabaseName()
                + File.separator +curToken + ".tab";
        File file = new File(filePath);
        if(!file.exists()){
            curCommandStatus = "[ERROR]File does not exists.";
            return curCommandStatus;
        }
        index++; // should be where now
        curToken = token.tokens.get(index);
        if (curToken.equalsIgnoreCase("WHERE")){
            curCommandStatus ="[ERROR]Missing or typo 'where'.";
        } // where attribute operation message
        try {
            ArrayList<Integer> rowIndex = multipleConditionCheck();
            System.out.println("cur commandstatus: " + curCommandStatus);
            System.out.println("tablecol" + tableCol);
            System.out.println("tablerow" + tableRow);
            if (curCommandStatus.contains("[ERROR]")) {
                return curCommandStatus;
            }
            curCommandStatus = showTheContent(rowIndex);
            curCommandStatus = "[OK]\n" + curCommandStatus;
            return curCommandStatus;
        }catch (Exception e){
            return "[ERROR]";
        }
    }

    private String variableLengthAttribute()  {
        String curToken = token.tokens.get(index);
        // Situation 1 : consider the attribute name more than one
        ArrayList<String> attributesCheck = new ArrayList<>();
        if(!curToken.equals("*")){ // attributes
            while(!token.tokens.get(index).equalsIgnoreCase("FROM")){
                attributesCheck.add(token.tokens.get(index));
                attributes.add(token.tokens.get(index));
                index++; // until equals from
            }
            curCommandStatus = attributeCheck(attributesCheck);
            if(curCommandStatus.contains("[ERROR]")){
                curCommandStatus = "[ERROR]Attribute or format error";
                return curCommandStatus;
            }
            for (int i = 0; i < attributes.size(); i++) {
                String attribute = attributes.get(i);
                if(attribute.equals(",")){
                    attributes.remove(i);
                    i--; // after remove
                }
            }
            setSelectAttribute(attributes);
            index++; // should be table name now
            curToken = token.tokens.get(index).toLowerCase(); // should be table name now
            filePath = server.getStorageFolderPath() + File.separator + getCurDatabaseName()
                    + File.separator +curToken + ".tab";
            File file = new File(filePath);
            if(!file.exists()){
                curCommandStatus = "[ERROR]File does not exists.";
                return curCommandStatus;
            }
            try {
                ArrayList<Integer> rowIndex = new ArrayList();
                rowIndex = multipleConditionCheck();
                boolean exist = colIndexStorage(filePath, attributes, rowIndex);
                if (!exist) {
                    curCommandStatus = "[ERROR]Attribute Name does not exist.";
                    return curCommandStatus;
                }
            }catch (Exception e){
                curCommandStatus = "[ERROR]";
                return curCommandStatus;
            }
            // show the content
            if(token.tokens.size()-2 == index){ // no where condition on here
                //curCommandStatus = "[OK]\n" + showTheContent();
                return curCommandStatus;
            }else {
                // todo : add WHERE CONDITION in here
                System.out.println("NOW IN THE SELECT WHERE CONDITION");
                index++; // now check is where
                if (curToken.equalsIgnoreCase("WHERE")){
                    curCommandStatus ="[ERROR]Missing or typo 'where'.";
                } // where attribute operation message
                index++; // should be attribute now

                try {
                    ArrayList<Integer> rowIndex1 = new ArrayList<>();
                    rowIndex1 = multipleConditionCheck();
                    // add error situation here
                    if (curCommandStatus.contains("[ERROR]")) {
                        return curCommandStatus;
                    }
                    curCommandStatus = "[OK]\n" + strictShowTheContent(rowIndex1);
                    return curCommandStatus;
                }catch(Exception e){
                    curCommandStatus = "[ERROR]";
                    return curCommandStatus;
                }

            }
        }
        return curCommandStatus;
    }

    private String strictShowTheContent (ArrayList<Integer> rowIndex){
        StringBuilder newString = new StringBuilder();
        System.out.println("In select attributes:" + attributes);
        for (int i = 0; i < rowIndex.size(); i++) {
            for (int j = 0; j < tableCol.size(); j++) {
                if(!rowIndex.get(i).equals(-1)&& !tableCol.get(j).equals(-1)) {
                    for (String attribute : attributes) {
                        if (tableContent.get(j).equalsIgnoreCase(attribute)) {
                            if (j == rowIndex.size() - 1) {
                                newString.append(tableContent.get(i * tableCol.size() + j));
                                newString.append("\t");
                            } else {
                                newString.append(tableContent.get(i * tableCol.size() + j));
                                newString.append("\t");
                            }
                        }
                    }
                }
            }
            newString.append("\n");
        }
        curCommandStatus = newString.toString().trim();
        return curCommandStatus;
    }
}

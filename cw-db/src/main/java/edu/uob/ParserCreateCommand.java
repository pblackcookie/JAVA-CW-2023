package edu.uob;

import java.io.IOException;
import java.util.ArrayList;

import static edu.uob.GlobalMethod.getCurDatabaseName;
import static edu.uob.GlobalMethod.setCurTableName;

public class ParserCreateCommand extends DBParser{
    protected ParserCreateCommand(String command, int index) {
        super(command);
        this.index = index; // Now should be the database or table
    }
    // When command type = 'CREATE'
    // One thing need to be considered: table may with attributes.
    protected String parserCreate() throws IOException {
        String curToken = token.tokens.get(index);
        switch (curToken.toUpperCase()) {
            case "DATABASE":
                index++;
                parserCreateDatabase();
                break;
            case "TABLE":
                index++;
                parserCreateTable();
                break;
            default:
                curCommandStatus = "[ERROR]Invalid create command. Please use [TABLE] or [DATABASE]";
        }
        return curCommandStatus;
    }

    private String parserCreateDatabase() throws IOException {
        //System.out.println("token size: " + token.tokens.size());
        String curToken = token.tokens.get(index);
        if(token.tokens.size()!=4){
            curCommandStatus = "[ERROR]Invalid create database command.";
            return curCommandStatus;
        }
        curCommandStatus = nameCheck(curToken);
        if(curCommandStatus.contains("[ERROR]")){
            return curCommandStatus;
        }
        curCommandStatus = database.createDatabase(curToken);
        return curCommandStatus;
    }


    // Two different situation: 1. Just create the table. 2. With the attributes
    // situation 1 "CREATE TABLE tableName ; "
    // situation 2 "CREATE TABLE TableName ( att1 , att2 , att3 ); "
    // need to store the current table
    // TODO implement the logic and check in the (); when create table with attributes
    private String parserCreateTable() throws IOException {
        String curToken = token.tokens.get(index);
        if(token.tokens.size() == 3 ){
            curCommandStatus = "[ERROR]Not have enough length";
            return curCommandStatus;
        }
        if(token.tokens.size() != 4 && token.tokens.get(index+1).equals(";")){
            curCommandStatus = "[ERROR]Invalid create table command.";
            return curCommandStatus;
        }
        // Check the name firstly
        curCommandStatus = nameCheck(curToken);
        if(curCommandStatus.contains("[ERROR]")){
            return curCommandStatus;
        }
        setCurTableName(curToken);
        String curDatabase = getCurDatabaseName();
        if(curDatabase != null) {
            index++; // now it is in ( or ; if the syntax is correct
            //System.out.println(token.tokens.get(index));
            if(token.tokens.get(index).equals(";")) {
                curCommandStatus = table.createFile(curToken, curDatabase);
                return curCommandStatus;
            }else if(token.tokens.get(index).equals("(")){
                // TODO : In here imaging a series of error may occur....Need to implement
                // DONE : 1. Check the attribute after the symbol "("
                // TODO : Attribute list valid check
                ArrayList<String> InAttributes = new ArrayList<>();
                for(int i = index+1; i < token.tokens.size()-2; i++){  // size-1: ; size-2: ) size-3: should be the attribute
                    InAttributes.add(token.tokens.get(i)); // should not have any ( or ) now
                    //System.out.println(InAttributes);
                }
                curCommandStatus = attributeCheck(InAttributes);
                if(curCommandStatus.contains("[ERROR]")){
                    return curCommandStatus;
                } // End of to check the valid attribute name
                String attributeName = token.tokens.get(index+1);
                //System.out.println("EXPECT: " + attributeName);
                curCommandStatus = nameCheck(attributeName);
                if (curCommandStatus.contains("[ERROR]")){
                    return curCommandStatus;
                }
                if(!token.tokens.get(token.tokens.size() - 2).equals(")")){
                    // In order to prevent the situation like 'create table test(ss, mark, kkk)deaf;' occur.
                    curCommandStatus = "[ERROR]Invalid format: Error occurs between ')' and ';'. ";
                    return curCommandStatus;
                }
                // Pass all check then create the table
                for (int i = index+1; i < token.tokens.size()-2; i++) {
                    if (!token.tokens.get(i).equals(",")) {
                        attributes.add(token.tokens.get(i));
                    }
                }
                curCommandStatus = table.createFile(curToken, curDatabase, attributes);
                return curCommandStatus;
            }else{
                curCommandStatus = "[ERROR]Invalid syntax in attributes";
                return curCommandStatus;
            }
        }
        curCommandStatus = "[ERROR]Please choose use database first.";
        return curCommandStatus;
    }
}

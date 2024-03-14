package edu.uob;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static edu.uob.GlobalMethod.*;

public class DBParser {
    private int index = 0; // use to indicate the current token
    private String curCommandStatus;

    private String id = ".id";
    ArrayList<String> attributes = new ArrayList<>();
    ArrayList<String> data = new ArrayList<>();
    CommandToken token = new CommandToken(); // storage all tokens
    DatabaseProcess database = new DatabaseProcess();
    FileProcess table = new FileProcess();

    ArrayList<String> symbol = new ArrayList<>(Arrays.asList("!", "#", "$","%","&","(",")","*","+",",","-",".","/", ":",";",
            ">","=","<","?","@","[","\\","]","^","_","`","{","}","~")); //29

    ArrayList<String> keyWords = new ArrayList<>(Arrays.asList("USE","CREATE","DROP","ALTER","INSERT","SELECT","UPDATE",
            "DELETE","JOIN","TRUE","FALSE","DATABASE","TABLE","INTO","VALUES","FROM","WHERE","SET","AND","ON","ADD",
            "OR", "NULL","LIKE")); //24


    public DBParser(String command){
        token.setup(command); // get all tokens from command
    }

    public String parserCommand() throws IOException {
        // check the ';' on the end -> only one simple syntax check
        if(!token.tokens.get(token.tokens.size() - 1).equals(";")){
            return "[ERROR]Invalid format: Please end the command with ';'";
        } else {
            return parserCommandType();
        }
    }

    private String parserCommandType() throws IOException {
        String curToken = token.tokens.get(index);
        switch (curToken.toUpperCase()){
            case "USE":
                index++;
                parserUse();
                break;
            case "CREATE":
                index++;
                parserCreate();
                break;
            case "DROP":
                index++;
                parserDrop();
                break;
            case "ALTER": //parserAlter();
            case "INSERT":
                index++;
                parserInsert();
                break;
            case "SELECT": //parserSelect();
            case "UPDATE": //parserUpdate();
            case "DELETE": //parserDelete();
            case "JOIN": //parserJoin();
                break;
            default:
                curCommandStatus = "[ERROR]Invalid commandType";
        }
        return curCommandStatus;
    }
    //When command type = 'USE'
    private String parserUse() throws IOException {
        String curToken = token.tokens.get(index);
        if(token.tokens.size() != 3){
            curCommandStatus = "[ERROR]Invalid syntax.";
            return curCommandStatus;
        }
        curCommandStatus = nameCheck(curToken);
        if (curCommandStatus.contains("[ERROR]")){
            return curCommandStatus;
        }
        curCommandStatus = database.useDatabase(curToken);
        setCurDatabaseName(curToken);
        return curCommandStatus;
    }
    // When command type = 'CREATE'
    // One thing need to be considered: table may with attributes.
    private String parserCreate() throws IOException {
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
        System.out.println("token size: " + token.tokens.size());
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
    private String parserCreateTable() throws IOException {
        String curToken = token.tokens.get(index);
        if(token.tokens.size() != 4 && token.tokens.get(index+1).equals(";")){
            curCommandStatus = "[ERROR]Invalid create table command.";
            return curCommandStatus;
        }
        curCommandStatus = nameCheck(curToken);
        if(curCommandStatus.contains("[ERROR]")){
            return curCommandStatus;
        }
        setCurTableName(curToken);
        String curDatabase = getCurDatabaseName();
        if(curDatabase != null) {
            index++; // now it is in ( or ; if the syntax is correct
            if(token.tokens.get(index).equals(";")) {
                curCommandStatus = table.createFile(curToken, curDatabase);
                return curCommandStatus;
            }else if(token.tokens.get(index).equals("(")){
                // In here imaging a series of error may occur....Need to implement
                if(!token.tokens.get(token.tokens.size() - 2).equals(")")){
                    // In order to prevent the situation like 'create table test(ss, mark, kkk)deaf;' occur.
                    curCommandStatus = "[ERROR]Invalid format: Error occurs between ')' and ';'. ";
                    return curCommandStatus;
                }
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

    // When command type = 'DROP'
    private String parserDrop() throws IOException {
        String curToken = token.tokens.get(index);
        switch (curToken.toUpperCase()) {
            case "DATABASE":
                index++;
                parserDropDatabase();
                break;
            case "TABLE":
                index++;
                parserDropTable();
                break;
            default:
                //System.out.println("Invalid drop command. Please use [TABLE] or [DATABASE]");
                curCommandStatus = "[ERROR]Invalid drop command. Please use [TABLE] or [DATABASE]";
        }
        return curCommandStatus;
    }
    private String parserDropDatabase() throws IOException {
        String curToken = token.tokens.get(index);
        curCommandStatus = database.dropDatabase(curToken);
        return curCommandStatus;
    }
    private String parserDropTable() throws IOException {
        String curToken = token.tokens.get(index);
        String curDatabase = getCurDatabaseName();
        curCommandStatus = table.dropFile(curToken,curDatabase);
        return curCommandStatus;
    }


    // // When command type = 'INSERT'
    private String parserInsert() throws IOException {
        int idNumber;
        String filePath = database.getCurDatabasePath(getCurDatabaseName()) + File.separator + token.tokens.get(index+1) + ".tab";
        String curToken = token.tokens.get(index);
        if(!curToken.equalsIgnoreCase("INTO")){
            curCommandStatus = "[ERROR] Missing or wrong the 'INTO'";
            return curCommandStatus;
        }else{
            index++; // should be the table name now
            curToken = token.tokens.get(index);
            ArrayList<String> curFiles = new ArrayList<>();
            curFiles = table.displayFiles(getCurDatabaseName());
            if(!curFiles.contains(curToken + ".tab")){
                curCommandStatus = "[ERROR]: Select file doesn't exists.";
                return curCommandStatus;
            }
            // Table exists ,so Read the id file to see which id it should be now
            String IdRecordPath = database.getCurDatabasePath(getCurDatabaseName()) + File.separator + curToken + id;
            BufferedReader reader = new BufferedReader(new FileReader(IdRecordPath));
            String line = reader.readLine();
            idNumber = Integer.parseInt(line) + 1;
            // Update the value and write back to the .id file
            FileWriter writer = new FileWriter(IdRecordPath);
            BufferedWriter buffer = new BufferedWriter(writer);
            buffer.write(String.valueOf(idNumber));
            buffer.close();
            writer.close();
            index++; // should be the "VALUES" now
            curToken = token.tokens.get(index);
            if(!curToken.equalsIgnoreCase("VALUES")){
                curCommandStatus = "[ERROR] Missing or typo 'VALUES'.";
                return curCommandStatus;
            }
            index++; // should be the '(' now
            curToken = token.tokens.get(index);
            if(!curToken.equalsIgnoreCase("(")){
                curCommandStatus = "[ERROR] Missing or typo '('.";
                return curCommandStatus;
            }
            if(!token.tokens.get(token.tokens.size() - 2).equals(")")) {
                // In order to prevent the situation like 'create table test(ss, mark, kkk)deaf;' occur.
                curCommandStatus = "[ERROR]Invalid format: Error occurs between ')' and ';'. ";
                return curCommandStatus;
            }
            data.add(String.valueOf(idNumber));
            // For loop to store the data
            for (int i = index+1; i < token.tokens.size()-2; i++) { // should be the data now
                if (!token.tokens.get(i).equals(",")) {
                    data.add(token.tokens.get(i));
                }
            }
        }
        curCommandStatus = table.addFileContent(data, filePath);
        return curCommandStatus;
    }


    // Check the database name or table name is valid or not
    // Contain changing
    private String nameCheck(String curName){
        curName = curName.toUpperCase();
        for (String s :symbol) {
            if(curName.contains(s)){
                return "[ERROR]Name contains illegal symbol(s): " + s;
            }
            for(String word: keyWords){
                if(curName.contains(word)){
                    return "[ERROR]Name contains keyword(s): " + word;
                }
            }
        }
        return "[OK]Valid Name";
    }
}

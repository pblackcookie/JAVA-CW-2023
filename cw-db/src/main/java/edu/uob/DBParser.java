package edu.uob;

import java.io.IOException;
import java.util.ArrayList;

public class DBParser {
    private int index; // use to indicate the current token
    private String curCommandStatus;
    ArrayList<String> attributes = new ArrayList<>();
    CommandToken token = new CommandToken(); // storage all tokens
    DatabaseProcess database = new DatabaseProcess();
    FileProcess table = new FileProcess();

    public DBParser(String command){
        token.setup(command); // get all tokens from command
        index = 0; // initialise the index
    }

    public String parserCommand() throws IOException {
        // check the ';' on the end
        if(!token.tokens.get(token.tokens.size() - 1).equals(";")){
            return "[ERROR]Invalid format: Please end the command with ';'";
        }else {
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
            case "INSERT": //parserInsert();
            case "SELECT": //parserSelect();
            case "UPDATE": //parserUpdate();
            case "DELETE": //parserDelete();
            case "JOIN": //parserDrop();
                break;
            default:
                curCommandStatus = "[ERROR]Invalid commandType";
        }
        return curCommandStatus;
    }
    //When command type = 'USE'
    private String parserUse() throws IOException {
        String curToken = token.tokens.get(index);
        curCommandStatus = database.useDatabase(curToken);
        GlobalMethod.setCurDatabaseName(curToken);
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
        String curToken = token.tokens.get(index);
        curCommandStatus = database.createDatabase(curToken);
        return curCommandStatus;
    }
    // Two different situation: 1. Just create the table. 2. With the attributes
    // situation 1 "CREATE TABLE tableName ; "
    // situation 2 "CREATE TABLE TableName ( att1 , att2 , att3 ); "
    private String parserCreateTable() throws IOException {
        String curToken = token.tokens.get(index);
        String curDatabase = GlobalMethod.getCurDatabaseName();
        System.out.println("TEST: " + curDatabase);
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
                        System.out.println(attributes);
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
        String curDatabase = GlobalMethod.getCurDatabaseName();
        curCommandStatus = table.dropFile(curToken,curDatabase);
        return curCommandStatus;
    }


}

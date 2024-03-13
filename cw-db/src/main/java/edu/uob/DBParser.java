package edu.uob;

import java.io.IOException;

public class DBParser {
    private int index; // use to indicate the current token
    private String curDatabaseName; // use to store current database name.
    CommandToken token = new CommandToken(); // storage all tokens
    DatabaseProcess database = new DatabaseProcess();
    FileProcess table = new FileProcess();
    public DBParser(String command){
        token.setup(command); // get all tokens from command
        index = 0; // initialise the index
    }
    public boolean syntaxCheck(String command){
        // Check if this command is valid. -> normal syntax check
        if(command.endsWith(";")){
            return true;
        }else {
            return false;
        }
    }

    public void parserCommand(String command) throws IOException {
        //return parserCommandType() && match(";");
        parserCommandType();
        //return parserCommandType();
    }

    private void parserCommandType() throws IOException {
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
            case "DROP": //parserDrop();
            case "ALTER": //parserAlter();
            case "INSERT": //parserInsert();
            case "SELECT": //parserSelect();
            case "UPDATE": //parserUpdate();
            case "DELETE": //parserDelete();
            case "JOIN": //parserDrop();
                //index++;
                break;
            default:
                System.out.println("Invalid commandType");
        }
    }

    private void parserUse() throws IOException {
        String curToken = token.tokens.get(index);
        database.useDatabase(curToken);
        //setCurDatabaseName(curToken); need to store it
        System.out.println(curDatabaseName);
    }

    private void parserCreate() throws IOException {
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
                System.out.println("Invalid create type. Please use [TABLE] or [DATABASE]");
        }
    }

    private void parserCreateDatabase() throws IOException {
        String curToken = token.tokens.get(index);
        database.createDatabase(curToken);
    }
    private void parserCreateTable() throws IOException {
        String curToken = token.tokens.get(index);
        //String curDatabase = getCurDatabaseName();
        //table.createFile(curToken, getCurDatabaseName());
    }
}

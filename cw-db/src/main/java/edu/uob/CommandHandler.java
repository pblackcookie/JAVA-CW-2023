package edu.uob;

import java.io.IOException;
import java.util.ArrayList;

public class CommandHandler {
    DBParser parser; //Only has some values and public methods....
    protected int index; // use to indicate the current token
    CommandToken token;
    protected String curCommandStatus;
    protected String curCommand;
    ArrayList<String> attributes;
    ArrayList<String> data;
    DatabaseProcess database;
    FileProcess table;
    public CommandHandler(String command) {
        parser = new DBParser(command);
        this.index = parser.index;
        this.token = parser.token;
        this.curCommandStatus = parser.curCommandStatus;
        this.attributes = parser.attributes;
        this.data = parser.data;
        this.database = parser.database;
        this.table = parser.table;
    }
    public String commandHandler() throws IOException {
        return parserCommand();
    }

    protected String parserCommand() throws IOException {
        // check the ';' on the end -> only one simple syntax check
        if(!token.tokens.get(token.tokens.size() - 1).equals(";")){
            curCommandStatus = "[ERROR]Invalid format: Please end the command with ';'";
            return curCommandStatus;
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
                ParserCreateCommand p = new ParserCreateCommand(curCommand,index);
                curCommandStatus =  p.parserCreate();
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
            case "SELECT":
                index++;
                parserSelect();
                break;
            case "UPDATE": //parserUpdate();
            case "DELETE": //parserDelete();
            case "JOIN": //parserJoin();
                break;
            default:
                curCommandStatus = "[ERROR]Invalid commandType";
        }
        return curCommandStatus;
    }

}

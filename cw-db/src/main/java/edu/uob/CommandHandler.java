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
        this.curCommand = command;
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
                ParserUseCommand pUse = new ParserUseCommand(curCommand,index);
                curCommandStatus = pUse.parserUse();
                break;
            case "CREATE":
                index++;
                ParserCreateCommand pCreate = new ParserCreateCommand(curCommand,index);
                curCommandStatus =  pCreate.parserCreate();
                break;
            case "DROP":
                index++;
                ParserDropCommand pDrop = new ParserDropCommand(curCommand,index);
                curCommandStatus =  pDrop.parserDrop();
                break;
            case "ALTER":
                index++;
                ParserAlterCommand pAlter = new ParserAlterCommand(curCommand,index);
                curCommandStatus = pAlter.parserAlter();
                break;
            case "INSERT":
                index++;
                ParserInsertCommand pInsert = new ParserInsertCommand(curCommand,index);
                curCommandStatus = pInsert.parserInsert();
                break;
            case "SELECT":
                index++;
                ParserSelectCommand pSelect = new ParserSelectCommand(curCommand,index);
                curCommandStatus = pSelect.parserSelect();
                break;
            case "UPDATE":
//                index++;
//                ParserUpdateCommand parserUpdateCommand = new ParserUpdateCommand(curCommand,index);
//                curCommandStatus = pUpdate.parserUpdate();
                break;
            case "DELETE": //parserDelete();
                index++;
                ParserDeleteCommand pDelete = new ParserDeleteCommand(curCommand,index);
                curCommandStatus = pDelete.parserDelete();
                break;
            case "JOIN":
                index++;
                ParserJoinCommand pJoin = new ParserJoinCommand(curCommand,index);
                curCommandStatus = pJoin.parserJoin();
                break;
            default:
                curCommandStatus = "[ERROR]Invalid commandType";
        }
        return curCommandStatus;
    }

}

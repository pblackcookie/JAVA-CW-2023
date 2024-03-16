package edu.uob;

import java.io.IOException;

import static edu.uob.GlobalMethod.getCurDatabaseName;
import static edu.uob.GlobalMethod.setCurDatabaseName;

public class ParserDropCommand extends DBParser{

    public ParserDropCommand(String command, int index) {
        super(command);
        this.index = index;
    }
    // When command type = 'DROP'
    protected String parserDrop() throws IOException {
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
        String curToken = token.tokens.get(index).toLowerCase();
        curCommandStatus = database.dropDatabase(curToken);
        setCurDatabaseName(null); //
        //System.out.println(getCurDatabaseName());
        return curCommandStatus;
    }
    private String parserDropTable() throws IOException {
        String curToken = token.tokens.get(index).toLowerCase();
        String curDatabase = getCurDatabaseName();
        curCommandStatus = table.dropFile(curToken,curDatabase);
        return curCommandStatus;
    }
}

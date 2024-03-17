package edu.uob;

import java.io.IOException;

import static edu.uob.GlobalMethod.getCurDatabaseName;
import static edu.uob.GlobalMethod.setCurDatabaseName;

public class ParserDropCommand extends DBParser{
    //"DROP " "DATABASE " [DatabaseName] | "DROP " "TABLE " [TableName]
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
                curCommandStatus = "[ERROR]Invalid drop command. Please use [TABLE] or [DATABASE]";
        }
        return curCommandStatus;
    }
    private String parserDropDatabase() throws IOException {
        String curToken = token.tokens.get(index).toLowerCase();
        curCommandStatus = database.dropDatabase(curToken);
        // if delete the current using database, then set the current
        // database name equals to null. else do not need to operation
        if(curToken.equals(getCurDatabaseName())) {
            setCurDatabaseName(null);
        }
        return curCommandStatus;
    }
    // when drop table command , because it only can delete the current database table so
    // does not need to see the current database name is what.
    private String parserDropTable() throws IOException {
        String curToken = token.tokens.get(index).toLowerCase();
        String curDatabase = getCurDatabaseName();
        curCommandStatus = table.dropFile(curToken,curDatabase);
        return curCommandStatus;
    }
}

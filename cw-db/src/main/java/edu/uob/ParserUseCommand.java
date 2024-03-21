package edu.uob;
import static edu.uob.GlobalMethod.setCurDatabaseName;

public class ParserUseCommand extends DBParser{
    protected ParserUseCommand(String command, int index) {
        super(command);
        this.index = index; // Now should be the database or table
    }
    protected String parserUse(){
        String curToken = token.tokens.get(index).toLowerCase();
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
}

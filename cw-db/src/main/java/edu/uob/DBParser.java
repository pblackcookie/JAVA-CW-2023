package edu.uob;

import java.util.List;

public class DBParser {
    private List<String> tokens;
    private int index;
    CommandToken token = new CommandToken();
    public DBParser(String command){
        this.index = 0;
        tokens = token.setup(command);
        this.tokens = tokens;
    }

    public boolean syntaxCheck(String command){
        // Check if this command is valid. -> normal syntax check
        if(command.endsWith(";")){
            return true;
        }else {
            return false;
        }
    }

    public void parserCommand(String command){
        //return parserCommandType() && match(";");
        return parserCommandType();
    }

    private void parserCommandType() {
        
    }

}

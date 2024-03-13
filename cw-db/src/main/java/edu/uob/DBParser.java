package edu.uob;

import java.io.IOException;
import java.util.List;

public class DBParser {
    private List<String> tokens;
    private int index;
    CommandToken token = new CommandToken();
    DatabaseProcess database = new DatabaseProcess();
    FileProcess table = new FileProcess();
    public DBParser(String command){

        tokens = token.setup(command);
        index = 0;
        for (String token : tokens) {
            System.out.println(token);
        }

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
        //String token = tokens.get(index);
        switch ((tokens.get(index)).toUpperCase()){
            case "USE":
                parserUse();
                break;
            case "CREATE": //parserCreate();
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
        index++;
        String token = tokens.get(index);
        database.useDatabase(tokens.get(index));
    }

}

package edu.uob;

import java.io.IOException;

public class CommandHandler {
    DBParser parser; //Only has some values and public methods....
    public CommandHandler(String command) {
        parser = new DBParser(command);
    }
    public String commandHandler() throws IOException {
        return parser.parserCommand();
    }


}

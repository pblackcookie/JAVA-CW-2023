package edu.uob;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.nio.file.Files;

/** This class implements the DB server. */
public class DBServer {

    private static final char END_OF_TRANSMISSION = 4;
    private String storageFolderPath;
    private String curDatabaseName;

    public static void main(String args[]) throws IOException {
        DBServer server = new DBServer();
        server.blockingListenOn(8888);
    }

    /**
    * KEEP this signature otherwise we won't be able to mark your submission correctly.
    */
    public DBServer() {
        storageFolderPath = Paths.get("databases").toAbsolutePath().toString();
        try {
            // Create the database storage folder if it doesn't already exist !
            Files.createDirectories(Paths.get(storageFolderPath));
        } catch(IOException ioe) {
            System.out.println("Can't seem to create database storage folder " + storageFolderPath);
        }
    }

    /**
    * KEEP this signature (i.e. {@code edu.uob.DBServer.handleCommand(String)}) otherwise we won't be
    * able to mark your submission correctly.
    *
    * <p>This method handles all incoming DB commands and carries out the required actions.
    */
    public String handleCommand(String command) throws IOException {
        // TODO implement your server logic here
        // Parser need to created in here
        CommandToken token = new CommandToken();
        DatabaseProcess database = new DatabaseProcess();
        FileProcess table = new FileProcess();
        token.setup(command);
        if(token.tokens.get(0).equalsIgnoreCase("USE")){
             database.useDatabase(token.tokens.get(1));
             setCurDatabaseName(token.tokens.get(1));
        }
        if(token.tokens.get(0).equalsIgnoreCase("CREATE")){
            if(token.tokens.get(1).equalsIgnoreCase("DATABASE")){
                //DatabaseProcess database = new DatabaseProcess();
                database.createDatabase(token.tokens.get(2));
            }else if(token.tokens.get(1).equalsIgnoreCase("TABLE")){
                table.createFile(token.tokens.get(2),getCurDatabaseName());
            }
        }
        if(token.tokens.get(0).equalsIgnoreCase("DROP")){
            if(token.tokens.get(1).equalsIgnoreCase("DATABASE")){
                database.dropDatabase(token.tokens.get(2));
            }else if(token.tokens.get(1).equalsIgnoreCase("TABLE")){
                table.dropFile(token.tokens.get(2),getCurDatabaseName());
            }
        }
        // Check if this command is valid.
        if(command.endsWith(";")){
            return "[OK]";
        }else {
            return "[ERROR]";
        }
    }
    // Using for store current database name.
    public void setCurDatabaseName(String databaseName){
        this.curDatabaseName = databaseName;
    }
    public String getCurDatabaseName(){ return this.curDatabaseName;}
    // For file class can know the folder path
    public String getStorageFolderPath() {
        return storageFolderPath;
    }

    //public String getCommandString() { return commandString; }
    //  === Methods below handle networking aspects of the project - you will not need to change these ! ===

    public void blockingListenOn(int portNumber) throws IOException {
        try (ServerSocket s = new ServerSocket(portNumber)) {
            System.out.println("Server listening on port " + portNumber);
            while (!Thread.interrupted()) {
                try {
                    blockingHandleConnection(s);
                } catch (IOException e) {
                    System.err.println("Server encountered a non-fatal IO error:");
                    e.printStackTrace();
                    System.err.println("Continuing...");
                }
            }
        }
    }

    private void blockingHandleConnection(ServerSocket serverSocket) throws IOException {
        try (Socket s = serverSocket.accept();
        BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()))) {

            System.out.println("Connection established: " + serverSocket.getInetAddress());
            while (!Thread.interrupted()) {
                String incomingCommand = reader.readLine();
                System.out.println("Received message: " + incomingCommand);
                String result = handleCommand(incomingCommand);
                writer.write(result);
                writer.write("\n" + END_OF_TRANSMISSION + "\n");
                writer.flush();
            }
        }
    }
}

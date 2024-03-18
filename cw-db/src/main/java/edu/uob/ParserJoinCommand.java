package edu.uob;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

public class ParserJoinCommand extends DBParser{
    private final DBServer server;
    private ArrayList<Boolean> table1Col;
    private ArrayList<Boolean> table1Row;
    private ArrayList<Boolean> table2Col;
    private ArrayList<Boolean> table2Row;
    private ArrayList<String> table1Content;
    private ArrayList<String> table2Content;


    public ParserJoinCommand(String command, int index) {
        super(command);
        this.index = index;
        server = new DBServer();
        table1Col = new ArrayList<Boolean>();
        table1Row = new ArrayList<Boolean>();
        table1Content = new ArrayList<String>();
        table2Col = new ArrayList<Boolean>();
        table2Row = new ArrayList<Boolean>();
        table2Content = new ArrayList<String>();

    }
    // <Join> ::=  "JOIN " [TableName] " AND " [TableName] " ON " [AttributeName] " AND " [AttributeName]
    protected String parserJoin() throws IOException {
        if (token.tokens.size()!=9){
            curCommandStatus = "Invalid command cause the incorrect length";
            return curCommandStatus;
        }
        String curToken = token.tokens.get(index).toLowerCase(); // Table1 now
        //Already check the format when create the table
        String filePath1 = server.getStorageFolderPath() + File.separator + GlobalMethod.getCurDatabaseName()
                + File.separator +curToken + ".tab";
        System.out.println(filePath1);
        Path path1 = Paths.get(filePath1);
        if (!Files.exists(path1)){
            curCommandStatus = "[ERROR]Can't find the first table: " + curToken;
            return curCommandStatus;
        }
        index++; // now the current token should be "AND"
        curToken = token.tokens.get(index);
        if(!curToken.equalsIgnoreCase("AND")){
            curCommandStatus = "Missing or typo [AND] when typing command";
            return curCommandStatus;
        }
        index++; // now the current token should be the table2 name
        curToken = token.tokens.get(index).toLowerCase();
        String filePath2 = server.getStorageFolderPath() + File.separator + GlobalMethod.getCurDatabaseName()
                + File.separator +curToken + ".tab";
        System.out.println(filePath2);
        Path path2 = Paths.get(filePath2);
        if (!Files.exists(path2)){
            curCommandStatus = "[ERROR]Can't find the second table: " + curToken;
            return curCommandStatus;
        }
        index++; // now the current token should be "ON";
        curToken = token.tokens.get(index);
        if(!curToken.equalsIgnoreCase("ON")){
            curCommandStatus = "Missing or typo [ON] when typing command";
            return curCommandStatus;
        }
        index++; // now the current token should attribute name from table1;
        curToken = token.tokens.get(index);
        // if condition
        if(!checkTable(filePath1,curToken)){
            curCommandStatus = "Attribute is not exist or invalid format";
            return curCommandStatus;
        }
        index++; // now the current token should be "AND"
        curToken = token.tokens.get(index);
        if(!curToken.equalsIgnoreCase("AND")){
            curCommandStatus = "Missing or typo [AND] when typing command";
            return curCommandStatus;
        }
        index++; // now the current token should attribute name from table2;
        curToken = token.tokens.get(index);
        if(!checkTable(filePath1,curToken)){
            curCommandStatus = "Attribute is not exist or invalid format";
            return curCommandStatus;
        }
        // Satisfy all the conditions so do the command in here
        return "[OK]\n" + joinTable(filePath1,filePath2);
    }

    // Set the row and column flags
    // return the boolean value to indicate the attribute name is exist or not
    private boolean checkTable(String filePath, String attributeName) throws IOException {
        boolean exist = false;
        ArrayList<String> lineContent = new ArrayList<String>();
        BufferedReader tableReader = new BufferedReader(new FileReader(filePath));
        String line = tableReader.readLine();
        lineContent.add(Arrays.toString(line.split("\t")));
        for(String attribute: lineContent){
            if(attribute.equalsIgnoreCase(attributeName)){
                table1Col.add(false); // not show on the table
                exist = true;
            }else{
                table1Col.add(true);
            }
        }
        return exist;
    }
    // In here do the operation to join two tables
    // just using buffer reader to change the content
    private String joinTable(String path1, String path2){
        return curCommandStatus;
    }
}

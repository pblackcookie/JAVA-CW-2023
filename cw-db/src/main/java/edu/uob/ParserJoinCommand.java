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
import java.util.Objects;

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
        table1Col = new ArrayList<>();
        table1Row = new ArrayList<>();
        table1Content = new ArrayList<>();
        table2Col = new ArrayList<>();
        table2Row = new ArrayList<>();
        table2Content = new ArrayList<>();

    }
    // <Join> ::=  "JOIN " [TableName] " AND " [TableName] " ON " [AttributeName] " AND " [AttributeName]
    protected String parserJoin() throws IOException {
        if (token.tokens.size()!=9){
            curCommandStatus = "Invalid command cause the incorrect length";
            return curCommandStatus;
        }
        String tableName1;
        String tableName2;
        String curToken = token.tokens.get(index).toLowerCase(); // Table1 now
        //Already check the format when create the table
        String filePath1 = server.getStorageFolderPath() + File.separator + GlobalMethod.getCurDatabaseName()
                + File.separator +curToken + ".tab";
        Path path1 = Paths.get(filePath1);
        if (!Files.exists(path1)){
            curCommandStatus = "[ERROR]Can't find the first table: " + curToken;
            return curCommandStatus;
        }
        tableName1 = curToken;
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
        Path path2 = Paths.get(filePath2);
        if (!Files.exists(path2)){
            curCommandStatus = "[ERROR]Can't find the second table: " + curToken;
            return curCommandStatus;
        }
        tableName2 = curToken;
        index++; // now the current token should be "ON";
        curToken = token.tokens.get(index);
        if(!curToken.equalsIgnoreCase("ON")){
            curCommandStatus = "[ERROR]Missing or typo [ON] when typing command";
            return curCommandStatus;
        }
        index++; // now the current token should attribute name from table1;
        curToken = token.tokens.get(index);
        // if condition
        if(!checkLeftTable(filePath1,curToken,tableName1)){
            curCommandStatus = "[ERROR]Attribute is not exist or invalid format";
            return curCommandStatus;
        }
        index++; // now the current token should be "AND"
        curToken = token.tokens.get(index);
        if(!curToken.equalsIgnoreCase("AND")){
            curCommandStatus = "[ERROR]Missing or typo [AND] when typing command";
            return curCommandStatus;
        }
        index++; // now the current token should attribute name from table2;
        curToken = token.tokens.get(index);
        if(!checkRightTable(filePath2,curToken,tableName2)){
            curCommandStatus = "[ERROR]Attribute is not exist or invalid format";
            return curCommandStatus;
        }
        // Satisfy all the conditions so do the command in here
        curCommandStatus = "[OK]\n" + joinTable();
        return curCommandStatus;
    }

    // Set the row and column flags & storage table messages
    // return the boolean value to indicate the attribute name is exist or not
    private boolean checkLeftTable(String filePath, String attributeName, String tableName) throws IOException {
        boolean exist = false;
        BufferedReader tableReader = new BufferedReader(new FileReader(filePath));
        String line = tableReader.readLine();
        table1Content.addAll(Arrays.asList(line.split("\t")));
            for (String attribute : table1Content) {
                if (attribute.equalsIgnoreCase(attributeName)) {
                    if(!attributeName.equalsIgnoreCase("id")) { // need to show the id line on the left
                        table1Col.add(false); // not show on the table, so set the flag to false
                    }else {
                        table1Col.add(true);
                    }
                    exist = true;
                } else {
                    table1Col.add(true);
                }
            }
            for (int i = 0; i < table1Content.size(); i++) {
                if(!table1Content.get(i).equals("id") && table1Col.get(i)){
                    table1Content.set(i, tableName + "." + table1Content.get(i));
                }
            }
        while ((line = tableReader.readLine()) != null) {
            table1Content.addAll(Arrays.asList(line.split("\t")));
        }
        for(int i = 0; i < table1Content.size()/table1Col.size(); i++) {
            table1Row.add(true);
        }
        tableReader.close();
        return exist;
    }

    private boolean checkRightTable(String filePath, String attributeName, String tableName) throws IOException {
        boolean exist = false;
        BufferedReader tableReader = new BufferedReader(new FileReader(filePath));
        String line = tableReader.readLine();
        table2Content.addAll(Arrays.asList(line.split("\t")));
            for (String attribute : table2Content) {
                if (attribute.equalsIgnoreCase(attributeName)) {
                    table2Col.add(false); // not show on the table, so set the flag to false
                    exist = true;
                } else {
                    table2Col.add(true);
                }
            }
            for (int i = 0; i < table2Content.size(); i++) {
                if(table2Col.get(i)){
                    table2Content.set(i, tableName + "." + table2Content.get(i));
                }
            }
        while ((line = tableReader.readLine()) != null) {
            table2Content.addAll(Arrays.asList(line.split("\t")));
        }
        for(int i = 0; i < table2Content.size()/table2Col.size(); i++) {
            table2Row.add(true);
        }
        tableReader.close();
        return exist;
    }
    // In here do the operation to join two tables
    // just using buffer reader to change the content
    private String joinTable(){
        curCommandStatus = "";
        // Attribute message
        for (int t1 = 0; t1 < table1Col.size(); t1++){
            if(table1Col.get(t1)) {
                curCommandStatus += table1Content.get(t1) + "\t";
            }
        }
        for (int t2 = 0; t2 < table2Col.size(); t2++) {
            if(table2Col.get(t2)) {
                curCommandStatus += table2Content.get(t2) + "\t";
            }
        }
        curCommandStatus = curCommandStatus.trim() + "\n";
        // table information message
        for (int i = 1; i < table1Row.size(); i++) {
            for (int j = 0; j < table1Col.size(); j++) {
                if(table1Col.get(j)){
                    curCommandStatus += table1Content.get(i*table1Col.size()+j) + "\t";
                }else { // table1 false -> not print out it to the terminal
                    for (int k = 1; k < table2Row.size(); k++) {
                        for (int l = 0; l <table2Col.size() ; l++) {
                            if (Objects.equals(table1Content.get(i * table1Col.size() + j), table2Content.get(k * table2Col.size() + l))){
                                for (int m = (k*table2Col.size()+l+1); m < ((k*table2Col.size()+l)+table2Col.size()); m++) {
                                        // l column false --> so l+1 and left right column all were true.
                                        if (m == ((k * table2Col.size() + l) + table2Col.size() - 1)) {
                                            curCommandStatus += table2Content.get(m);
                                        } else {
                                            curCommandStatus += table2Content.get(m) + "\t";
                                        }

                                }
                            }
                        }
                    }
                }
            }
            curCommandStatus += "\n";
        }
        return curCommandStatus;
    }
}

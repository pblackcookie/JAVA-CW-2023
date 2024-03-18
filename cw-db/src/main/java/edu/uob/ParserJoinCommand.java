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
import java.util.Collections;

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
        System.out.println("table one current token:" + curToken);
        // if condition
        if(!checkLeftTable(filePath1,curToken)){
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
        System.out.println("table 2 current token:" + curToken);
        if(!checkRightTable(filePath2,curToken)){
            curCommandStatus = "Attribute is not exist or invalid format";
            return curCommandStatus;
        }
        // Satisfy all the conditions so do the command in here
        curCommandStatus = "[OK]\n" + joinTable();
        return curCommandStatus;
    }

    // Set the row and column flags & storage table messages
    // return the boolean value to indicate the attribute name is exist or not
    private boolean checkLeftTable(String filePath, String attributeName) throws IOException {
        boolean exist = false;
        BufferedReader tableReader = new BufferedReader(new FileReader(filePath));
        String line = tableReader.readLine();
        table1Content.addAll(Arrays.asList(line.split("\t")));
        if(line != null){
            for (String attribute : table1Content) {
                if (attribute.equalsIgnoreCase(attributeName)) {
                    table1Col.add(false); // not show on the table, so set the flag to false
                    exist = true;
                } else {
                    table1Col.add(true);
                }
            }
        }
        while ((line = tableReader.readLine()) != null) {
            table1Content.addAll(Arrays.asList(line.split("\t")));
        }
        for(int i = 0; i < table1Content.size()/table1Col.size(); i++) {
            table1Row.add(true);
        }
        System.out.println("table1:");
        System.out.println(table1Col);
        System.out.println(table1Row);
        System.out.println(table1Content);
        tableReader.close();
        return exist;
    }

    private boolean checkRightTable(String filePath, String attributeName) throws IOException {
        boolean exist = false;
        BufferedReader tableReader = new BufferedReader(new FileReader(filePath));
        String line = tableReader.readLine();
        table2Content.addAll(Arrays.asList(line.split("\t")));
        if (line!=null) {
            for (String attribute : table2Content) {
                if (attribute.equalsIgnoreCase(attributeName)) {
                    table2Col.add(false); // not show on the table, so set the flag to false
                    exist = true;
                } else {
                    table2Col.add(true);
                }
            }
        }
        while ((line = tableReader.readLine()) != null) {
            table2Content.addAll(Arrays.asList(line.split("\t")));
        }
        for(int i = 0; i < table2Content.size()/table2Col.size(); i++) {
            table2Row.add(true);
        }
        System.out.println("table2:");
        System.out.println(table2Col);
        System.out.println(table2Row);
        System.out.println(table2Content);
        tableReader.close();
        return exist;
    }
    // In here do the operation to join two tables
    // just using buffer reader to change the content
    private String joinTable(){
        curCommandStatus = "";
        for(int k = 0; k < table1Row.size(); k++){
            for(int i = 0; i < table1Col.size(); i++){
                if(table1Col.get(i)){
                    curCommandStatus += table1Content.get(i) + "\t";
                }else{
                    if(!table2Col.get(i)){
                        for(int j = 0; j < table2Content.size(); j++){
                            if(table2Content.get(j).equalsIgnoreCase(table1Content.get(i))){
                                int l;
                                for(l=0; l <table2Col.size()-2; l++){
                                    curCommandStatus += table2Content.get(j) + "\t";
                                }
                                if(l == table2Col.size()-1){
                                    curCommandStatus += table2Content.get(j);
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

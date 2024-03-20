package edu.uob;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static edu.uob.GlobalMethod.*;

public class DBParser {
    protected int index = 0; // use to indicate the current token
    protected String curCommandStatus = "[OK]Haven't finished that function or parser.";
    protected String curCommand;
    protected ArrayList<Integer> tableCol = new ArrayList<>();
    protected ArrayList<Integer> tableRow = new ArrayList<>();
    protected ArrayList<String> tableContent = new ArrayList<>();
    ArrayList<String> attributes = new ArrayList<>();
    ArrayList<String> data = new ArrayList<>();
    CommandToken token = new CommandToken(); // storage all tokens
    DatabaseProcess database = new DatabaseProcess();
    FileProcess table = new FileProcess();

    DBServer server = new DBServer();



    public DBParser(String command){
        token.setup(command); // get all tokens from command
        curCommand = command;
    }


    // Check the database name or table name is valid or not
    // Contain changing for symbol--using contain & for keyword using equal
    protected String nameCheck(String curName){
        curName = curName.toUpperCase();
        for (char c : curName.toCharArray()) {
            String s = String.valueOf(c);
            if (symbols.contains(s)) {
                return "[ERROR]Name contains illegal symbol(s): " + s;
            }
        }
        if(keyWords.contains(curName)){
            return "[ERROR]Name has the keyword: " + curName;
        }
        return "[OK]Valid Name";
    }


    // Check if the attributes is valid -- using Recursion method
    // <AttributeList>   ::=  [AttributeName] | [AttributeName] "," <AttributeList>
    protected String attributeCheck(ArrayList<String> attributes){
        //System.out.println("In attributes check:" + attributes);
        if(attributes.isEmpty()){
            curCommandStatus = "[ERROR]Attributes can't be the empty.";
            return curCommandStatus;
        }
        for (String element : attributes){
            if(element.equals("(") || element.equals(")")){
                curCommandStatus = "[ERROR]: Invalid bracket.";
                return curCommandStatus;
            }
        }
        String checkNow = attributes.get(0);
        //System.out.println("Check out now is :" + checkNow);
        if(!checkNow.equals(",")){ //First one need to check the valid
            curCommandStatus= nameCheck(checkNow);
            if (curCommandStatus.contains("[ERROR]")){
                return curCommandStatus;
            }
            // After checking the name still valid.
            attributes.remove(0);
            if(attributes.isEmpty()){
                curCommandStatus = "[OK]Finish checking.";
                return curCommandStatus;
            }else {
                attributeCheck(attributes); // Continuing checking....
            }
            return curCommandStatus;
        } else{
            if(attributes.size()<2){
                curCommandStatus = "[ERROR]Invalid length";
                return curCommandStatus;
            }
            checkNow = attributes.get(1); // should be attribute name now
            //System.out.println("In the start with , , now: " + checkNow);
            curCommandStatus= nameCheck(checkNow);
            if (curCommandStatus.contains("[ERROR]")){
                return curCommandStatus;
            }
            attributes.remove(0); // remove ,
            attributes.remove(0); // remove name
            //System.out.println("In the , + name part:" + attributes);
            if(attributes.isEmpty()){
                curCommandStatus = "[OK]Finish checking.";
                return curCommandStatus;
            }else {
                attributeCheck(attributes); // Continuing checking....
            }
            return curCommandStatus;
        }
    }
    protected String valueCheck(String value){
        // check it value type in here
        System.out.println("Now check is:" + value);
        if (value.matches("[+-]?\\d+")){
            curCommandStatus = "[OK]Integer format valid";
            return curCommandStatus;
        }else if(value.matches("[+-]?\\d+(\\.\\d+)?")){
            curCommandStatus = "[OK]Float format valid";
            return curCommandStatus;

        }else if(value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")){
            curCommandStatus = "[OK]Boolean format valid";
            return curCommandStatus;
        }else if(value.startsWith("'") && value.endsWith("'")){
            curCommandStatus = "[OK]String format valid";
            return curCommandStatus;
        } else {
            curCommandStatus= "[ERROR]The insert type of:" + value + " is invalid.";
            return curCommandStatus;
        }
    }

    // use one int array --> for print out inordered result
    // changing the variable in the function
    // return the boolean to indicate if this arraylist valid.
    protected boolean colIndexStorage(String filePath, ArrayList<String> attributeNames) throws IOException {
        boolean exist = false;
        BufferedReader tableReader = new BufferedReader(new FileReader(filePath));
        String line = tableReader.readLine();
        int duplicate = 0;
        tableContent.addAll(Arrays.asList(line.split("\t")));
        // for loop for storage the information that need to be shown
        for (int i = 0; i < tableContent.size(); i++) {
            if(tableCol.size()<=tableContent.size()) { // prevent many times adding
                tableCol.add(-1);
            }
        }// for loop for record the duplicate elements
        for (int i = 0; i < attributeNames.size(); i++) {
            String attributeNow = attributeNames.get(i);
            for (int j = i+1; j < attributeNames.size(); j++) {
                if(attributeNow.equalsIgnoreCase(attributeNames.get(j))){
                    duplicate++;
                }
            }
        }
        for (int i = 0; i < attributeNames.size(); i++) {
            for (int j = 0; j < tableContent.size(); j++) {
                if (tableContent.get(j).equalsIgnoreCase(attributeNames.get(i))) {
                    tableCol.set(j,i);
                }
            }
        }
        while ((line = tableReader.readLine()) != null) {
            tableContent.addAll(Arrays.asList(line.split("\t")));
        }
        System.out.println("table Col now" + tableCol);
        for(int i = 0; i < tableContent.size()/tableCol.size(); i++) {
            if(i == 0){
                tableRow.add(0); // table head information
            }else {
                tableRow.add(-1);
            }
        }
        System.out.println("table Row now" + tableRow);
        tableReader.close();
        int condition = 0;
        for (Integer integer : tableCol) {
            if (integer == -1) {
                condition++;
            }
        }// solve the problem when duplicate same name.
        /*if(condition == (tableCol.size()-(attributeNames.size()-duplicate))){
            exist = true;
            return exist;
        }else {
            return exist;
        }*/ // check can not be the here
        exist = true;
        return exist;
    }


    // need to rewrite
    // different symbols will lead different result
    // col now -> then row : ordered
    protected boolean rowIndexStorage(String symbol,String demand){
        boolean valid = true;
        System.out.println("symbol now" + symbol);
        System.out.println("demand now" + demand);
        if(demand.startsWith("'") && demand.endsWith("'")){
            demand = demand.substring(1, demand.length() - 1); // remove "'"
        }
        if(symbol.equals("==")) {
            for (int i = 1; i < tableRow.size(); i++) {
                for (int j = 0; j < tableCol.size(); j++) {
                    if (!tableCol.get(j).equals(-1)) {
                        if (tableContent.get(i * tableCol.size() + j).equalsIgnoreCase(demand)) {
                            tableRow.set(i, 0);
                        }
                    }
                }
            }
            return valid;
        }else if(symbol.equals("!=")){
            for (int i = 1; i < tableRow.size(); i++) {
                for (int j = 0; j < tableCol.size(); j++) {
                    if (!tableCol.get(j).equals(-1)) {
                        if (!tableContent.get(i * tableCol.size() + j).equalsIgnoreCase(demand)) {
                            tableRow.set(i, 0);
                        }
                    }
                }
            }
            return valid;
        }else if(symbol.equals(">")){
            for (int i = 1; i < tableRow.size(); i++) {
                for (int j = 0; j < tableCol.size(); j++) {
                    if (!tableCol.get(j).equals(-1)) {
                        float numberNow = Float.parseFloat(tableContent.get(i * tableCol.size() + j));
                        float demandNow = Float.parseFloat(demand);
                        if(numberNow > demandNow){
                            tableRow.set(i, 0);
                        }
                    }
                }
            }
            return valid;
        }else if(symbol.equals("<")){
            for (int i = 1; i < tableRow.size(); i++) {
                for (int j = 0; j < tableCol.size(); j++) {
                    if (!tableCol.get(j).equals(-1)) {
                        float numberNow = Float.parseFloat(tableContent.get(i * tableCol.size() + j));
                        float demandNow = Float.parseFloat(demand);
                        if(numberNow < demandNow){
                            tableRow.set(i, 0);
                        }
                    }
                }
            }
            return valid;
        }else if(symbol.equals(">=")){
            for (int i = 1; i < tableRow.size(); i++) {
                for (int j = 0; j < tableCol.size(); j++) {
                    if (!tableCol.get(j).equals(-1)) {
                        float numberNow = Float.parseFloat(tableContent.get(i * tableCol.size() + j));
                        float demandNow = Float.parseFloat(demand);
                        if(numberNow >= demandNow){
                            tableRow.set(i, 0);
                        }
                    }
                }
            }
            return valid;
        }else if(symbol.equals("<=")){
            for (int i = 1; i < tableRow.size(); i++) {
                for (int j = 0; j < tableCol.size(); j++) {
                    if (!tableCol.get(j).equals(-1)) {
                        float numberNow = Float.parseFloat(tableContent.get(i * tableCol.size() + j));
                        float demandNow = Float.parseFloat(demand);
                        if(numberNow <= demandNow){
                            tableRow.set(i, 0);
                        }
                    }
                }
            }
            return valid;
        }else if(symbol.equalsIgnoreCase("LIKE")){
            for (int i = 1; i < tableRow.size(); i++) {
                for (int j = 0; j < tableCol.size(); j++) {
                    if (!tableCol.get(j).equals(-1)) {
                        String element = tableContent.get(i * tableCol.size() + j);
                        if (element.contains(demand)) {
                            tableRow.set(i, 0);
                        }
                    }
                }
            }
            return valid;
        }
        valid = false;
        return valid;
    }

    protected String showTheContent (){
        curCommandStatus = "";
            for (int i = 0; i < tableRow.size(); i++) {
                for (int j = 0; j < tableCol.size(); j++) {
                    if(tableCol.get(j)!= -1){
                        if(j == tableRow.size()-1) {
                            curCommandStatus += tableContent.get(i * tableCol.size() + j);
                        }else{
                            curCommandStatus += tableContent.get(i * tableCol.size() + j) + "\t";
                        }
                    }
                }
                curCommandStatus += "\n";
            }
        return curCommandStatus;
    }


protected String showTheContent (String operation, String demand){
    curCommandStatus = "";
        for (int i = 0; i < tableRow.size(); i++) {
            for (int j = 0; j < tableCol.size(); j++) {
                if(tableRow.get(i)!= -1){
                    if(j == tableCol.size()-1) {
                        curCommandStatus += tableContent.get(i * tableCol.size() + j) + "\n";
                    }else{
                        curCommandStatus += tableContent.get(i * tableCol.size() + j) + "\t";
                    }
                }
            }
        }
    curCommandStatus = curCommandStatus.trim();
    return curCommandStatus;
}

    protected boolean checkOperation(String operation){
        return keyWords.contains(operation);
    }
    // for condition check (only one condition)
    protected String conditionCheck(String filePath) throws IOException {
        // assume only one condition now
        String symbol;
        String curToken = token.tokens.get(index); // ( or attribute
        if(curToken.equalsIgnoreCase("(")){
            index++; //ignore
            curToken = token.tokens.get(index); // attribute
        }
        ArrayList<String> attributes = new ArrayList<>() ;
        attributes.add(curToken);
        curCommandStatus = attributeCheck(attributes);
        if(curCommandStatus.contains("[ERROR]")){
            curCommandStatus = "[ERROR]Attribute error.";
            return curCommandStatus;
        }
        attributes.add(curToken);
        boolean exist = colIndexStorage(filePath,attributes);
        if(!exist){
            curCommandStatus ="[ERROR]Attributes not exist.";
            return curCommandStatus;
        }
        // symbol check
        index++;
        curToken = token.tokens.get(index);
        symbol = curToken;
        if(symbol.equals("=")||symbol.equals("!")||symbol.equals("<")||symbol.equals(">")||symbol.equalsIgnoreCase("LIKE")){
            index++; // may be the values or another symbol.
            curToken = token.tokens.get(index);
            if(curToken.equals("=")){
                symbol = symbol + curToken;
                index++;
                curToken = token.tokens.get(index); // one values in here
            }
        }// make sure after this if-condition the current token is the
        curCommandStatus = valueCheck(curToken);
        if(curCommandStatus.contains("[ERROR]")){
            curCommandStatus = "[ERROR]Value format invalid";
            return curCommandStatus;
        }
        System.out.println(attributes+ " " + symbol + " " + curToken);
        boolean symbolValid = rowIndexStorage(symbol,curToken);
        if(!symbolValid){
            curCommandStatus = "[ERROR]Detected the invalid symbol:" + symbol;
            return curCommandStatus;
        }
        curCommandStatus = showTheContent(symbol,curToken);
        return curCommandStatus;
    }
}

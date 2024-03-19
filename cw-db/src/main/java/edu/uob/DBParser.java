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
    // use one int array --> for print out inordered result
    // changing the variable in the function
    // return the boolean to indicate if this arraylist valid..
    protected boolean colIndexStorage(String filePath, ArrayList<String> attributeNames) throws IOException {
        boolean exist = false;
        BufferedReader tableReader = new BufferedReader(new FileReader(filePath));
        String line = tableReader.readLine();
        tableContent.addAll(Arrays.asList(line.split("\t")));
        // for loop for storage the information that need to be shown
        for (int i = 0; i < attributeNames.size(); i++) {
            for (int j = 0; j < tableContent.size(); j++) {
                if (tableContent.get(j).equalsIgnoreCase(attributeNames.get(i))) {
                    tableCol.add(j);
                }
            }
        }
        while ((line = tableReader.readLine()) != null) {
            tableContent.addAll(Arrays.asList(line.split("\t")));
        }
        for(int i = 0; i < tableContent.size()/tableCol.size(); i++) {
            tableRow.add(i);
        }
        tableReader.close();
        // add condition to
        if(tableCol.size()<attributeNames.size()){
            // attribute name does not exist or like 1/2
            return exist;
        }else { // all needed attributes are exist.
            exist = true;
            return exist;
        }
    }

}

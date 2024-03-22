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

    protected ArrayList<String> rowIndex = new ArrayList<>();
    ArrayList<String> attributes = new ArrayList<>();
    ArrayList<String> data = new ArrayList<>();

    ArrayList<String> selectAttributes = new ArrayList<>();


    CommandToken token = new CommandToken(); // storage all tokens
    DatabaseProcess database = new DatabaseProcess();
    FileProcess table = new FileProcess();
    String filePath = null;
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
            curCommandStatus= nameCheck(checkNow);
            if (curCommandStatus.contains("[ERROR]")){
                return curCommandStatus;
            }
            attributes.remove(0); // remove ,
            attributes.remove(0); // remove name
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
    protected boolean colIndexStorage(String filePath, ArrayList<String> attributeNames, ArrayList<Integer> rowIndex) throws IOException {
        System.out.println("filePath is:" + filePath);
        selectAttributes.addAll(attributes);
        if(tableContent.isEmpty()) {
            BufferedReader tableReader = new BufferedReader(new FileReader(filePath));
            String line = tableReader.readLine();
            tableContent.addAll(Arrays.asList(line.split("\t")));
            System.out.println("table content is:" + tableContent);
            // for loop for storage the information that need to be shown
            if (tableCol.isEmpty()) {
                for (int i = 0; i < tableContent.size(); i++) {
                    if (tableCol.size() <= tableContent.size()) { // prevent many times adding
                        tableCol.add(-1);
                    }
                }
            }
            for (int i = 0; i < attributeNames.size(); i++) {
                for (int j = 0; j < tableCol.size(); j++) {
                    if (tableContent.get(j).equalsIgnoreCase(attributeNames.get(i))) {
                        tableCol.set(j, i);
                    }
                }
            }
            while ((line = tableReader.readLine()) != null) {
                tableContent.addAll(Arrays.asList(line.split("\t")));
            }
            tableReader.close();// check if the attribute name exist
            if(tableRow.isEmpty()) {
                for (int i = 0; i < tableContent.size() / tableCol.size(); i++) {
                    if (i == 0) {
                        rowIndex.add(0); // table head information
                    } else {
                        rowIndex.add(-1);
                    }
                }
                tableRow = rowIndex; //pass the arguments
            }
        }else{
            for (int i = 0; i < tableContent.size() / tableCol.size(); i++) {
                if (i == 0) {
                    rowIndex.add(0); // table head information
                } else {
                    rowIndex.add(-1);
                }
            }


        }
        int count = 0;
        for (Integer integer : tableCol) {
            if (integer == -1) {
                count++;
            }
        }// not exists... -> after simplify
        return count != tableCol.size();
    }

    // different symbols will lead different result
    // col now -> then row : ordered
    protected boolean rowIndexStorage(String symbol,String demand, ArrayList<Integer> rowIndex){
        if(demand.startsWith("'") && demand.endsWith("'")){
            demand = demand.substring(1, demand.length() - 1); // remove "'"
        }
        switch(symbol){
            case "==":
                rowIndexStorageEquals(demand,rowIndex);break;
            case "!=":
                rowIndexStorageNotEquals(demand,rowIndex); break;
            case ">" :
                if(!rowIndexStorageMoreThan(demand,rowIndex)){return false;}
                break;
            case ">=":
                if(!rowIndexStorageMoreThanEquals(demand,rowIndex)){return false;}
                break;
            case "<":
                if(!rowIndexStorageLessThan(demand,rowIndex)){return false;}
                break;
            case "<=":
                if(!rowIndexStorageLessThanEquals(demand,rowIndex)){return false;}
                break;
            case "LIKE":
                rowIndexStorageLike(demand,rowIndex);
                break;
            default: return false;
        }
        return true;
    }
    protected void rowIndexStorageEquals(String demand,ArrayList<Integer> rowIndex){ //==
        System.out.println("In the == operation");
        System.out.println("select attribute:" + selectAttributes);
        for (int i = 1; i < rowIndex.size(); i++) {
            for (int j = 0; j < tableCol.size(); j++) {
                if (!tableCol.get(j).equals(-1) && !selectAttributes.isEmpty()) {
                    for (String selectAttribute : selectAttributes) {
                        if (!tableContent.get(j).equalsIgnoreCase(selectAttribute)) {
                            if (tableContent.get(i * tableCol.size() + j).equals(demand)) {
                                rowIndex.set(i, 0);
                            }
                        }
                    }
                }else if(!tableCol.get(j).equals(-1)){
                    if (tableContent.get(i * tableCol.size() + j).equalsIgnoreCase(demand)) {
                        rowIndex.set(i, 0);
                    }
                }
            }
        }
         tableRow = rowIndex;
    }
    protected void rowIndexStorageNotEquals(String demand,ArrayList<Integer> rowIndex) { //!=
        for (int i = 1; i < tableRow.size(); i++) {
            for (int j = 0; j < tableCol.size(); j++) {
                if (!tableCol.get(j).equals(-1) && !selectAttributes.isEmpty()) {
                    for (String selectAttribute : selectAttributes) {
                        if (!tableContent.get(j).equalsIgnoreCase(selectAttribute)) {
                            if (!tableContent.get(i * tableCol.size() + j).equalsIgnoreCase(demand)) {
                                tableRow.set(i, 0);
                            }
                        }
                    }
                }else if(!tableCol.get(j).equals(-1)){
                    if (!tableContent.get(i * tableCol.size() + j).equalsIgnoreCase(demand)) {
                        tableRow.set(i, 0);
                    }
                }
            }
        }
    }
    protected boolean rowIndexStorageMoreThan(String demand,ArrayList<Integer> rowIndex) { // >
        try{
            for (int i = 1; i < tableRow.size(); i++) {
                for (int j = 0; j < tableCol.size(); j++) {
                    if (!tableCol.get(j).equals(-1) && !selectAttributes.isEmpty()) {
                        for (String selectAttribute : selectAttributes) {
                            if (!tableContent.get(j).equalsIgnoreCase(selectAttribute)) {
                                float numberNow = Float.parseFloat(tableContent.get(i * tableCol.size() + j));
                                float demandNow = Float.parseFloat(demand);
                                if (numberNow > demandNow) {
                                    tableRow.set(i, 0);
                                }
                            }
                        }
                    }else if(!tableCol.get(j).equals(-1)){
                        float numberNow = Float.parseFloat(tableContent.get(i * tableCol.size() + j));
                        float demandNow = Float.parseFloat(demand);
                        if (numberNow > demandNow) {
                            tableRow.set(i, 0);
                        }
                    }
                }
            }
        }catch (Exception e){
            return false;
        }
        return true;
    }
    protected boolean rowIndexStorageLessThan(String demand,ArrayList<Integer> rowIndex) { //<
        try {
            for (int i = 1; i < tableRow.size(); i++) {
                for (int j = 0; j < tableCol.size(); j++) {
                    if (!tableCol.get(j).equals(-1) && !selectAttributes.isEmpty()) {
                        for (String selectAttribute : selectAttributes) {
                            if (!tableContent.get(j).equalsIgnoreCase(selectAttribute)) {
                                float numberNow = Float.parseFloat(tableContent.get(i * tableCol.size() + j));
                                float demandNow = Float.parseFloat(demand);
                                if (numberNow < demandNow) {
                                    tableRow.set(i, 0);
                                }
                            }
                        }
                    } else if (!tableCol.get(j).equals(-1)) {
                        float numberNow = Float.parseFloat(tableContent.get(i * tableCol.size() + j));
                        float demandNow = Float.parseFloat(demand);
                        if (numberNow < demandNow) {
                            tableRow.set(i, 0);
                        }
                    }
                }
            }
        }catch (Exception e){
            return false;
        }
        return true;
    }
    protected boolean rowIndexStorageMoreThanEquals(String demand,ArrayList<Integer> rowIndex) { // >=
        try{
            for (int i = 1; i < tableRow.size(); i++) {
                for (int j = 0; j < tableCol.size(); j++) {
                    if (!tableCol.get(j).equals(-1) && !selectAttributes.isEmpty()) {
                        for (String selectAttribute : selectAttributes) {
                            if (!tableContent.get(j).equalsIgnoreCase(selectAttribute)) {
                                float numberNow = Float.parseFloat(tableContent.get(i * tableCol.size() + j));
                                float demandNow = Float.parseFloat(demand);
                                if (numberNow >= demandNow) {
                                    tableRow.set(i, 0);
                                }
                            }
                        }
                    } else if (!tableCol.get(j).equals(-1)) {
                        float numberNow = Float.parseFloat(tableContent.get(i * tableCol.size() + j));
                        float demandNow = Float.parseFloat(demand);
                        if (numberNow >= demandNow) {
                            tableRow.set(i, 0);
                        }
                    }
                }
            }
        }catch(Exception e){
                return false;
        }
        return true;
    }
    protected boolean rowIndexStorageLessThanEquals(String demand,ArrayList<Integer> rowIndex){  //<=
        try {
            for (int i = 1; i < tableRow.size(); i++) {
                for (int j = 0; j < tableCol.size(); j++) {
                    if (!tableCol.get(j).equals(-1) && !selectAttributes.isEmpty()) {
                        for (String selectAttribute : selectAttributes) {
                            if (!tableContent.get(j).equalsIgnoreCase(selectAttribute)) {
                                float numberNow = Float.parseFloat(tableContent.get(i * tableCol.size() + j));
                                float demandNow = Float.parseFloat(demand);
                                if (numberNow <= demandNow) {
                                    tableRow.set(i, 0);
                                }
                            }
                        }
                    } else if (!tableCol.get(j).equals(-1)) {
                        float numberNow = Float.parseFloat(tableContent.get(i * tableCol.size() + j));
                        float demandNow = Float.parseFloat(demand);
                        if (numberNow <= demandNow) {
                            tableRow.set(i, 0);
                        }
                    }
                }
            }
        }catch (Exception e){
            return false;
        }
        return true;
    }
    protected void rowIndexStorageLike(String demand,ArrayList<Integer> rowIndex){ // LIKE
        for (int i = 1; i < tableRow.size(); i++) {
            for (int j = 0; j < tableCol.size(); j++) {
                if (!tableCol.get(j).equals(-1) && !selectAttributes.isEmpty()) {
                    for (String selectAttribute : selectAttributes) {
                        if (!tableContent.get(j).equalsIgnoreCase(selectAttribute)) {
                            String element = tableContent.get(i * tableCol.size() + j);
                            if (element.contains(demand)) {
                                tableRow.set(i, 0);
                            }
                        }
                    }
                }else if(!tableCol.get(j).equals(-1)){
                    String element = tableContent.get(i * tableCol.size() + j);
                    if (element.contains(demand)) {
                        tableRow.set(i, 0);
                    }
                }
            }
        }
    }
    protected String showTheContent (ArrayList<Integer> rowIndex){ // most original show the content
        StringBuilder newString = new StringBuilder();
            for (int i = 0; i < rowIndex.size(); i++) {
                for (int j = 0; j < tableCol.size(); j++) {
                    if(!tableCol.get(j).equals(-1)){
                        if(j == rowIndex.size()-1) {
                            newString.append(tableContent.get(i * tableCol.size() + j));
                            newString.append("\t");
                        }else{
                            newString.append(tableContent.get(i * tableCol.size() + j));
                            newString.append("\t");
                        }
                    }
                }
                newString.append("\n");
            }
        curCommandStatus = newString.toString().trim();
        return curCommandStatus;
    }
    // <Condition>       ::=  "(" <Condition> <BoolOperator> <Condition> ")" | <Condition> <BoolOperator> <Condition> |
    // "(" [AttributeName] <Comparator> [Value] ")" | [AttributeName] <Comparator> [Value]
    // Try to recursion the Condition -> assume first token is next the "WHERE"
    // Need to return the Integer arraylist
    protected ArrayList<Integer> multipleConditionCheck() throws Exception {
        ArrayList<Integer> newRow1 = new ArrayList<Integer>();
        String curToken;
        if(index <= token.tokens.size()-1){curToken = token.tokens.get(index); // Assume this is the first token after where
        }else{ curToken = ";";}
//        String curToken = token.tokens.get(index); // Assume this is the first token after where
        if(curToken.equals("(")){
            index++;
            multipleConditionCheck();
        }
        System.out.println("current token is:" + curToken);
        if(!checkInCondition(curToken,filePath,newRow1) && !curToken.equals("(")){throw new Exception("[ERROR]");}  // Global variable filePath
        newRow1 = tableRow; // just for test
        System.out.println("After check& set column, newRow 1 is:" + newRow1);
        // get one new rowIndex value in here...
        //curToken = token.tokens.get(index); // may be AND or OR | ) |;
        if(index <= token.tokens.size()-1){curToken = token.tokens.get(index); // Assume this is the first token after where
        }else{ curToken = ";";}
        if(curToken.equalsIgnoreCase("AND") || curToken.equalsIgnoreCase("OR")) {
            index++;
            String currentOperation = curToken.toUpperCase(); // AND or OR
            ArrayList<Integer> newRow2 = new ArrayList<Integer>();
            newRow2 = multipleConditionCheck(); // call itself
            // The compare should occur in here...? -> update row index
            newRow1 = operationCondition(newRow1,newRow2,currentOperation);
            System.out.println("After  and & or operation, newRow 1 is:" + newRow1);
        }
        index++;
        //curToken = token.tokens.get(index);
        if(index <= token.tokens.size()-1){curToken = token.tokens.get(index); // Assume this is the first token after where
        }else{curToken = ";";}
        System.out.println("The curtoken is:" + curToken); // assume be ) ; ..
        if(curToken.equals(")")){
            index++; // check the next index
            curToken = token.tokens.get(index); // may be AND or OR | ) |;
            if(curToken.equalsIgnoreCase("AND") || curToken.equalsIgnoreCase("OR")) {
                index++;
                String currentOperation = curToken.toUpperCase(); // AND or OR
                ArrayList<Integer> newRow2 = new ArrayList<Integer>();
                newRow2 = multipleConditionCheck(); // call itself
                // The compare should occur in here...? -> update row index
                newRow1 = operationCondition(newRow1,newRow2,currentOperation);
                return newRow1;
            }
        }
        return newRow1;
    }

    // [AttributeName] <Comparator> [Value]
    protected boolean checkInCondition(String attributeName, String filePath, ArrayList<Integer> rowIndex) throws Exception {
        ArrayList<String> conditionAttribute = new ArrayList<>();
        conditionAttribute.add(attributeName);
        System.out.println("attribute now is:" + conditionAttribute);
        if(attributeCheck(conditionAttribute).contains("[ERROR]")){ return false;} // check attribute format valid
        conditionAttribute.add(attributeName);
        System.out.println("attribute after check is:" + conditionAttribute);
        if(!colIndexStorage(filePath,conditionAttribute,rowIndex)){ return false;} // check attribute exist in the table
        System.out.println("attribute after exist check is:" + conditionAttribute);
        index++; // expect be the <Comparator> in here
        String comparator = token.tokens.get(index); // check now : <Comparator> [Value]
        return comparatorValueCheck(comparator,rowIndex);
    }

    protected boolean comparatorValueCheck(String symbol,ArrayList<Integer> rowIndex){ //
        String curToken;
        if(symbol.equals("=")||symbol.equals("!")||symbol.equals("<")||symbol.equals(">")||symbol.equalsIgnoreCase("LIKE")){
            index++; // may be the values or another symbol.  ->
            curToken = token.tokens.get(index);
            if(curToken.equals("=")){
                symbol = symbol + curToken;
                index++;
                curToken = token.tokens.get(index);
            }
            valueCheck(curToken); // In here value check
            rowIndexStorage(symbol,curToken,rowIndex); // also check demand(?) and set the row Index
        }else{
            return false;
        }
        return true;
    }

    protected ArrayList<Integer> operationCondition(ArrayList<Integer> row1,ArrayList<Integer> row2,String operation){
        switch (operation){
            case "AND":
                return operationAnd(row1,row2);
            case "OR":
                return operationOr(row1,row2);
            default:
                throw new IllegalStateException("Unexpected value: " + operation);
        }
    }

    protected ArrayList<Integer> operationAnd(ArrayList<Integer> row1,ArrayList<Integer> row2){
        ArrayList<Integer> newOne = new ArrayList<Integer>();
        for (int i = 0; i < row1.size(); i++) {
            if(row1.get(i).equals(row2.get(i)) && !row1.get(i).equals(-1)){
                newOne.add(0);
            }else{
                newOne.add(-1);
            }
        }
        System.out.println("In AND: " + newOne);
        return newOne;
    }

    protected ArrayList<Integer> operationOr(ArrayList<Integer> row1,ArrayList<Integer> row2){
        ArrayList<Integer> newOne = new ArrayList<Integer>();
        for (int i = 0; i < row1.size(); i++) {
            if(!row1.get(i).equals(-1) || !row2.get(i).equals(-1)){
                newOne.add(0);
            }else{
                newOne.add(-1);
            }
        }
        System.out.println("In Or: " + newOne);
        return newOne;
    }

    public void setSelectAttribute(ArrayList<String> value){
        selectAttributes.addAll(value);
    }
}

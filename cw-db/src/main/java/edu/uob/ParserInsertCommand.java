package edu.uob;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

import static edu.uob.GlobalMethod.getCurDatabaseName;

public class ParserInsertCommand extends DBParser{
    public ParserInsertCommand(String command, int index) {
        super(command);
        this.index = index;
    }

    // // When command type = 'INSERT'
    // TODO implement the logic and check in the ();
    protected String parserInsert() throws IOException {
        // command length check
        ArrayList<String> valueList = new ArrayList<>();
        ArrayList<String> attributeName = new ArrayList<>();
        if(token.tokens.size() < 8){
            curCommandStatus = "[ERROR]Invalid insert command - no completed.";
            return curCommandStatus;
        }
        int idNumber;
        String fileName = token.tokens.get(index+1).toLowerCase();
        String filePath = database.getCurDatabasePath(getCurDatabaseName()) + File.separator + fileName + ".tab";
        String curToken = token.tokens.get(index);
        if(!curToken.equalsIgnoreCase("INTO")){
            curCommandStatus = "[ERROR] Missing or wrong the 'INTO'";
            return curCommandStatus;
        }else{
            index++; // should be the table name now
            curToken = token.tokens.get(index).toLowerCase();
            curCommandStatus = nameCheck(curToken);
            if(curCommandStatus.contains("[ERROR]")){
                return curCommandStatus;
            }
            ArrayList<String> curFiles;
            curFiles = table.displayFiles(getCurDatabaseName());
            if(!curFiles.contains(curToken + ".tab")){
                curCommandStatus = "[ERROR]: Select file doesn't exists.";
                return curCommandStatus;
            }
            //Now see the file is empty or not
            File file = new File(filePath);
            if(file.length()==0){
                curCommandStatus = "[ERROR]: Can not insert the data to the empty file.";
                return curCommandStatus;
            }
            // Table exists and not the empty, read it to store the attribute number (contains id)
            BufferedReader tableReader = new BufferedReader(new FileReader(filePath));
            String attributeLine = tableReader.readLine();
            attributeName.addAll(Arrays.asList(attributeLine.split("\t"))); // will have one length
            tableReader.close();
            // Table exists ,so Read the id file to see which id it should be now
            String IdRecordPath = database.getCurDatabasePath(getCurDatabaseName()) + File.separator + curToken + ".id";
            index++; // should be the "VALUES" now
            curToken = token.tokens.get(index);
            if(!curToken.equalsIgnoreCase("VALUES")){
                curCommandStatus = "[ERROR] Missing or typo 'VALUES'.";
                return curCommandStatus;
            }
            index++; // should be the '(' now
            curToken = token.tokens.get(index);
            if(!curToken.equals("(")){
                curCommandStatus = "[ERROR] Missing or typo '('.";
                return curCommandStatus;
            }
            if((token.tokens.get(index+1)).equals(")")){
                curCommandStatus = "[ERROR] Missing Values.";
                return curCommandStatus;
            }
            if(!token.tokens.get(token.tokens.size() - 2).equals(")")) {
                // In order to prevent the situation like 'create table test(ss, mark, kkk)deaf;' occur.
                curCommandStatus = "[ERROR]Invalid format: Error occurs between ')' and ';'. ";
                return curCommandStatus;
            }
            // from here start should add one value & format check
            for (int i = index+1; i < token.tokens.size()-2; i++) { // should be the data now
                valueList.add(token.tokens.get(i));
            }// value list check in here
            curCommandStatus = valueListCheck(valueList);
            if(curCommandStatus.contains("[ERROR]")){
                return curCommandStatus;
            }// only if the check is ok then open the id and insert the data...
            valueList.clear();
            BufferedReader reader = new BufferedReader(new FileReader(IdRecordPath));
            String line = reader.readLine();
            idNumber = Integer.parseInt(line) + 1;// Update the value
            valueList.add(String.valueOf(idNumber)); // Update the id file about this table(file).
            // For loop to store the data -> need to check the number of ,
            for (int i = index+1; i < token.tokens.size()-2; i++) { // should be the data now
                if (!token.tokens.get(i).equals(",") && !(token.tokens.get(i).startsWith("'")&&token.tokens.get(i).endsWith("'"))) {
                    valueList.add(token.tokens.get(i));
                }else if(token.tokens.get(i).startsWith("'")&&token.tokens.get(i).endsWith("'")) { // remove the ' ' surrounding
                    valueList.add(token.tokens.get(i).substring(1, token.tokens.get(i).length() - 1));
                }
            }
            // Compare the insert value number , it should be equals to the attribute name
            if(attributeName.size() == valueList.size()){
                FileWriter writer = new FileWriter(IdRecordPath);
                BufferedWriter buffer = new BufferedWriter(writer); // write id value back to the .id file
                buffer.write(String.valueOf(idNumber));
                buffer.close();
                writer.close();
                reader.close();
                curCommandStatus = table.addFileContent(valueList, filePath);
                return curCommandStatus;
            }else{
                reader.close(); // need to close the id file reader.
                curCommandStatus = "[ERROR]The attributes' number is not equals to the insert data number";
                return curCommandStatus;
            }
        }
    }
    // very similar to the attribute list check but the individual value
    // check is different.
    private String valueListCheck(ArrayList<String> valueList) {
        if (valueList.isEmpty()) {
            curCommandStatus = "[ERROR]Data can't be the empty.";
            return curCommandStatus;
        }
        for (String s : valueList) {
            if (s.equals("(") || s.equals(")")) {
                curCommandStatus = "[ERROR]: Invalid bracket.";
                return curCommandStatus;
            }
        }
        String dataNow = valueList.get(0);
        if (!dataNow.equals(",")) { //First one need to check the valid
            curCommandStatus = valueCheck(dataNow);
            if (curCommandStatus.contains("[ERROR]")) {
                return curCommandStatus;
            }
            // After checking the name still valid.
            valueList.remove(0);
            if (valueList.isEmpty()) {
                curCommandStatus = "[OK]Finish value list checking.";
                return curCommandStatus;
            } else {
                valueListCheck(valueList); // Continuing checking....
            }
            return curCommandStatus;
        } else {
            if (valueList.size() < 2) {
                curCommandStatus = "[ERROR]Invalid value list length";
                return curCommandStatus;
            }
            dataNow = valueList.get(1); // should be insert value now
            curCommandStatus = valueCheck(dataNow);
            if (curCommandStatus.contains("[ERROR]")) {
                return curCommandStatus;
            }
            valueList.remove(0); // remove ,
            valueList.remove(0); // remove values
            if (valueList.isEmpty()) {
                curCommandStatus = "[OK]Finish value list checking.";
                return curCommandStatus;
            } else {
                valueListCheck(valueList); // Continuing checking....
            }
            return curCommandStatus;
        }
    }

    private String valueCheck(String value){
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
}


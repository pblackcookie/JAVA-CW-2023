package edu.uob;

import java.io.*;
import java.util.ArrayList;

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
            /*for (int i = index+1; i < token.tokens.size()-2; i++) { // should be the data now
                valueList.add(token.tokens.get(i));
            }// value list check in here
            curCommandStatus = valueListCheck(valueList);*/
            if(curCommandStatus.contains("[ERROR]")){
                return curCommandStatus;
            }// only if the check is ok then open the id and insert the data...
            BufferedReader reader = new BufferedReader(new FileReader(IdRecordPath));
            String line = reader.readLine();
            idNumber = Integer.parseInt(line) + 1;
            // Update the value and write back to the .id file
            FileWriter writer = new FileWriter(IdRecordPath);
            BufferedWriter buffer = new BufferedWriter(writer);
            buffer.write(String.valueOf(idNumber));
            buffer.close();
            writer.close();
            reader.close();
            data.add(String.valueOf(idNumber)); // Update the id file about this table(file).
            // For loop to store the data -> need to check the number of ,
            for (int i = index+1; i < token.tokens.size()-2; i++) { // should be the data now
                if (!token.tokens.get(i).equals(",")) {
                    data.add(token.tokens.get(i));
                }
            }
        }
        curCommandStatus = table.addFileContent(data, filePath);
        return curCommandStatus;
    }

    private String valueListCheck(ArrayList<String> valueList){



        curCommandStatus = "[ERROR]Error occur in valueListCheck function.";
        return curCommandStatus;
    }

    //private String valueCheck()
}


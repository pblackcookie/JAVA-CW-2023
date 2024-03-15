package edu.uob;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static edu.uob.GlobalMethod.*;

public class DBParser {
    protected int index = 0; // use to indicate the current token
    protected String curCommandStatus = "[OK]Haven't finished that function or parser.";
    protected String curCommand;
    ArrayList<String> attributes = new ArrayList<>();
    ArrayList<String> data = new ArrayList<>();
    CommandToken token = new CommandToken(); // storage all tokens
    DatabaseProcess database = new DatabaseProcess();
    FileProcess table = new FileProcess();


    ArrayList<String> symbol = new ArrayList<>(Arrays.asList("!", "#", "$","%","&","(",")","*","+",",","-",".","/", ":",";",
            ">","=","<","?","@","[","\\","]","^","_","`","{","}","~")); //29

    ArrayList<String> keyWords = new ArrayList<>(Arrays.asList("USE","CREATE","DROP","ALTER","INSERT","SELECT","UPDATE",
            "DELETE","JOIN","TRUE","FALSE","DATABASE","TABLE","INTO","VALUES","FROM","WHERE","SET","AND","ON","ADD",
            "OR", "NULL","LIKE")); //24


    public DBParser(String command){
        token.setup(command); // get all tokens from command
        curCommand = command;
    }

//    private String parserCommandType() throws IOException {
//        String curToken = token.tokens.get(index);
//        switch (curToken.toUpperCase()){
//            case "USE":
//                index++;
//                parserUse();
//                break;
//            case "CREATE":
//                index++;
//                ParserCreateCommand a = new ParserCreateCommand(curCommand,index);
//                curCommandStatus =  a.parserCreate();
//                break;
//            case "DROP":
//                index++;
//                parserDrop();
//                break;
//            case "ALTER": //parserAlter();
//            case "INSERT":
//                index++;
//                parserInsert();
//                break;
//            case "SELECT":
//                index++;
//                parserSelect();
//                break;
//            case "UPDATE": //parserUpdate();
//            case "DELETE": //parserDelete();
//            case "JOIN": //parserJoin();
//                break;
//            default:
//                curCommandStatus = "[ERROR]Invalid commandType";
//        }
//        return curCommandStatus;
//    }
    //When command type = 'USE'
    private String parserUse() throws IOException {
        String curToken = token.tokens.get(index);
        if(token.tokens.size() != 3){
            curCommandStatus = "[ERROR]Invalid syntax.";
            return curCommandStatus;
        }
        curCommandStatus = nameCheck(curToken);
        if (curCommandStatus.contains("[ERROR]")){
            return curCommandStatus;
        }
        curCommandStatus = database.useDatabase(curToken);
        setCurDatabaseName(curToken);
        return curCommandStatus;
    }



    // When command type = 'DROP'
    private String parserDrop() throws IOException {
        String curToken = token.tokens.get(index);
        switch (curToken.toUpperCase()) {
            case "DATABASE":
                index++;
                parserDropDatabase();
                break;
            case "TABLE":
                index++;
                parserDropTable();
                break;
            default:
                //System.out.println("Invalid drop command. Please use [TABLE] or [DATABASE]");
                curCommandStatus = "[ERROR]Invalid drop command. Please use [TABLE] or [DATABASE]";
        }
        return curCommandStatus;
    }
    private String parserDropDatabase() throws IOException {
        String curToken = token.tokens.get(index);
        curCommandStatus = database.dropDatabase(curToken);
        setCurDatabaseName("null"); //
        return curCommandStatus;
    }
    private String parserDropTable() throws IOException {
        String curToken = token.tokens.get(index);
        String curDatabase = getCurDatabaseName();
        curCommandStatus = table.dropFile(curToken,curDatabase);
        return curCommandStatus;
    }


    // // When command type = 'INSERT'
    // TODO implement the logic and check in the ();
    private String parserInsert() throws IOException {
        // command length check
        if(token.tokens.size() < 8){
            curCommandStatus = "[ERROR]Invalid insert command - no completed.";
            return curCommandStatus;
        }
        int idNumber;
        String filePath = database.getCurDatabasePath(getCurDatabaseName()) + File.separator + token.tokens.get(index+1) + ".tab";
        String curToken = token.tokens.get(index);
        if(!curToken.equalsIgnoreCase("INTO")){
            curCommandStatus = "[ERROR] Missing or wrong the 'INTO'";
            return curCommandStatus;
        }else{
            index++; // should be the table name now
            curToken = token.tokens.get(index);
            ArrayList<String> curFiles;
            curFiles = table.displayFiles(getCurDatabaseName());
            if(!curFiles.contains(curToken + ".tab")){
                curCommandStatus = "[ERROR]: Select file doesn't exists.";
                return curCommandStatus;
            }
            // Table exists ,so Read the id file to see which id it should be now
            String IdRecordPath = database.getCurDatabasePath(getCurDatabaseName()) + File.separator + curToken + ".id";
            BufferedReader reader = new BufferedReader(new FileReader(IdRecordPath));
            String line = reader.readLine();
            idNumber = Integer.parseInt(line) + 1;
            // Update the value and write back to the .id file
            FileWriter writer = new FileWriter(IdRecordPath);
            BufferedWriter buffer = new BufferedWriter(writer);
            buffer.write(String.valueOf(idNumber));
            buffer.close();
            writer.close();
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
            int minicheck = index + 1;
            System.out.println("NOW TOKEN: "+ (token.tokens.get(minicheck)));
            if((token.tokens.get(index+1)).equals(")")){
                curCommandStatus = "[ERROR] Missing Attributes.";
                return curCommandStatus;
            }
            if(!token.tokens.get(token.tokens.size() - 2).equals(")")) {
                // In order to prevent the situation like 'create table test(ss, mark, kkk)deaf;' occur.
                curCommandStatus = "[ERROR]Invalid format: Error occurs between ')' and ';'. ";
                return curCommandStatus;
            }
            data.add(String.valueOf(idNumber)); // Update the id file about this table(file).
            // For loop to store the data -> need to check the number of ,
            for (int i = index+1; i < token.tokens.size()-2; i++) { // should be the data now
                if (!token.tokens.get(i).equals(",")) {
                    String checkName = token.tokens.get(i);
                    if(checkName.contains("[ERROR]")){ // Invalid attribute name
                        curCommandStatus = checkName;
                        return  curCommandStatus;
                    }
                    data.add(token.tokens.get(i));
                }
            }
        }
        curCommandStatus = table.addFileContent(data, filePath);
        return curCommandStatus;
    }
    // // When command type = 'SELECT'
    private String parserSelect() throws IOException {
        String curToken  = token.tokens.get(index);
        //length check
        if(token.tokens.size() < 5){
            curCommandStatus = "[ERROR]Invalid SELECT command - no completed.";
            return curCommandStatus;
        }
        if(token.tokens.size() == 5){
            //System.out.println("TEST: In the insert valid command ");
            if(curToken.equals("*")) { // if * show total content
                index++;
                curToken = token.tokens.get(index); // should be 'FROM' now
                if (!curToken.equalsIgnoreCase("FROM")) {
                    curCommandStatus = "[ERROR]Missing or typo FROM here.";
                    return curCommandStatus;
                }
                index++;
                curToken = token.tokens.get(index); // should be table name now
                // DONE: Check the file(table) exists first then check the valid name.
                curCommandStatus = nameCheck(curToken);
                ArrayList<String> existTables = table.displayFiles(getCurDatabaseName());
                for (String t: existTables){
                    if((curToken + ".tab").contains(t)){
                        if (curCommandStatus.contains("[ERROR]")) {
                            return curCommandStatus;
                        }
                        curCommandStatus = "[OK]\n" + table.showFileContent(curToken, getCurDatabaseName());
                        return curCommandStatus;
                    }
                }
                curCommandStatus = "[ERROR]Selected table dos not exist.";
                return curCommandStatus;
            }
        }
        // In this situation, the SELECT must add some conditions...
        if(token.tokens.size() >5){
            return "[OK]In the condition now,";
        }
        return "[ERROR]";
    }




    // Check the database name or table name is valid or not
    // Contain changing
    protected String nameCheck(String curName){
        curName = curName.toUpperCase();
        for (String s :symbol) {
            if(curName.equals(s)){
                return "[ERROR]Name contains illegal symbol(s): " + s;
            }
            for(String word: keyWords){
                if(curName.equals(word)){
                    return "[ERROR]Name contains keyword(s): " + word;
                }
            }
        }
        return "[OK]Valid Name";
    }


    // Check if the attributes is valid    OR
    // check the first ( , then start write into arraylist till the length - 2
    // If the format is correct, length - 2 should be the previous one in )
    protected String attributeCheck(ArrayList<String> attributes){
        System.out.println("In attributes check:" + attributes);
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
        System.out.println("Check out now is :" + checkNow);
        if(!checkNow.equals(",")){ //First one need to check the valid
            curCommandStatus= nameCheck(checkNow);
            if (curCommandStatus.contains("[ERROR]")){
                return curCommandStatus;
            }
            // After checking the name still valid.
            attributes.remove(0);
            if(attributes.isEmpty()){
                curCommandStatus = "[OK]";
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
            System.out.println("In the start with , , now: " + checkNow);
            curCommandStatus= nameCheck(checkNow);
            if (curCommandStatus.contains("[ERROR]")){
                return curCommandStatus;
            }
            attributes.remove(0); // remove ,
            attributes.remove(0); // remove name
            System.out.println("In the , + name part:" + attributes);
            if(attributes.isEmpty()){
                curCommandStatus = "[OK]";
                return curCommandStatus;
            }else {
                attributeCheck(attributes); // Continuing checking....
            }
            return curCommandStatus;
        }
    }
}

package edu.uob;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;

public class ExampleDBTests {

    private DBServer server;

    // Create a new server _before_ every @Test
    @BeforeEach
    public void setup() {
        server = new DBServer();
    }

    // Random name generator - useful for testing "bare earth" queries (i.e. where tables don't previously exist)
    private String generateRandomName() {
        String randomName = "";
        for(int i=0; i<10 ;i++) randomName += (char)( 97 + (Math.random() * 25.0));
        return randomName;
    }

    private String sendCommandToServer(String command) {
        // Try to send a command to the server - this call will timeout if it takes too long (in case the server enters an infinite loop)
        return assertTimeoutPreemptively(Duration.ofMillis(1000), () -> { return server.handleCommand(command);},
        "Server took too long to respond (probably stuck in an infinite loop)");
    }

    // A basic test that creates a database, creates a table, inserts some test data, then queries it.
    // It then checks the response to see that a couple of the entries in the table are returned as expected
    @Test
    public void testBasicCreateAndQuery() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
        sendCommandToServer("INSERT INTO marks VALUES ('Simon', 65, TRUE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Sion', 55, TRUE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Rob', 35, FALSE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Chris', 20, FALSE);");
        String response = sendCommandToServer("SELECT * FROM marks;");
        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        assertFalse(response.contains("[ERROR]"), "A valid query was made, however an [ERROR] tag was returned");
        assertTrue(response.contains("Simon"), "An attempt was made to add Simon to the table, but they were not returned by SELECT *");
        assertTrue(response.contains("Chris"), "An attempt was made to add Chris to the table, but they were not returned by SELECT *");
    }

    // A test to make sure that querying returns a valid ID (this test also implicitly checks the "==" condition)
    // (these IDs are used to create relations between tables, so it is essential that suitable IDs are being generated and returned !)
    @Test
    public void testQueryID() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
        sendCommandToServer("INSERT INTO marks VALUES ('Simon', 65, TRUE);");
        String response = sendCommandToServer("SELECT id FROM marks WHERE name == 'Simon';");
        // Convert multi-lined responses into just a single line
        String singleLine = response.replace("\n"," ").trim();
        // Split the line on the space character
        String[] tokens = singleLine.split(" ");
        // Check that the very last token is a number (which should be the ID of the entry)
        String lastToken = tokens[tokens.length-1];
        try {
            Integer.parseInt(lastToken);
        } catch (NumberFormatException nfe) {
            fail("The last token returned by `SELECT id FROM marks WHERE name == 'Simon';` should have been an integer ID, but was " + lastToken);
        }
    }

    // A test to make sure that databases can be reopened after server restart
    @Test
    public void testTablePersistsAfterRestart() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
        sendCommandToServer("INSERT INTO marks VALUES ('Simon', 65, TRUE);");
        // Create a new server object
        server = new DBServer();
        sendCommandToServer("USE " + randomName + ";");
        String response = sendCommandToServer("SELECT * FROM marks;");
        assertTrue(response.contains("Simon"), "Simon was added to a table and the server restarted - but Simon was not returned by SELECT *");
    }

    // Test to make sure that the [ERROR] tag is returned in the case of an error (and NOT the [OK] tag)
    @Test
    public void testForErrorTag() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
        sendCommandToServer("INSERT INTO marks VALUES ('Simon', 65, TRUE);");
        String response = sendCommandToServer("SELECT * FROM libraryfines;");
        assertTrue(response.contains("[ERROR]"), "An attempt was made to access a non-existent table, however an [ERROR] tag was not returned");
        assertFalse(response.contains("[OK]"), "An attempt was made to access a non-existent table, however an [OK] tag was returned");
    }

    // Test to make sure the database folder can be created correctly
    @Test
    public void testDatabaseCreate() throws IOException {
        String databaseName[] = new String[2];
        databaseName[0] = "company";
        databaseName[1] = "university";
        DatabaseProcess testDatabase = new DatabaseProcess();
        testDatabase.createDatabase(databaseName[0]);
        testDatabase.createDatabase(databaseName[1]);
    }

    // To make sure the database drop logic is correct
    @Test
    public void testDatabaseDrop() throws IOException {
        String databaseName[] = new String[3];
        databaseName[0] = "BigBrother";
        databaseName[1] = "Is";
        databaseName[2] = "WatchingYou";
        DatabaseProcess testDatabase = new DatabaseProcess();
        testDatabase.createDatabase(databaseName[0]);
        testDatabase.createDatabase(databaseName[1]);
        testDatabase.createDatabase(databaseName[2]);
        testDatabase.dropDatabase(databaseName[0]);
        testDatabase.dropDatabase(databaseName[1]);
        testDatabase.dropDatabase(databaseName[2]);
        testDatabase.dropDatabase("university");

    }
    // To make sure the database selected to use logic is correct
    @Test
    public void testDatabaseUse() throws IOException {
        String databaseName[] = new String[3];
        databaseName[0] = "q";
        databaseName[1] = "qq";
        databaseName[2] = "nomeaning";
        DatabaseProcess testDatabase = new DatabaseProcess();
        testDatabase.createDatabase(databaseName[0]);
        testDatabase.createDatabase(databaseName[1]);
        testDatabase.createDatabase(databaseName[2]);
        testDatabase.dropDatabase(databaseName[0]);
        testDatabase.dropDatabase(databaseName[2]);
        testDatabase.useDatabase(databaseName[0]);
        testDatabase.useDatabase(databaseName[1]);

    }
    @Test
    public void testDocxReference(){
        String response=sendCommandToServer("CREATE DATABASE markbook;");
        assertTrue(response.contains("[OK]"));
        assertFalse(response.contains("[ERROR]"));
        response=sendCommandToServer("USE markbook;");
        assertTrue(response.contains("[OK]"));
        assertFalse(response.contains("[ERROR]"));
        response=sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
        assertTrue(response.contains("[OK]"));
        assertFalse(response.contains("[ERROR]"));
        response=sendCommandToServer("INSERT INTO marks VALUES ('Simon', 65, TRUE);");
        assertTrue(response.contains("[OK]"));
        assertFalse(response.contains("[ERROR]"));
        response=sendCommandToServer("INSERT INTO marks VALUES ('Sion', 55, TRUE);");
        assertTrue(response.contains("[OK]"));
        assertFalse(response.contains("[ERROR]"));
        response=sendCommandToServer("INSERT INTO marks VALUES ('Rob', 35, FALSE);");
        assertTrue(response.contains("[OK]"));
        assertFalse(response.contains("[ERROR]"));
        response=sendCommandToServer("INSERT INTO marks VALUES ('Chris', 20, FALSE);");
        assertTrue(response.contains("[OK]"));
        assertFalse(response.contains("[ERROR]"));
        response=sendCommandToServer("SELECT * FROM marks;");
        assertTrue(response.contains("[OK]"));
        assertFalse(response.contains("[ERROR]"));
        assertTrue(response.contains("id"));
        assertTrue(response.contains("name"));
        assertTrue(response.contains("mark"));
        assertTrue(response.contains("pass"));
        assertTrue(response.contains("1"));
        assertTrue(response.contains("Simon"));
        assertTrue(response.contains("65"));
        assertTrue(response.contains("TRUE"));
        assertTrue(response.contains("2"));
        assertTrue(response.contains("Sion"));
        assertTrue(response.contains("55"));
        assertTrue(response.contains("FALSE"));
        assertTrue(response.contains("3"));
        assertTrue(response.contains("Rob"));
        assertTrue(response.contains("35"));
        assertTrue(response.contains("4"));
        assertTrue(response.contains("Chris"));
        assertTrue(response.contains("20"));
        response=sendCommandToServer("SELECT * FROM marks WHERE name != 'Sion';");
        assertTrue(response.contains("[OK]"));
        assertFalse(response.contains("[ERROR]"));
        assertFalse(response.contains("Sion"));
        response=sendCommandToServer("SELECT * FROM marks WHERE pass == TRUE;");
        assertFalse(response.contains("FALSE"));
        response=sendCommandToServer("CREATE TABLE coursework (task, submission);");
        assertTrue(response.contains("[OK]"));
        assertFalse(response.contains("[ERROR]"));
        response=sendCommandToServer("INSERT INTO coursework VALUES ('OXO',3);");
        assertTrue(response.contains("[OK]"));
        assertFalse(response.contains("[ERROR]"));
        response=sendCommandToServer("INSERT INTO coursework VALUES ('DB',1);");
        assertTrue(response.contains("[OK]"));
        assertFalse(response.contains("[ERROR]"));
        response=sendCommandToServer("INSERT INTO coursework VALUES ('OXO',4);");
        assertTrue(response.contains("[OK]"));
        assertFalse(response.contains("[ERROR]"));
        response=sendCommandToServer("INSERT INTO coursework VALUES ('STAG',2);");
        assertTrue(response.contains("[OK]"));
        assertFalse(response.contains("[ERROR]"));
        response=sendCommandToServer("SELECT * FROM coursework;");
        assertTrue(response.contains("[OK]"));
        assertFalse(response.contains("[ERROR]"));
        response=sendCommandToServer("JOIN coursework AND marks ON submission AND id;");
        assertTrue(response.contains("[OK]"));
        assertFalse(response.contains("[ERROR]"));
        assertTrue(response.contains("Rob"));
        assertTrue(response.contains("Simon"));
        assertTrue(response.contains("Chris"));
        assertTrue(response.contains("Sion"));
        response=sendCommandToServer("UPDATE marks SET mark = 38 WHERE name == 'Chris';");
        assertTrue(response.contains("[OK]"));
        assertFalse(response.contains("[ERROR]"));
        response=sendCommandToServer("SELECT * FROM marks WHERE name == 'Chris';");
        assertTrue(response.contains("38"));
        response=sendCommandToServer("DELETE FROM marks WHERE name == 'Sion';");
        assertTrue(response.contains("[OK]"));
        assertFalse(response.contains("[ERROR]"));
        response=sendCommandToServer("SELECT * FROM marks;");
        assertTrue(response.contains("[OK]"));
        assertFalse(response.contains("[ERROR]"));
        assertFalse(response.contains("Sion"));
        response=sendCommandToServer("SELECT * FROM marks WHERE (pass == FALSE) AND (mark > 35);");
        assertTrue(response.contains("[OK]"));
        assertFalse(response.contains("[ERROR]"));
        assertTrue(response.contains("Chris"));
        response=sendCommandToServer("SELECT * FROM marks WHERE name LIKE 'i';");
        assertTrue(response.contains("[OK]"));
        assertFalse(response.contains("[ERROR]"));
        assertTrue(response.contains("Simon"));
        assertTrue(response.contains("Chris"));
        response=sendCommandToServer("SELECT id FROM marks WHERE pass == FALSE;");
        assertTrue(response.contains("[OK]"));
        assertFalse(response.contains("[ERROR]"));
        assertTrue(response.contains("3"));
        assertTrue(response.contains("4"));
        response=sendCommandToServer("SELECT name FROM marks WHERE mark>60;");
        assertTrue(response.contains("[OK]"));
        assertFalse(response.contains("[ERROR]"));
        assertTrue(response.contains("Simon"));
        response=sendCommandToServer("DELETE FROM marks WHERE mark<40;");
        assertTrue(response.contains("[OK]"));
        assertFalse(response.contains("[ERROR]"));
        response=sendCommandToServer("SELECT * FROM marks;");
        assertTrue(response.contains("[OK]"));
        assertFalse(response.contains("[ERROR]"));
        assertTrue(response.contains("1"));
        assertFalse(response.contains("2"));
        assertFalse(response.contains("3"));
        assertFalse(response.contains("4"));
        response=sendCommandToServer("ALTER TABLE marks ADD age;");
        assertTrue(response.contains("[OK]"));
        assertFalse(response.contains("[ERROR]"));
        response=sendCommandToServer("SELECT * FROM marks;");
        assertTrue(response.contains("[OK]"));
        assertFalse(response.contains("[ERROR]"));
        assertTrue(response.contains("age"));
        response=sendCommandToServer("UPDATE marks SET age = 35 WHERE name == 'Simon';");
        assertTrue(response.contains("[OK]"));
        assertFalse(response.contains("[ERROR]"));
        response=sendCommandToServer("SELECT * FROM marks;");
        assertTrue(response.contains("[OK]"));
        assertFalse(response.contains("[ERROR]"));
        assertTrue(response.contains("35"));
        assertTrue(response.contains("pass"));
        response=sendCommandToServer("ALTER TABLE marks DROP pass;");
        assertTrue(response.contains("[OK]"));
        assertFalse(response.contains("[ERROR]"));
        response=sendCommandToServer("SELECT * FROM marks;");
        assertTrue(response.contains("[OK]"));
        assertFalse(response.contains("[ERROR]"));
        assertFalse(response.contains("pass"));
        response=sendCommandToServer("SELECT * FROM marks");
        assertTrue(response.contains("[ERROR]"));
        assertFalse(response.contains("[OK]"));
        response=sendCommandToServer("SELECT * FROM crew;");
        assertTrue(response.contains("[ERROR]"));
        assertFalse(response.contains("[OK]"));
        response=sendCommandToServer("SELECT height FROM marks WHERE name == 'Chris';");
        assertTrue(response.contains("[ERROR]"));
        assertFalse(response.contains("[OK]"));
        response=sendCommandToServer("DROP TABLE marks;");
        assertTrue(response.contains("[OK]"));
        assertFalse(response.contains("[ERROR]"));
        response=sendCommandToServer("DROP DATABASE markbook;");
        assertTrue(response.contains("[OK]"));
        assertFalse(response.contains("[ERROR]"));


    }
}

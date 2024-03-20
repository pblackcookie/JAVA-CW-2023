# JAVA-CW-2023
UoB username: [vz23211]

## Assignment 1 : Databases
### Understand the assignment requirements first.

*Attention* - **running codes on Windows system**

For running the Server or Client successfully, it's needing to *administrator permissions*.Because it has two main classes to repersenting the server or client, so needing specify which main class should be compile and run.

`mvnw clean compile` - good command before each running command.

For running Server: `mvnw exec:java -Dexec.mainClass="edu.uob.DBServer"`

For running Client: `mvnw exec:java -Dexec.mainClass="edu.uob.DBClient"`

Emmm.Alright.Just finding one simpler way to run them by @id

`mvnw exec:java@"server"` for running server and `mvnw exec:java@"client"` for running client.

**On linux system**

`./mvnw exec:java@server` and `./mvnw exec:java@client`

### About file read / write

Add the class name *FileProcess* to process various kind of operation relevant to the file and folder.

Finishing printing the content in the terminal when the tables are not empty.

Implenment the function that create the database in folder databases and empty table in database.

### About handle command

Continuing implement the table/database functions.

Statring to add some logic process functions into handleCommandhandle function in the DBServer class. 

### Restructure all the implentment methods and class.

1. Add the recursion function the check the attributes name is valid or not.

2. Add the lowercase convert when storage all the data.

### Implement the alter function and insert function.

1. Add the value list check fucntion to see whether the insert data is valid or not. If all the values valid, check it length is valid or not.

2. Rewrite some codes from alter command.

### Implement the join function and the join command parser.

1. now the database can join two different tables now.

2. Fix some bugs that the insert will insert the 'String' type with "'". --> remove it.

### Implement the update and delete function for the simplest test.

1. Create delete class and update successfully.

2. Add only one condition check.

3. Add current database use check when in select command.

# JAVA-CW-2023
UoB username: [vz23211]

## Assignment 1 : Databases
1. Understand the assignment requirements first.
*Attention* - **running codes on Windows system**
For running the Server or Client successfully, it's needing to *administrator permissions*.Because it has two main classes to repersenting the server or client, so needing specify which main class should be compile and run.
`mvnw clean compile` - good command before each running command.
For running Server: `mvnw exec:java -Dexec.mainClass="edu.uob.DBServer"`
For running Client: `mvnw exec:java -Dexec.mainClass="edu.uob.DBClient"`
Emmm.Alright.Just finding one simpler way to run them by @id
`mvnw exec:java@"server"` for running server and `mvnw exec:java@"client"
` for running client.
**On linux system**
`./mvnw exec:java@server` and `./mvnw exec:java@client
`
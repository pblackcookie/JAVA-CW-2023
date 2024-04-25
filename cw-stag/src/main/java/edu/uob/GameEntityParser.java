package edu.uob;

import java.util.ArrayList;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.ParseException;
import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Node;
import com.alexmerz.graphviz.objects.Edge;

import javax.xml.crypto.NodeSetData;

public class GameEntityParser {
    // Read the config file
    static Map<String, String> pathMap = new HashMap<>();

    public static void main(String[] args) {
        fileReader();
        // For test the 'path' is corrected stored or not
        for (Map.Entry<String, String> entry : pathMap.entrySet()) {
            String from = entry.getKey();
            String to = entry.getValue();
            System.out.println( from + " -> " +  to);
        }
    }

    //Reading the entities config file
    public static void fileReader() {
        try {
            Parser parser = new Parser();
            FileReader reader = new FileReader("config" + File.separator + "basic-entities.dot");
            parser.parse(reader);
            Graph wholeDocument = parser.getGraphs().get(0);
            ArrayList<Graph> sections = wholeDocument.getSubgraphs(); // get the file information

            // In first subgraph -> get all locations & description
            ArrayList<Graph> locations = sections.get(0).getSubgraphs();
            for(Graph location: locations){
                Node locationDetails = location.getNodes(false).get(0);
                // Get the each location's name and description
                String locationName = locationDetails.getId().getId();
                String description = locationDetails.getAttributes().get("description");
                // Put the entities to the Location object
                Location currentLocation = new Location(locationName,description);

                // All entities on each location
                ArrayList<Graph> entities = location.getSubgraphs();
                for(Graph entity: entities){
                    ArrayList<Node> nodeDetails = entity.getNodes(false);
                    String currentEntity = entity.getId().getId();
                    switch(currentEntity) {
                        case "artefacts":
                            for(Node node: nodeDetails){
                                String name = node.getId().getId();
                                description = node.getAttributes().get("description");
                                Artefacts currentArtefacts = new Artefacts(name,description);
                            }
                            break;
                        case "furniture":
                            for(Node node: nodeDetails){
                                String name = node.getId().getId();
                                description = node.getAttributes().get("description");
                                Furniture currentFurniture = new Furniture(name,description);
                            }
                            break;
                        case "characters":
                            for(Node node: nodeDetails){
                                String name = node.getId().getId();
                                description = node.getAttributes().get("description");
                                Characters currentCharacters = new Characters(name,description);
                            }
                            break;
                        default:
                            System.out.println("Not current Entity now.");
                            break;
                    }
                }
            }
            // In the second subgraph -> get all paths
            ArrayList<Edge> paths = sections.get(1).getEdges();
            // for loop: to store tha path information and direction to the Hashmap
            for (Edge path : paths) {
                String fromLocation = path.getSource().getNode().getId().getId();
                String toLocation = path.getTarget().getNode().getId().getId();
                pathMap.put(fromLocation, toLocation);
            }
        } catch (FileNotFoundException fnfe) {
            System.out.println("FileNotFoundException was thrown when attempting to read basic entities file");
        } catch (ParseException pe) {
            System.out.println("ParseException was thrown when attempting to read basic entities file");
        }
    }



}

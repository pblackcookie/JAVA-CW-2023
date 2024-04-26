package edu.uob;

import java.io.File;
import java.util.ArrayList;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.ParseException;
import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Node;
import com.alexmerz.graphviz.objects.Edge;

public class GameEntityParser {
    // Read the config file
    Map<String, String> pathMap = new HashMap<>();
    Map<Location,HashMap<String, HashSet<GameEntity>>> entitiesMap = new HashMap<>();


    //Reading the entities config file
    public void fileReader(String filePath) {
        try {
            Parser parser = new Parser();
            FileReader reader = new FileReader(filePath);
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
                    String entityName;
                    String entityDescription;
                    ArrayList<Node> nodeDetails = entity.getNodes(false);
                    String currentEntity = entity.getId().getId();
                    // Get the detail node information from each location
                    for(Node node: nodeDetails){
                        entityName = node.getId().getId();
                        entityDescription = node.getAttributes().get("description");
                        GameEntity newEntity = createEntity(currentEntity, entityName, entityDescription);
                        if (newEntity != null) {
                            entitiesLoading(currentLocation, newEntity, entityName);
                        }
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

    // Create the different entities when on the different locations with different entities
    private GameEntity createEntity(String currentEntity, String entityName, String entityDescription){
        switch(currentEntity) {
            case "artefacts":
                return new Artefacts(entityName, entityDescription);
            case "furniture":
                return new Furniture(entityName, entityDescription);
            case "characters":
                return new Characters(entityName, entityDescription);
            default:
                return null;
        }
    }

    private void entitiesLoading(Location currentLocation, GameEntity currentEntity, String entityName){
        // get or create current location's inner HashMap
        HashMap<String, HashSet<GameEntity>> innerMap = entitiesMap.getOrDefault(currentLocation, new HashMap<>());
        // get or create  current entityName's HashSet
        HashSet<GameEntity> entitySet = innerMap.getOrDefault(entityName, new HashSet<>());
        // add the object into HashSet
        entitySet.add(currentEntity);
        // put the updated HashSet back to inner HashMap
        innerMap.put(entityName, entitySet);
        // put the inner HashMap into HashMap
        entitiesMap.put(currentLocation, innerMap);
    }



}

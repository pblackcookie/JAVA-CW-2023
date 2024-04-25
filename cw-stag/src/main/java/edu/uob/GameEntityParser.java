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

    public static void fileReader() {
        try {
            //Create the parser to read the config file
            Parser parser = new Parser();
            FileReader reader = new FileReader("config" + File.separator + "basic-entities.dot");
            parser.parse(reader);
            Graph wholeDocument = parser.getGraphs().get(0);
            ArrayList<Graph> sections = wholeDocument.getSubgraphs(); // get the list

            // In first subgraph -> get all locations
            ArrayList<Graph> locations = sections.get(0).getSubgraphs();

            // In the second subgraph -> get all paths
            ArrayList<Edge> paths = sections.get(1).getEdges();
            // for loop: to store from and to location to the map
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

package edu.uob;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class GameEngine {
    HashMap<String, String> pathMap;
    HashMap<Location,HashMap<String, HashSet<GameEntity>>> entitiesMap;
    HashMap<String,HashSet<GameAction>> actions;

    public GameEngine(HashMap<String, String> pathMap, HashMap<Location, HashMap<String, HashSet<GameEntity>>> entitiesMap, HashMap<String, HashSet<GameAction>> actions) {
        this.pathMap = pathMap;
        this.entitiesMap = entitiesMap;
        this.actions = actions;
    }

    public String builtInCommand(String command, Player player){
        // create all built in commands
        String builtInTrigger = null;
        String currentCommand = command.toLowerCase();
        String[] words = currentCommand.split(" ");
        for(String word: words){
            //look the trigger first
            if(word.contains("inv")){
                return inv(player);
            }else if(word.contains("look")){
                return look(player);
            }else if(word.contains("get")){
                return get(player);
            }else if(word.contains("drop")){
                return drop(player);
            }else if(word.contains("goto")){
                return goto_(player);
            }
        }
        return "[Warning]Can not found the current command.";
    }

    // inventory (or inv for short) lists all of the artefacts currently being carried by the player
    public String inv(Player player){
        HashMap<String, HashSet<GameEntity>> storeRoom = null;
        StringBuilder artefactsNow = new StringBuilder();
        Artefacts bag;
        System.out.println("Now the player is: " + player.getName());
        // Get the current players store room
        Set<Location> locations = entitiesMap.keySet();
        for(Location location: locations){
            if(location.getName().equalsIgnoreCase("storeroom")){
                storeRoom = entitiesMap.get(location);
                break;
            }
        }
        if(storeRoom != null && storeRoom.containsKey("artefacts")){
            HashSet<GameEntity> artefactsSet = storeRoom.get("artefacts");
            for(GameEntity artefact: artefactsSet)
                artefactsNow.append(artefact.toString());
        }
        return artefactsNow.toString();
    }
    // get picks up a specified artefact from the current location and adds it into player's inventory
    public String get(Player player){
        return "";
    }
    // drop puts down an artefact from player's inventory and places it into the current location
    public String drop(Player player){
        return "";
    }
    // goto moves the player from the current location to the specified location (if there is a path to that location)
    public String goto_(Player player){
        return "";
    }
    // look 1.prints names and descriptions of entities in the current location and 2.lists paths to other locations
    public String look(Player player){
        String currentLocation = player.getCurrentLocation();
        String locationDetails = "";
        String pathDetails = "";
        Set<Location> locations = entitiesMap.keySet();
        HashMap<String, HashSet<GameEntity>> currentEntities;
        String EntityDetail = "";
        for (Location location : locations){
            if(location.getName().equals(currentLocation)){
                locationDetails = location.toString();
                currentEntities = entitiesMap.get(location);
                Set<String> entities = currentEntities.keySet();
                for (String entityType : entities){
                    EntityDetail += currentEntities.get(entityType).toString() + "\n";
                }
                break;
            }
        }
        Set<String> paths = pathMap.keySet();
        for(String path: paths){
            if (path.equals(currentLocation)){
                pathDetails = pathMap.get(path) + " ";
            }
        }
        return locationDetails + "\n" + EntityDetail + "You can go to the: " + pathDetails;
    }
}

package edu.uob;

import java.util.ArrayList;
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
        String currentCommand = command.toLowerCase();
        String[] words = currentCommand.split(" ");
        //only the trigger need to be checked
        for(String word: words) {
            //look the trigger first
            if (word.contains("inv")) {
                return inv(player);
            } else if (word.contains("look")) {
                return look(player);
            }
        }// The case trigger + one key phrase to be checked
        for (int i = 0; i < words.length - 1; i++) {
            if(words[i].contains("get")){
                return get(player,words[i+1]);
            }else if(words[i].contains("drop")){
                return drop(player,words[i+1]);
            }else if(words[i].contains("goto")){
                return goto_(player,words[i+1]);
            }
        }
        return "[Warning]Can not found the current command.";
    }

    // inventory (or inv for short) lists all of the artefacts currently being carried by the player
    public String inv(Player player){
        HashMap<String, HashSet<GameEntity>> storeRoom = null;
        StringBuilder artefactsNow = new StringBuilder();
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
    public String get(Player player, String entity){
        System.out.println("Now the player is:" + player.getName() + "\n");
        String currentLocation = player.getCurrentLocation();
        HashMap<String, HashSet<GameEntity>> nowLocation = null;
        HashMap<String, HashSet<GameEntity>> storeRoom = null;
        Set<Location> locations = entitiesMap.keySet();

        for(Location location: locations){
            if(location.getName().equals(currentLocation)){
                nowLocation = entitiesMap.get(location);
            }
            if(location.getName().equalsIgnoreCase("storeroom")){
                storeRoom = entitiesMap.get(location);
            }
        }
        // only artefacts can be collected by the player
        HashSet<GameEntity> entitiesCanBeCollected = nowLocation.get("artefacts");
        HashSet<GameEntity> artefacts = storeRoom.get("artefacts");
        for (GameEntity entityCanBeCollected : entitiesCanBeCollected) {
            System.out.println(entityCanBeCollected);
            if(entityCanBeCollected.getName().equals(entity)){
                // Remove the entity after collecting it from location
                nowLocation.get("artefacts").remove(entityCanBeCollected);
                // Add the collected entity into the storeroom
                artefacts.add(entityCanBeCollected);
                return "You get the " + entity;
            }
        }
        return "[Warning]You can't pick up this thing. / Item does not exist";
    }

    // drop puts down an artefact from player's inventory and places it into the current location
    public String drop(Player player, String entity){
        System.out.println("Now the player is:" + player.getName() + "\n");

        return "You drop the " + entity;
    }

    // goto moves the player from the current location to the specified location (if there is a path to that location)
    public String goto_(Player player, String destination){
        Set<Location> locations = entitiesMap.keySet();
        ArrayList<String> locationNames = new ArrayList<>();
        for (Location location : locations){
            locationNames.add(location.getName());
        }
        for(String currentLocation: locationNames){
            if(currentLocation.equals(destination)){
                player.setCurrentLocation(destination);
                return "You are now in the: " + destination;
            }
        }
        return "[Warning]You entered a non-existent location.";

    }

    // look 1.prints names and descriptions of entities in the current location and 2.lists paths to other locations
    public String look(Player player){
        String currentLocation = player.getCurrentLocation();
        String locationDetails = "";
        String pathDetails = "";
        Set<Location> locations = entitiesMap.keySet();
        HashMap<String, HashSet<GameEntity>> currentEntities;
        StringBuilder EntityDetail = new StringBuilder();
        for (Location location : locations){
            if(location.getName().equals(currentLocation)){
                locationDetails = location.toString();
                currentEntities = entitiesMap.get(location);
                Set<String> entities = currentEntities.keySet();
                for (String entityType : entities){
                    EntityDetail.append(currentEntities.get(entityType).toString()).append("\n");
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

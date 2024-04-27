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
        String currentCommand = command.toLowerCase();
        if(currentCommand.contains("inv")){
            currentCommand = "inv";
        }
        switch (currentCommand){
            case "inv": return inv(player);
            case "get": return get(player);
            case "drop": return drop(player);
            case "goto": return goto_(player);
            case "look": return look(player);
            default:
                break;
        }
        return "";
    }

    // inventory (or inv for short) lists all of the artefacts currently being carried by the player
    public String inv(Player player){
        HashMap<String, HashSet<GameEntity>> storeRoom = null;
        String artefactsNow = "";
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
            System.out.println(artefactsSet);
            artefactsNow = String.valueOf(artefactsSet);
        }
        return artefactsNow;
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
    // look prints names and descriptions of entities in the current location and lists paths to other locations
    public String look(Player player){
        return "";
    }
}

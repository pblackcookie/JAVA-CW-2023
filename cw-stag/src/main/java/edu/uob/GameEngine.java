package edu.uob;

import java.util.*;

public class GameEngine {
    HashMap<String, HashSet<String>> pathMap;
    HashMap<Location,HashMap<String, HashSet<GameEntity>>> entitiesMap;
    HashMap<String,HashSet<GameAction>> actions;
    HashMap<String, Player> playerMap;
    // Entities
    HashSet<String> locations = new HashSet<>();
    HashSet<String> artefacts = new HashSet<>();
    HashSet<String> furniture = new HashSet<>();
    HashSet<String> characters = new HashSet<>();

    public GameEngine(HashMap<String, HashSet<String>> pathMap,
                      HashMap<Location, HashMap<String, HashSet<GameEntity>>> entitiesMap,
                      HashMap<String, HashSet<GameAction>> actions,
                      HashMap<String, Player> playerMap) {
        this.pathMap = pathMap;
        this.entitiesMap = entitiesMap;
        this.actions = actions;
        this.playerMap = playerMap;
    }

    public String commandParser(String command){
        // get current player name & check the player exists or not.
        String currentPlayer = command.split(":")[0].trim();
        Player player = playerChecker(currentPlayer);
        // Split the command.
        HashSet<String> wordSet = new HashSet<>(Arrays.asList(command.split(" ")));
        // Check the trigger && entity valid or not
        String trigger = triggerChecker(wordSet);
        entitiesSet(player);
        String entity = entityChecker(trigger, wordSet);
        if(trigger.contains("Warning")||entity.contains("Warning")){
            return "[Warning]No valid trigger/entity or trigger/entity more than one.";
        } else if(trigger.contains("inv") || trigger.equals("look") ||
                trigger.equals("get")||trigger.equals("drop") || trigger.equals("goto")){
            return builtInCommand(trigger,entity,player);
        }
        return "[Warning]This function has not been implemented.";
    }

    // Check if the player exists or not
    // If player does not exists, create the new object and put it into player map.
    public Player playerChecker(String playerName){
        Player nowPlayer = playerMap.get(playerName);
        if(nowPlayer == null){ // put thr new player into player map
            nowPlayer = new Player(playerName,"","cabin",entitiesMap);
            playerMap.put(playerName,nowPlayer);
        }
        return nowPlayer;
    }

    // Check if the trigger is valid or not(only 1 trigger is valid)
    public String triggerChecker(HashSet<String> words){
        String curTrigger = "";
        int triggerCount = 0;
        //Check if the command contains builtin commands
        for(String word: words){
            if(word.contains("inv")){ curTrigger = word; triggerCount++;
            }else if(word.contains("goto")){ curTrigger = word; triggerCount++;
            }else if(word.contains("look")){ curTrigger = word; triggerCount++;
            }else if(word.contains("get")){ curTrigger = word; triggerCount++;
            }else if(word.contains("drop")){ curTrigger = word; triggerCount++;}
        }
        if(triggerCount != 1){
            return "[Warning]Not a valid trigger.";
        }
        return curTrigger;
    }


    // Check if the entity is valid or not(only 1 entity is valid)
    public String entityChecker(String trigger, HashSet<String> words){
        String curEntity = "";
        int entityCounter = 0;
        HashSet<String> mergedSet = new HashSet<>();
        mergedSet.addAll(locations);
        mergedSet.addAll(artefacts);
        mergedSet.addAll(furniture);
        mergedSet.addAll(characters);
        for(String word: words){
            // need one entity set
            if(trigger.equals("goto")) {
                for (String location : locations) {
                    if (word.equals(location)) {
                        curEntity = word;
                        entityCounter++;
                    }
                }
            }else if(trigger.equals("get") || trigger.equals("drop")) {
                for (String artefact : artefacts) {
                    if (word.equals(artefact)) {
                        curEntity = word;
                        entityCounter++;
                    }
                }
            }else{
                for (String entity: mergedSet){
                    if (word.equals(entity)) {
                        curEntity = word;
                        entityCounter++;
                    }
                }
            }
        }
        if((trigger.contains("inv") || trigger.equals("look")) && entityCounter != 0){
            return "[Warning]Not a valid command.";
        }else if((trigger.equals("get")||trigger.equals("drop") || trigger.equals("goto")) && entityCounter !=1){
            return "[Warning]Not a valid entity.";
        }
        return curEntity;
    }
    public void entitiesSet(Player player){
        // Get all location entities
        for (Map.Entry<Location, HashMap<String, HashSet<GameEntity>>> entry : entitiesMap.entrySet()) {
            Location location = entry.getKey();
            locations.add(location.getName()); //Update the location entities
            // Get the all entities for current location & storeroom
            if(location.getName().equals(player.getCurrentLocation())||location.getName().equals("storeroom")){
                for(Map.Entry<String, HashSet<GameEntity>> innerEntry : entry.getValue().entrySet()){
                    String entityName = innerEntry.getKey(); // artefacts or characters or furniture
                    for (GameEntity entity : innerEntry.getValue()) {
                        if(entityName.equals("artefacts")){ artefacts.add(entity.getName());}
                        if(entityName.equals("furniture")){ furniture.add(entity.getName());}
                        if(entityName.equals("characters")){ characters.add(entity.getName());}
                    }
                }
            }
        }

    }

    public String builtInCommand(String trigger, String entity, Player player){
        //look the trigger first
        if (trigger.contains("inv")) { return inv(player);
        }else if (trigger.equals("look")) { return look(player);
        }else if(trigger.equals("goto")){ return goto_(player,entity);
        }else if(trigger.equals("get")){ return get(player,entity);
        }else if(trigger.equals("drop")){ return drop(player,entity); }
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
            if(entityCanBeCollected.getName().equals(entity)){
                // Remove the entity after collecting it from location
                nowLocation.get("artefacts").remove(entityCanBeCollected);
                // Add the collected entity into the storeroom
                artefacts.add(entityCanBeCollected);
                return "You get the [" + entity + "]";
            }
        }
        return "[Warning]You can't pick up this thing. / Item does not exist";
    }

    // drop puts down an artefact from player's inventory and places it into the current location
    public String drop(Player player, String entity){
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
        // only artefacts can be dropped by the player
        HashSet<GameEntity> entitiesCanBeDropped = storeRoom.get("artefacts");
        HashSet<GameEntity> artefacts = nowLocation.get("artefacts");
        for (GameEntity entityCanBeDropped : entitiesCanBeDropped) {
            if(entityCanBeDropped.getName().equals(entity)){
                // Remove the entity after dropped it from storeroom
                storeRoom.get("artefacts").remove(entityCanBeDropped);
                // Add the collected entity into the now location
                artefacts.add(entityCanBeDropped);
                return "You drop the [" + entity +"]";
            }
        }
        return "[Warning]You can't drop this thing. / Item does not exist";
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
        return "[Warning]You entered a non-existent location. -Maybe a typo?";

    }

    // look 1.prints names and descriptions of entities in the current location and 2.lists paths to other locations
    public String look(Player player){
        String currentLocation = player.getCurrentLocation();
        String locationDetails = "";
        StringBuilder pathDetails = new StringBuilder();
        StringBuilder totalPlayers = new StringBuilder();
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

        // Get the current map
        Set<String> paths = pathMap.keySet();
        for(String path: paths){
            if (path.equals(currentLocation)){
                pathDetails.append(pathMap.get(path)).append(" ");
            }
        }

        // Get the other players
        Set<String> players = playerMap.keySet();
        for (String curPlayer: players){
            Player loaclPlayer = playerMap.get(curPlayer);
            if(loaclPlayer.getCurrentLocation().equals(currentLocation)){
                totalPlayers.append(loaclPlayer.getDescription()).append(" ");
            }
        }
        return locationDetails + "\n" + EntityDetail + "You can go to the: " + pathDetails + totalPlayers;
    }
}

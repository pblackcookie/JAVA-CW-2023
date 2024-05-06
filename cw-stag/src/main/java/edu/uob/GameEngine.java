package edu.uob;

import java.util.*;

public class GameEngine {
    HashMap<String, HashSet<String>> pathMap;
    HashMap<Location,HashMap<String, HashSet<GameEntity>>> entitiesMap;
    HashMap<String,HashSet<GameAction>> actions;
    HashMap<String, Player> playerMap;
    HashMap<String, HashSet<GameEntity>> bagMap;
    // Entities
    HashSet<String> locations = new HashSet<>();
    HashSet<String> artefacts = new HashSet<>();
    HashSet<String> furniture = new HashSet<>();
    HashSet<String> characters = new HashSet<>();
    HashSet<String> mergedSet = new HashSet<>();


    public GameEngine(HashMap<String, HashSet<String>> pathMap,
                      HashMap<Location, HashMap<String, HashSet<GameEntity>>> entitiesMap,
                      HashMap<String, HashSet<GameAction>> actions,
                      HashMap<String, Player> playerMap,
                      HashMap<String, HashSet<GameEntity>> bagMap) {
        this.pathMap = pathMap;
        this.entitiesMap = entitiesMap;
        this.actions = actions;
        this.playerMap = playerMap;
        this.bagMap = bagMap;
    }

    public String commandParser(String command){
        // get current player name & check the player exists or not.
        String currentPlayer = command.split(":")[0].trim();
        if(currentPlayer.equals("goto")||currentPlayer.equals("get")||currentPlayer.equals("drop")||
                currentPlayer.equals("look")||currentPlayer.equals("inv")||
                currentPlayer.equals("inventory")){
            return "[Warning]Invalid player name.";
        }
        Player player = playerChecker(currentPlayer);
        // Split the command.
        HashSet<String> wordSet = new HashSet<>(Arrays.asList(command.split(" ")));
        // Check the trigger && entity valid or not
        String trigger = triggerChecker(wordSet);
        entitiesSet(player);
        String entity = entityChecker(currentPlayer, trigger, wordSet);
        if(trigger.contains("Warning")||entity.contains("Warning")){
            return "[Warning]No valid trigger/entity or trigger/entity more than one.";
        } else if(trigger.contains("inv") || trigger.equals("look") ||
                trigger.equals("get")||trigger.equals("drop") || trigger.equals("goto")){
            return builtInCommand(trigger,entity,player);
        }else{// If is is not a built in command
            String gameAction = gameActionChecker(trigger,wordSet);
            if(gameAction.contains("Warning")){
                return "[Warning]No valid trigger/gameAction or trigger/gameAction more than one.";
            }
            return gameActionCommand(trigger,player);
        }
    }

    // Check if the player exists or not
    // If player does not exists, create the new object and put it into player map.
    public Player playerChecker(String playerName){
        Player nowPlayer = playerMap.get(playerName);
        if(nowPlayer == null){ // put thr new player into player map
            nowPlayer = new Player(playerName,"","cabin",entitiesMap,3);
            playerMap.put(playerName,nowPlayer);
            bagMap.put(playerName, new HashSet<>()); // create the bag
        }
        return nowPlayer;
    }
    // Reset the player state when the health is down to 0
    public void resetPlayerState(Player player){
        String name = player.getName();
        //getPlayerBag(name);
        if(getPlayerBag(name)!=null) {
            for (GameEntity entity : getPlayerBag(name)) {
                drop(player, entity.getName());
            }
        }
        player.setHealth(3);
        player.setCurrentLocation("cabin");
    }

    // Check if the trigger is valid or not(only 1 trigger is valid)
    public String triggerChecker(HashSet<String> words){
        String curTrigger = "";
        int triggerCount = 0;
        // Check if the command contains builtin commands
        for(String word: words){
            if(word.contains("inv")){ curTrigger = word; triggerCount++;
            }else if(word.equals("goto")){ curTrigger = word; triggerCount++;
            }else if(word.equals("look")){ curTrigger = word; triggerCount++;
            }else if(word.equals("get")){ curTrigger = word; triggerCount++;
            }else if(word.equals("drop")){ curTrigger = word; triggerCount++;}
        }
        // Check if the command is game action trigger
        if(triggerCount != 1 || curTrigger.contains("Warning")){
            curTrigger = actionTriggerChecker(words);
            if(curTrigger.contains("Warning")) {
                return "[Warning]Not a valid trigger.";
            }
        }
        return curTrigger;
    }

    // Check if the entity is valid or not(only 1 entity is valid)
    public String entityChecker(String playerName,String trigger, HashSet<String> entities){
        String curEntity = "";
        int entityCounter = 0;
        mergeSet();
        for(String entity: entities){
            switch (trigger) {
                case "goto" -> {
                    for (String location : locations) {
                        if (entity.equals(location)) {
                            curEntity = entity;
                            entityCounter++;
                        }
                    }
                }
                case "get" -> {
                    for (String artefact : artefacts) {
                        if (entity.equals(artefact)) {
                            curEntity = entity;
                            entityCounter++;
                        }
                    }
                }
                case "drop" -> {
                    for (GameEntity artefact : bagMap.get(playerName)) {
                        if (entity.equals(artefact.getName())) {
                            curEntity = entity;
                            entityCounter++;
                        }
                    }
                }
                default -> {
                    for (String mergeEntity : mergedSet) {
                        if (mergeEntity.equals(entity)) {
                            curEntity = entity;
                            entityCounter++;
                        }
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

    // Check for not built in command
    public String actionTriggerChecker(HashSet<String> words) {
        String curActionTrigger = "";
        int triggerCounter = 0;
        for (String trigger : actions.keySet()) {
            for(String word : words){
                if(word.equals(trigger)){
                    curActionTrigger = trigger;
                    triggerCounter++;
                }
            }
        }
        if(triggerCounter != 1){
            return "[Warning]Invalid action trigger";
        }
        return curActionTrigger;
    }

    // trigger -> action trigger gameActions -> subjects from now trigger
    public String gameActionChecker(String trigger, HashSet<String> wordSet){
        String gameAction = ""; //Check the game action is valid or not
        int actionCounter = 0;
        HashSet<GameAction> actionSet = actions.get(trigger);
        HashSet<String> subjects;
        for (GameAction action : actionSet) {
            subjects = action.getSubjects();
            for(String subject: subjects){
                for(String word : wordSet){
                    if (subject.equals(word)){
                        gameAction = word;
                        actionCounter++;
                    }
                }
            }
        }
        // 1. Check the trigger is valid or not 2.Check the action is valid or not
        if(actionCounter < 1){
            return "No valid game actions";
        }
        return gameAction;
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

    public void mergeSet(){
        mergedSet.addAll(locations);
        mergedSet.addAll(artefacts);
        mergedSet.addAll(furniture);
        mergedSet.addAll(characters);
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
        String playerName = player.getName();
        return "You are " + playerName + ". Your bag now has:" + bagMap.get(playerName);
    }

    // get picks up a specified artefact from the current location and adds it into player's inventory
    public String get(Player player, String entity){
        HashMap<String, HashSet<GameEntity>> nowLocation = getLocationEntities(player);
        HashSet<GameEntity> playerBag = getPlayerBag(player.getName());
        // only artefacts can be collected by the player
        HashSet<GameEntity> entitiesCanBeCollected = nowLocation.get("artefacts");
        for (GameEntity entityCanBeCollected : entitiesCanBeCollected) {
            if(entityCanBeCollected.getName().equals(entity)){
                // Remove the entity after collecting it from location
                nowLocation.get("artefacts").remove(entityCanBeCollected);
                // Add the collected entity into the player's bag
                playerBag.add(entityCanBeCollected);
                return "You get the [" + entity + "]";
            }
        }
        return "[Warning]You can't pick up this thing. / Item does not exist";
    }

    // Can get current location
    public HashMap<String, HashSet<GameEntity>> getLocationEntities(Player player){
        String currentLocation = player.getCurrentLocation();
        for(Location location: entitiesMap.keySet()){
            if(location.getName().equals(currentLocation)){
                return entitiesMap.get(location);
            }
        }
        return null;
    }
    // Can get storeroom location
    public HashMap<String, HashSet<GameEntity>> getStoreroomEntities(){
        for(Location location: entitiesMap.keySet()){
            if(location.getName().equals("storeroom")){
                return entitiesMap.get(location);

            }
        }
        return null;
    }
    // Can get current player bag
    public HashSet<GameEntity> getPlayerBag(String playerName){
        for(String player: bagMap.keySet()){
            if(player.equals(playerName)){
                return bagMap.get(playerName);
            }
        }
        return null;
    }

    // drop puts down an artefact from player's inventory and places it into the current location
    public String drop(Player player, String entity){
        HashMap<String, HashSet<GameEntity>> nowLocation = getLocationEntities(player);
        HashSet<GameEntity> locationArtefacts = nowLocation.computeIfAbsent("artefacts", k -> new HashSet<>());
        HashSet<GameEntity> playerBag = getPlayerBag(player.getName());
        for (GameEntity entityCanBeDropped : playerBag) {
            if(entityCanBeDropped.getName().equals(entity)){
                // Remove the entity after dropped it from player's bag
                playerBag.remove(entityCanBeDropped);
                // Add the collected entity into the now location
                locationArtefacts.add(entityCanBeDropped);
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
        // TODO: Add the produced for check the available path
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

    // For execute the player's valid game action
    public String gameActionCommand(String trigger,Player player){
        HashSet<GameAction> actionSet = actions.get(trigger);
        gameActionLoop:
        for (GameAction gameAction : actionSet) {
            for (String consumed : gameAction.getConsumed()) {
                System.out.println(consumed);
                if(consumedAction(consumed,player).contains("Warning")){
                    break gameActionLoop;
                }
            }
            for (String produced : gameAction.getProduced()) {
                System.out.println(produced);
                if(producedAction(produced,player).contains("Warning")){
                    break gameActionLoop;
                }
            }
            // Return the narration to client(player)
            return gameAction.getNarration();
        }
        System.out.println("In game action command Health is:" + player.getHealth());
        if(player.getHealth()==0){
            resetPlayerState(player);
            return "You died and lost all of your items, you must return to the start of the game.";
        }
        return "[Warning]Invalid or no meaning command.";
    }

    // Check the location & player bag has the entity or not
    public String consumedAction(String consumed,Player player){
        String playerName = player.getName();
        if(consumed.equals("health")){
            int health = player.getHealth();
            player.setHealth(health-1);
            if((health-1) == 0){
                return "[Warning]";
            }
            return "OK";
        }
        HashMap<String, HashSet<GameEntity>> locationEntities = getLocationEntities(player);
        HashMap<String, HashSet<GameEntity>> storeroomEntities = getStoreroomEntities();
        HashSet<GameEntity> artefactsSet = locationEntities.get("artefacts");
        HashSet<GameEntity> furnitureSet = locationEntities.get("furniture");
        if (getStoreroomEntities().get("furniture") == null) {
            furnitureSet = new HashSet<>();
            storeroomEntities.put("furniture", furnitureSet);
        }
        if (locationEntities.get("artefacts") == null) {
            artefactsSet = new HashSet<>();
            locationEntities.put("artefacts", artefactsSet);
        }
        HashSet<GameEntity> playerEntities = getPlayerBag(playerName);
        if(playerEntities!=null) {
            for (GameEntity curEntity : playerEntities) {
                if (curEntity.getName().equals(consumed)) {
                    playerEntities.remove(curEntity); // remove the entity from the current player's bag
                    storeroomEntities.get("artefacts").add(curEntity); // add the entity to the store room
                    return "[OK]";
                }
            }
        }
        if(artefactsSet!=null){
            for(GameEntity curEntity: artefactsSet){
                if(curEntity.getName().equals(consumed)){
                    locationEntities.get("artefacts").remove(curEntity); // remove the entity from the current location
                    storeroomEntities.get("artefacts").add(curEntity); // add the entity to the store room
                    return "[OK]";
                }
            }
        }
        if(furnitureSet!=null) {
            for (GameEntity curEntity : furnitureSet) {
                if (curEntity.getName().equals(consumed)) {
                    locationEntities.get("furniture").remove(curEntity); // remove the entity from the current location
                    storeroomEntities.get("furniture").add(curEntity); // add the entity to the store room
                    return "[OK]";
                }
            }
        }
        return "[Warning]This entity does not exist";
    }
    // Create something to the map.
    private String producedAction(String produced, Player player) {
        String playerName = player.getName();
        String curLocation = player.getCurrentLocation();
        if(produced.equals("health")){
            int health = player.getHealth();
            if(health<3) {
                player.setHealth(health + 1);
            }
            return "OK";
        }
        // If the entity is not location, then it must in the storeroom
        HashMap<String, HashSet<GameEntity>> storeroomEntities = getStoreroomEntities();
        // If not exist try to look if it is the location
        for(String producedLocation: locations){
            if(producedLocation.equals(produced)){
                // Create a new Path in here, and add it to the path map
                pathMap.get(curLocation).add(producedLocation);
                return "OK";
            }
        }
        return "This entity/location does not exist";
    }


}

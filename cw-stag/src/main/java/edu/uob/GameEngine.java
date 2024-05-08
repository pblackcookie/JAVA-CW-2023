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
    String bornLocation;
    HashSet<String> builtInCommand = new HashSet<>();
    HashSet<String> entitiesSet = new HashSet<>();


    public GameEngine(HashMap<String, HashSet<String>> pathMap,
                      HashMap<Location, HashMap<String, HashSet<GameEntity>>> entitiesMap,
                      HashMap<String, HashSet<GameAction>> actions,
                      HashMap<String, Player> playerMap,
                      HashMap<String, HashSet<GameEntity>> bagMap,
                      String bornLocation) {
        this.pathMap = pathMap;
        this.entitiesMap = entitiesMap;
        this.actions = actions;
        this.playerMap = playerMap;
        this.bagMap = bagMap;
        this.bornLocation = bornLocation;
    }

    public String commandParser(String command){
        // get current player name & check the player exists or not.
        String currentPlayer = command.split(":")[0].trim();
        builtInInit(); // Check if the player name contains the built in command
        for(String builtIn: builtInCommand){
            if(currentPlayer.equals(builtIn)){
                return "[Warning]Invalid player name.";
            }
        }
        Player player = playerChecker(currentPlayer);
        command = command.substring(currentPlayer.length()+1).trim();
        HashSet<String> wordSet = new HashSet<>(Arrays.asList(command.split(" ")));
        entitiesSet(player);
        mergeSet(); //player current location entities
        return commandChecker(wordSet,player);
    }

    /*----------------------Player related checker------------------------------------
    playerChecker: Check if the player exists or not(used for creating new player)
    Description: If player does not exists, create the new object and put it into player map.
    resetPlayerState: Reset the player state
    Description: If the player health is down to 0, reset the player states to the born location
                 and drop all entities in bag to the died location
    -------------------------------------------------------------------------------- */
    public Player playerChecker(String playerName){
        Player nowPlayer = playerMap.get(playerName);
        if(nowPlayer == null){ // put thr new player into player map
            nowPlayer = new Player(playerName,"",bornLocation,entitiesMap,3);
            playerMap.put(playerName,nowPlayer);
            bagMap.put(playerName, new HashSet<>()); // create the bag
        }
        return nowPlayer;
    }
    //
    public void resetPlayerState(Player player){
        String name = player.getName();
        HashSet<GameEntity> playerBag;
        playerBag = getPlayerBag(name);
        HashSet<GameEntity> tmpBag = playerBag;
        if(playerBag!=null) {
            for (GameEntity entity : tmpBag) {
                if(entity != null && entity.getName() != null) {
                drop(player, entity.getName());
                }
            }
        }
        player.setHealth(3);
        player.setCurrentLocation(bornLocation);
    }
    /*-------------------------Trigger and game action checker----------
    1. built in command -> execute the built in command
    2. game action -> execute the current game action
    ------------------------------------------------------------------- */
    public String commandChecker(HashSet<String> words,Player player){
        int actionCounter = 0;
        String builtInTrigger = null;
        String gameActionTrigger = null;
        String narration = null;
        for(String curWord: words){
            for (String builtIn: builtInCommand){
                if(builtIn.equals(curWord)){
                    builtInTrigger = curWord;
                    actionCounter++;
                }
            }
            if(actions.containsKey(curWord)){ // in here look if two game action equals
                for(GameAction curAction: actions.get(curWord)){
                    if(gameActionChecker(curAction, player, words)){
                        gameActionTrigger = curWord;
                        if(narration == null){
                            narration = curAction.getNarration();
                            actionCounter++;
                        }else if(!curAction.getNarration().equals(narration)){
                            actionCounter++;
                        }
                    }
                }
            }
        }
        if (actionCounter != 1){
            return "[Error]No or More than one action";
        }else if(gameActionTrigger!=null && builtInTrigger == null){
            String result = gameActionCommand(gameActionTrigger,player);
            if(result.contains("Warning")){
                return "[ERROR]No valid command in game action";
            }
            return result;
        }else if(builtInTrigger != null){
            String entity = entityChecker(player.getName(),builtInTrigger, words);
            if(entity.contains("Warning")){
                return "[ERROR]" + entity;
            }
            return builtInCommand(builtInTrigger,entity,player);
        }else {
            return "[ERROR]Unknown error";
        }
    }
    /*--------------Game Action valid or not checker------------
    ----------------------------------------------------------- */
    public boolean gameActionChecker(GameAction action,Player player,HashSet<String> command){
        String curLocation = player.getCurrentLocation();
        HashSet<String> subjects = action.getSubjects();
        boolean hasCommandSubjects = checkCommandSubjects(command, subjects);
        boolean hasNoSubjectEntities = checkNoSubjectEntities(command, subjects);
        boolean hasAllEntitiesForCheck = checkAllEntitiesForCheck(player, curLocation, subjects);
        return hasCommandSubjects && !hasNoSubjectEntities && hasAllEntitiesForCheck;
    }
    // Check if the current command has the needed subjects or not
    private boolean checkCommandSubjects(HashSet<String> command, HashSet<String> subjects) {
        for (String curWord : command) {
            if (subjects.contains(curWord)) {
                return true;
            }
        }
        return false;
    }
    // Check if the command has no relative entities
    private boolean checkNoSubjectEntities(HashSet<String> command, HashSet<String> subjects) {
        getAllEntities(); // All map entities
        HashSet<String> newMergeSet = new HashSet<>(entitiesSet);
        newMergeSet.removeAll(subjects);
        for (String curWord : command) {
            if (newMergeSet.contains(curWord)) {
                return true;
            }
        }
        return false;
    }
    // Check if current location satisfies all the needed subjects
    private boolean checkAllEntitiesForCheck(Player player, String curLocation, HashSet<String> subjects) {
        HashSet<String> entitiesForCheck = new HashSet<>();
        for (String key : getLocationEntities(player).keySet()) {
            for (GameEntity entity : getLocationEntities(player).get(key)) {
                entitiesForCheck.add(entity.getName());
            }
        }
        for (GameEntity entity : bagMap.get(player.getName())) {
            entitiesForCheck.add(entity.getName());
        }
        entitiesForCheck.add(curLocation);
        for (String subject : subjects) {
            if (!entitiesForCheck.contains(subject)) {
                return false;
            }
        }
        return true;
    }

    // Check if the entity is valid or not(only 1 entity is valid)
    public String entityChecker(String playerName,String trigger, HashSet<String> entities){
        String curEntity = "";
        int entityCounter = 0;
        for(String entity: entities){
            boolean isValid = switch (trigger) {
                case "goto" -> checkNeededEntity(entity, locations);
                case "get" -> checkNeededEntity(entity, artefacts);
                case "drop" -> checkNeededEntity(entity, getPlayerBagString(playerName));
                default -> checkNeededEntity(entity, mergedSet);
            };
            if(isValid){
                curEntity = entity;
                entityCounter++;
            }
        } // inv look health does not need the entity exist & get,drop and goto only need one entity exist
        if((trigger.contains("inv") || trigger.equals("look") || trigger.equals("health")) && entityCounter != 0){
            return "[Warning]Not a valid command.";
        }else if((trigger.equals("get")||trigger.equals("drop") || trigger.equals("goto")) && entityCounter !=1){
            return "[Warning]Not a valid entity.";
        }
        return curEntity;
    }
    private boolean checkNeededEntity(String entity,HashSet<String> entities){
        for (String curEntity : entities) {
            if (entity.equals(curEntity)) {
                return true;
            }
        }
        return false;
    }

    /*---------------Some initialization methods--------------
    builtInInit: Put all the built in command into one HashSet.
    entitiesSet: Initialize all the entities name(Type: String)(No description, only name)
    mergeSet: Put all the entities(including location) into the one Hashset(But only from the current location)
     ---------------------------------------------------------*/
    public void builtInInit(){
        builtInCommand.add("inv");
        builtInCommand.add("goto");
        builtInCommand.add("drop");
        builtInCommand.add("get");
        builtInCommand.add("look");
        builtInCommand.add("health");
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
    // Get all the entities from entities map(type:string)
    public void getAllEntities(){
        Set<GameEntity> allEntities = new HashSet<>();
        Set<Location> allLocations = entitiesMap.keySet();
        for (HashMap<String, HashSet<GameEntity>> locationMap : entitiesMap.values()) {
            for (HashSet<GameEntity> entitySet : locationMap.values()) {
                allEntities.addAll(entitySet);
            }
        }
       for(GameEntity curEntity: allEntities){
           entitiesSet.add(curEntity.getName());
       }
       for (Location curLocation: allLocations){
           entitiesSet.add(curLocation.getName());
       }
        for (HashSet<GameEntity> entitySet : bagMap.values()) {
            for (GameEntity entity : entitySet) {
                entitiesSet.add(entity.getName());
            }
        }
    }

    /*--------------------Get some entities--------------------------
    getLocationEntities: get all the entities in current location
    getStoreroomEntities: get all the entities in the storeroom
    getPlayerBag: get all the entities from the current player's bag
     ------------------------------------------------------------*/
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
    // Can get the current player bag
    public HashSet<String> getPlayerBagString(String playerName){
        HashSet<String> bagEntity = new HashSet<>();
        for(String player: bagMap.keySet()){
            if(player.equals(playerName)){
                for(GameEntity entity: bagMap.get(playerName)){
                    bagEntity.add(entity.getName());
                }
            }
        }
        return bagEntity;
    }
    /*------------------------Built in command and execute-----------------
    If it is a built in command they determine which command should be execute
    builtInCommand: The switcher to determine which command should be execute
    -------------------No entity function------------------
    inv: look the player's bag
    look: look the current location(include the location, path, entities and other players)
    health: look the current player health
    -------------------One entity function-------------------
    get: get some entity from the current location to the player's bag
    drop: drop some entity from the player's bag to the current location
    goto_: go to one reachable location
    ---------------------------------------------------------------------- */
    public String builtInCommand(String trigger, String entity, Player player){
        //look the trigger first
        if (trigger.contains("inv")) { return inv(player);
        }else if (trigger.equals("look")) { return look(player);
        }else if(trigger.equals("goto")){ return goto_(player,entity);
        }else if(trigger.equals("get")){ return get(player,entity);
        }else if(trigger.equals("drop")) {return drop(player, entity);
        }else if(trigger.equals("health")) return health(player);
        return "[Warning]Can not found the current command.";
    }

    // inventory (or inv for short) lists all of the artefacts currently being carried by the player
    public String inv(Player player){
        String playerName = player.getName();
        return "You are " + playerName + ". Your bag now has:" + bagMap.get(playerName);
    }

    public String health(Player player){
        return "Your health now is: " + player.getHealth();
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
        String curLocation = player.getCurrentLocation();
        Set<String> locations = pathMap.get(curLocation);
        if(locations!=null && locations.contains(destination)){
            player.setCurrentLocation(destination);
            return "You are now in the: " + destination;
        }
        return "[Warning]You entered a non-existent location. -Maybe a typo?";

    }
    // look 1.prints names and descriptions of entities in the current location and 2.lists paths to other locations
    public String look(Player player){
        String currentLocation = player.getCurrentLocation();
        String locationDescription = null;
        StringBuilder EntitiesDetails = new StringBuilder();
        StringBuilder pathDetails = new StringBuilder();
        StringBuilder totalPlayers = new StringBuilder();
        HashMap<String, HashSet<GameEntity>> currentEntities = getLocationEntities(player);
        for (Location curLocation: entitiesMap.keySet()){
            if(curLocation.getName().equals(currentLocation)){
                locationDescription = curLocation.getDescription();
            }
        }
        for(String entityType: currentEntities.keySet()){ // // Get the current entities
            for(GameEntity entity: currentEntities.get(entityType)){
                EntitiesDetails.append(entity.getName()).append(":").append(entity.getDescription()).append(". ");
            }
        }
        for(String path: pathMap.keySet()){ // Get the current path map
            if (path.equals(currentLocation)){
                pathDetails.append(pathMap.get(path)).append(" ");
            }
        }
        for (String curPlayer: playerMap.keySet()){ // Get the other players
            Player loaclPlayer = playerMap.get(curPlayer);
            if(loaclPlayer.getCurrentLocation().equals(currentLocation)){
                totalPlayers.append(loaclPlayer.getDescription()).append(" ");
            }
        }
        String locationDetails = currentLocation + ": " + locationDescription;
        return locationDetails + "\n" + EntitiesDetails + "\nYou can go to the: " + pathDetails + totalPlayers;
    }

    // For execute the player's valid game action
    public String gameActionCommand(String trigger,Player player){
        HashSet<GameAction> actionSet = actions.get(trigger);
        gameActionLoop:
        for (GameAction gameAction : actionSet) {
            for (String consumed : gameAction.getConsumed()) {
                if(consumedAction(consumed,player).contains("Warning")){
                    break gameActionLoop;
                }
            }
            for (String produced : gameAction.getProduced()) {
                if(producedAction(produced,player).contains("Warning")){
                    break gameActionLoop;
                }
            }
            // Return the narration to client(player)
            return gameAction.getNarration();
        }
        if(player.getHealth()==0){
            resetPlayerState(player);
            return  "You attack the elf, but he fights back and you lose some health.\n" +
                    "You died and lost all of your items, you must return to the start of the game.";
        }
        return "[Warning]Invalid or no meaning command.";
    }

    // Check the location & player bag has the entity or not
    public String consumedAction(String consumed,Player player){
        String playerName = player.getName();
        if(consumed.equals("health")){  //Check if the player is still alive
            return handleHealthConsumption(player);
        }
        HashMap<String, HashSet<GameEntity>> locationEntities = getLocationEntities(player);
        HashMap<String, HashSet<GameEntity>> storeroomEntities = getStoreroomEntities();
        Arrays.asList("furniture", "artefacts").forEach(category -> {
            locationEntities.computeIfAbsent(category, k -> new HashSet<>());
            storeroomEntities.computeIfAbsent(category, k -> new HashSet<>());
        });
        HashSet<GameEntity> playerEntities = getPlayerBag(playerName);
        if (moveEntity(consumed, playerEntities, storeroomEntities.get("artefacts"))) {
            return "[OK]";
        }
        if (moveEntity(consumed, locationEntities.get("artefacts"), storeroomEntities.get("artefacts"))) {
            return "[OK]";
        }
        if (moveEntity(consumed, locationEntities.get("furniture"),
                storeroomEntities.get("furniture"))) {
            return "[OK]";
        }
        return "[Warning]This entity does not exist";
    }
    // Check the player's health and handle it
    private String handleHealthConsumption(Player player) {
        int health = player.getHealth();
        player.setHealth(health - 1);
        return (health - 1 == 0) ? "[Warning]" : "OK";
    }
    // If the command is valid then consumed the entity.
    private boolean moveEntity(String entityName, HashSet<GameEntity> sourceContainer,
                               HashSet<GameEntity> targetContainer) {
        for (GameEntity curEntity : sourceContainer) {
            if (curEntity.getName().equals(entityName)) {
                sourceContainer.remove(curEntity);
                targetContainer.add(curEntity);
                return true;
            }
        }
        return false;
    }

    // Create something into the map.
    public String producedAction(String produced, Player player) {
        if(produced.equals("health")){
            return increaseHealth(player);
        }
        String result = moveEntityFromStoreroomToLocation(produced, player);
        if (!result.equals("[Warning] Entity does not exist in the storeroom.")) {
            return result;
        }
        return createPath(produced, player);
    }
    // Method for add player health
    private String increaseHealth(Player player) {
        int health = player.getHealth();
        if (health < 3) {
            player.setHealth(health + 1);
        }
        return "OK";
    }
    // If the entity is not location, then it must in the storeroom(with different type)
    private String moveEntityFromStoreroomToLocation(String produced, Player player) {
        HashMap<String, HashSet<GameEntity>> storeroomEntities = getStoreroomEntities();
        HashMap<String, HashSet<GameEntity>> locationEntities = getLocationEntities(player);
        for (String key : storeroomEntities.keySet()) {
            for (GameEntity entity : storeroomEntities.get(key)) {
                if (entity.getName().equals(produced)) {
                    storeroomEntities.get(key).remove(entity);
                    locationEntities.computeIfAbsent(key, k -> new HashSet<>()).add(entity);
                    return "OK";
                }
            }
        }
        return "[Warning] Entity does not exist in the storeroom.";
    }
    // If not exist try to look if it is the location
    private String createPath(String produced, Player player) {
        String curLocation = player.getCurrentLocation();
        for (String producedLocation : locations) {
            if (producedLocation.equals(produced)) {
                pathMap.get(curLocation).add(producedLocation);
                return "OK";
            }
        }
        return "[Warning] Location does not exist.";
    }

}

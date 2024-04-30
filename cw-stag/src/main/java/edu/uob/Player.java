package edu.uob;

import java.util.HashMap;
import java.util.HashSet;

public class Player extends Characters{
    String currentLocation;
    HashMap<Location, HashMap<String, HashSet<GameEntity>>> entitiesMap;
    public Player(String name, String description, String currentLocation,
                  HashMap<Location, HashMap<String, HashSet<GameEntity>>> entitiesMap) {
        super(name, description);
        this.currentLocation = currentLocation;
        this.entitiesMap = entitiesMap;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }

    public String getDescription(){
        return "\nYou see the player: [" + getName() + "].";
    }
}

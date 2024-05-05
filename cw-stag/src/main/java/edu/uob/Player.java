package edu.uob;

import java.util.HashMap;
import java.util.HashSet;

public class Player extends Characters{
    String currentLocation;
    int health;
    HashMap<Location, HashMap<String, HashSet<GameEntity>>> entitiesMap;
    public Player(String name, String description, String currentLocation,
                  HashMap<Location, HashMap<String, HashSet<GameEntity>>> entitiesMap,
                  int health) {
        super(name, description);
        this.currentLocation = currentLocation;
        this.entitiesMap = entitiesMap;
        this.health = health;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public String getDescription(){
        return "\nYou see the player: [" + getName() + "].";
    }
}

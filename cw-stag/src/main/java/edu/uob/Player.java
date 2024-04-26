package edu.uob;

public class Player extends Characters{
    String currentLocation;
    public Player(String name, String description, String currentLocation) {
        super(name, description);
        this.currentLocation = currentLocation;
    }
}

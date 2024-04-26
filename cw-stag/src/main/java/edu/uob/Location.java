package edu.uob;

public class Location extends GameEntity{
     public Location(String name, String description) {
         super(name, description);
     }

    @Override
    public String toString() {
        return "Location: " + getName() + ", Description: " + getDescription();
    }
 }

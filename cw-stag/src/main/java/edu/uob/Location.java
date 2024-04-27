package edu.uob;

public class Location extends GameEntity{
     public Location(String name, String description) {
         super(name, description);
     }
    @Override
    public String toString() {
        return "You are now in the " + getName() + ". This is " + getDescription();
    }


 }

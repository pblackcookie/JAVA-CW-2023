package edu.uob;

public class Furniture extends GameEntity{
    public Furniture(String name, String description) {
        super(name, description);
    }

    @Override
    public String toString() {
        return "This is a " + getName() + ": " + getDescription() + " ";
    }
}

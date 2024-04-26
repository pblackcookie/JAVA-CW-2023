package edu.uob;

import java.util.HashMap;
import java.util.HashSet;

public class GameAction
{
    HashSet<String> triggers;
    HashSet<String> subjects;
    HashSet<String> consumed;
    HashSet<String> produced;
    String narration;

    public GameAction(HashSet<String> triggers, HashSet<String> subjects, HashSet<String> consumed, HashSet<String> produced, String narration) {
        this.triggers = triggers;
        this.subjects = subjects;
        this.consumed = consumed;
        this.produced = produced;
        this.narration = narration;
    }

    @Override
    public String toString() {
        return "GameAction{" +
                "triggers=" + triggers +
                ", subjects=" + subjects +
                ", consumed=" + consumed +
                ", produced=" + produced +
                ", narration='" + narration + '\'' +
                '}';
    }

    public HashSet<String> getTriggers() {
        return triggers;
    }

    public HashSet<String> getSubjects() {
        return subjects;
    }

    public HashSet<String> getConsumed() {
        return consumed;
    }

    public HashSet<String> getProduced() {
        return produced;
    }

    public String getNarration() {
        return narration;
    }

}

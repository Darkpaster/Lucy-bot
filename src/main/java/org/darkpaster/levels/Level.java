package org.darkpaster.levels;

public class Level {
    public location current_level;
    public enum location {
        START_DUNGEON, IMPERIAL_FOREST, IMPERIAL_PALACE, TAVERN
    }
    public Level(){
        current_level = location.START_DUNGEON;

    }


}

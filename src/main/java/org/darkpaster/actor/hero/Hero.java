package org.darkpaster.actor.hero;

import org.darkpaster.actor.Actor;
import org.darkpaster.levels.Level;
import org.darkpaster.levels.SpawnDungeon;

public class Hero extends Actor {

    public int skipTurn = 0;

    //public Level currentLevel;
    public Hero(){
        //currentLevel = new SpawnDungeon();
        name = "hero";
        x = 0;
        y = 0;
        z = -200;
        HT = HP = 20;
    }
}

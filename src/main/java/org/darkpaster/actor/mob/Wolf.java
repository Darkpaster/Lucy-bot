package org.darkpaster.actor.mob;

import org.darkpaster.GameGUI;
import org.darkpaster.utils.Random;

public class Wolf extends Mob {
    public Wolf(){
        rarity = 1;
        x = Random.Int(GameGUI.world[8].length() - 1);
        y = Random.Int(GameGUI.world.length - 1);
        name = "Волк";
        HT = HP = 10;
        minDmg = 1;
        maxDmg = 3;
        DR = 0;
        noticeRange = 10;
        dropExp = 2;
    }
}

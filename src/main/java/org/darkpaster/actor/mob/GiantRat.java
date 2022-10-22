package org.darkpaster.actor.mob;

import org.darkpaster.Bot;
import org.darkpaster.Game;

public class GiantRat extends Mob{
    public GiantRat(){
        rarity = 1;
        name = "Giant rat";
        HT = HP = 10;
        minDmg = 1;
        maxDmg = 3;
        DR = 0;
    }
}

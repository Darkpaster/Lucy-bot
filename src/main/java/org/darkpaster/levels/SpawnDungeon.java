package org.darkpaster.levels;

import org.darkpaster.actor.hero.Hero;

public class SpawnDungeon extends Level{

    public SpawnDungeon(){
        x = 0;
        y = 0;
        z = -200;
        area = 200;
        title = "Unknown dungeon.";
    }


    @Override
    protected String description() {
        return "You're at depth of " + -z + " meters.";
    }
}

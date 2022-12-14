package org.darkpaster.levels;

import org.darkpaster.Bot;
import org.darkpaster.actor.hero.Hero;
import org.darkpaster.actor.mob.GiantRat;

public class SpawnDungeon extends Level{

    public SpawnDungeon(){
        image = Bot.blackPath;
        x = 0;
        y = 0;
        z = -200;
        area = 200;
        lighting = 3;
        depth = -200;
        title = "Unknown dungeon.";
        dwellingEnemies.add(new GiantRat());
    }


    @Override
    protected String description() {
        return super.description();
    }

    @Override
    protected void spawnSign() {
        Bot.sendA("You hear someone's steps.");
    }
}

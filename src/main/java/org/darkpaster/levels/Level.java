package org.darkpaster.levels;

import org.darkpaster.Bot;
import org.darkpaster.actor.hero.Hero;
import org.darkpaster.actor.mob.Mob;
import org.darkpaster.utils.Random;

import java.io.File;
import java.util.ArrayList;

public class Level {
    //protected Level current_level;

    protected String title;
    protected int x;
    protected int y;
    protected int z;
    protected int area;

    protected int depth;

    public boolean isDungeon = false;

    protected int lighting;

    protected File image;

    public int getLighting(){return lighting;}

    public boolean combatMode = false;

    public ArrayList<Mob> enemies = new ArrayList<>();
    public ArrayList<Mob> dwellingEnemies = new ArrayList<>();



    public int getX(){return x;}
    public int getY(){return y;}
    public int getZ(){return z;}
    public int getArea(){return area;}
    public int getDepth(){return depth;}
    public String getTitle(){return title;}

    public enum location {
        START_DUNGEON, IMPERIAL_FOREST, IMPERIAL_PALACE, TAVERN
    }
    public Level(){
    }



    public void levelEvent(Hero hero){
        if(enemies.get(0) == null || enemies.get(0).getName().equals("dead")){
            enemies.clear();
            combatMode = false;
        }
        if(Random.Int(100) > 90 && !combatMode){
            combatMode = true;
            chanceToMeet();
        }
    }

    protected void chanceToMeet(){
        float random = Random.Float();
        for(Mob mob: dwellingEnemies){
            if(mob.getRarity() < random){
                enemies.add(mob);
                spawnSign();
            }
        }

    }

    protected void spawnSign(){

    }

    public void levelInit(){
        Bot.sendAttach("Now you're in the **" + title + "**.\n" + description(), image);
    }

    public String timeOfDay(){
        int hours = Bot.game.realTime / 60;
        int minutes = Bot.game.realTime % 60;
        if(isDungeon){
            return "You can't understand a time of day right now.";
        }else{
            if(hours > 12){
                hours -= 12;
                return "Now is **" + hours + ":" + minutes + " pm**.";
            }else{
                return "Now is **" + hours + ":" + minutes + " am**.";
            }
        }
    }

    protected String description(){
        if(lighting < 4){
            return "Because of the darkness almost nothing can be seen.";
        }else{
            return "";
        }
    }





}

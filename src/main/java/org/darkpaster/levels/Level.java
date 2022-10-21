package org.darkpaster.levels;

import org.darkpaster.Bot;
import org.darkpaster.actor.hero.Hero;

public class Level {
    //protected Level current_level;

    protected String title;
    protected int x;
    protected int y;
    protected int z;
    protected int area;

    public int getX(){return x;}
    public int getY(){return y;}
    public int getZ(){return z;}
    public int getArea(){return area;}
    public String getTitle(){return title;}

    public enum location {
        START_DUNGEON, IMPERIAL_FOREST, IMPERIAL_PALACE, TAVERN
    }
    public Level(){
        levelInit();
    }


    public Level getCurrentLevel(Hero hero){
        if(levelRadius(hero.getX(), hero.getY(), hero.getZ(), x, y, z, area) ){
            //return location.START_DUNGEON;
            return this;
        }else{
            return null;
            //return null;
        }
    }

    private boolean levelRadius(int x, int y, int z, int x2, int y2, int z2, int area){
        int distance = (int) Math.sqrt(((x - x2) * (x - x2)) + ((y - y2) * (y - y2)) + ((z - z2) * (z - z2)));

        if(distance < area){
            return true;
        }else{
            return false;
        }
    }


    public void levelEvent(Hero hero){

    }

    private void levelInit(){
        Bot.send("Now you're in the **" + title + "**.\n" + description());
    }

    protected String description(){
        return "Description.";
    }





}

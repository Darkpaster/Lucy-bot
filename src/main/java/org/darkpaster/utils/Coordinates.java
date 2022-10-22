package org.darkpaster.utils;

import org.darkpaster.actor.Actor;
import org.darkpaster.actor.hero.Hero;
import org.darkpaster.actor.mob.Mob;
import org.darkpaster.levels.Level;

public class Coordinates {

    public static boolean levelRadiusXYZ(Hero hero, Level lvl){
        int distance = (int) Math.sqrt(Math.pow(hero.getX() - lvl.getX(), 2) + Math.pow(hero.getY() - lvl.getY(), 2) + Math.pow(hero.getZ() - lvl.getZ(), 2));

        if(distance <= lvl.getArea()){
            return true;
        }else{
            return false;
        }
    }

    public static boolean levelRadiusXY(Hero hero, Level lvl){
        int distance = (int) Math.sqrt(Math.pow(hero.getX() - lvl.getX(), 2) + Math.pow(hero.getY() - lvl.getY(), 2));

        if(distance <= lvl.getArea()){
            return true;
        }else{
            return false;
        }
    }

    public static boolean levelRadiusZ(Hero hero, Level lvl){
        int distance = (int) Math.sqrt(Math.pow(hero.getZ() - lvl.getZ(), 2));

        if(distance <= lvl.getDepth()){
            return true;
        }else{
            return false;
        }
    }

    public static int pointDistanceXYZ(Actor hero, Actor mob){
        int distance = (int) Math.sqrt(Math.pow(hero.getX() - mob.getX(), 2) + Math.pow(hero.getY() - mob.getY(), 2) + Math.pow(hero.getZ() - mob.getZ(), 2));

        return distance;
    }

    public static int pointDistanceXY(Actor hero, Actor mob){

        int distance = (int) Math.sqrt(Math.pow(hero.getX() - mob.getX(), 2) + Math.pow(hero.getY() - mob.getY(), 2));

        return distance;
    }

    public static int pointDistanceX(Actor hero, Actor mob){

        int distance = (int) Math.sqrt(Math.pow(hero.getX() - mob.getX(), 2));

        return distance;
    }

    public static int pointDistanceY(Hero hero, Mob mob){

        int distance = (int) Math.sqrt(Math.pow(hero.getY() - mob.getY(), 2));

        return distance;
    }


}

package org.darkpaster.actor.hero;

import org.darkpaster.Bot;
import org.darkpaster.actor.Actor;
import org.darkpaster.levels.Level;
import org.darkpaster.levels.SpawnDungeon;
import org.darkpaster.utils.Coordinates;

public class Hero extends Actor {

    public int skipTurn = 0;

    private int visibilityRange = 0;

    public Level currentLevel;
    public Hero(){
        //currentLevel = new SpawnDungeon();
        name = "hero";
        x = 0;
        y = 0;
        z = -200;
        HT = HP = 20;
        minDmg = 2;
        maxDmg = 4;
        DR = 0;
    }

    public int getVisibilityRange(){return visibilityRange + currentLevel.getLighting();}

    public boolean move(String direction){

        boolean z = false;

        int x2 = x;
        int y2 = y;
        switch (direction){
            case "right":
                x += speed;
                z = true;
                break;
            case "left":
                x -= speed;
                z = true;
                break;
            case "back":
                y += speed;
                z = true;
                break;
            case "forward":
                y -= speed;
                z = true;
        }

        if(currentLevel.isDungeon){
            if(!Coordinates.levelRadiusXY(this, currentLevel) && Coordinates.levelRadiusZ(this, currentLevel)){
                x = x2;
                y = y2;
                Bot.send("**" + this.name + "** crashed into the wall. No further passage.");
            }


            if(!Coordinates.levelRadiusZ(this, currentLevel)){
                Bot.send("**" + this.name + "** left **" + currentLevel.getTitle() + "**.");
                currentLevel = Bot.game.getCurrentLevel(this);
                currentLevel.levelInit();
            }

        }else{
            if(!Coordinates.levelRadiusXY(this, currentLevel)){
                Bot.send("**" + this.name + "** left **" + currentLevel.getTitle() + "**.");
                currentLevel = Bot.game.getCurrentLevel(this);
                currentLevel.levelInit();
            }
        }



        return z;
    }



    public boolean move(String direction, int num){
        if(num <= 1){
            return false;
        }

        for (int i = 1; i < num; i++) {
            switch (direction){
                case "right":
                    x += speed;
                case "left":
                    x -= speed;
                case "back":
                    y += speed;
                case "forward":
                    y -= speed;
            }
        }

        return true;
    }
}

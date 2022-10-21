package org.darkpaster.actor;

import org.darkpaster.Game;

public class Actor {
protected int x;
protected int y;

    protected int z;
protected String name;
protected float speed;
protected int DR;
protected int HP;
protected int HT;
protected int MP;
protected int MT;
protected float critChance;
protected float critDmg;
protected float dodgeChance;
protected float hitChance;

public Actor(){
    name = "actor";
    speed = 1;
    DR = 0;
    critChance = 0;
    critDmg = 2;
    dodgeChance = 0;
    hitChance = 1;
}

public float getSpeed(){return speed;}
    public String getName(){return name;}

    public void setName(String name){this.name = name;}
    public float getDmg(){return speed;}

    public int getX(){return x;}
    public int getY(){return y;}
    public int getZ(){return z;}


public boolean move(String direction){

        switch (direction){
            case "right":
                x += speed;
                return true;
            case "left":
                x -= speed;
                return true;
            case "back":
                y += speed;
                return true;
            case "forward":
                y -= speed;
                return true;
        }

    return false;
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

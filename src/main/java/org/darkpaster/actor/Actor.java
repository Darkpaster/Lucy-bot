package org.darkpaster.actor;

import org.darkpaster.Game;

public class Actor {
protected int x;
protected int y;
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
}

package org.darkpaster.actor;

import org.darkpaster.Bot;
import org.darkpaster.Game;
import org.darkpaster.actor.hero.Hero;
import org.darkpaster.actor.mob.Mob;
import org.darkpaster.utils.Coordinates;
import org.darkpaster.utils.Random;

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

protected int attackRange = 1;

protected int minDmg;
protected int maxDmg;
protected float critChance;
protected float critDmg;
protected float dodgeChance;
protected float hitChance;
    protected boolean noticed;
protected Actor target;

public Actor(){
    name = "actor";
    speed = 1;
    DR = 0;
    critChance = 0;
    critDmg = 2;
    dodgeChance = 0;
    hitChance = 1;
}

public void attack(Actor enemy){
    if(canAttack(enemy)){
        int dmg = dmgRoll();
        enemy.HP -= dmg;
        Bot.send("**" + name + "** hits **" + enemy.name + "** and deals **" + dmg + "** damage.\n" + name + " HP: " + HP + "\n" + enemy.name + " HP: " + enemy.HP);
        if(enemy.HP <= 0){

            Bot.send("**" + enemy.name + "** dies.");
            enemy.name = "dead";
            if(enemy instanceof Mob){
                Bot.game.getHero().currentLevel.enemies.remove(enemy);
            }
        }
        if(this instanceof Hero){
            Bot.game.spended = 1;
        }
    }else{
        if(this instanceof Hero){
            Bot.sendA("You're too far from target.");
        }else{
            System.out.println("mob can't attack");
        }
    }
}

protected boolean canAttack(Actor enemy){
    return Coordinates.pointDistanceXYZ(this, enemy) <= attackRange;
}

protected int dmgRoll(){
    return Random.NormalIntRange(minDmg, maxDmg);
}

public float getSpeed(){return speed;}
    public String getName(){return name;}

    public void setName(String name){this.name = name;}
    public float getDmg(){return speed;}

    public int getX(){return x;}
    public int getY(){return y;}
    public int getZ(){return z;}





}

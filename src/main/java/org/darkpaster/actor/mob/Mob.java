package org.darkpaster.actor.mob;

import org.darkpaster.Bot;
import org.darkpaster.actor.Actor;
import org.darkpaster.actor.hero.Hero;
import org.darkpaster.utils.Coordinates;
import org.darkpaster.utils.Random;

public class Mob extends Actor {
    protected float rarity;


    protected int noticeRange;

    public float getRarity(){return rarity;}
    public Mob(){
        x = calc(Bot.game.getHero().getX(), Bot.game.getHero().getVisibilityRange());
        y = calc(Bot.game.getHero().getY(), Bot.game.getHero().getVisibilityRange());
        z = Bot.game.getHero().getZ();
    }

    protected void checkDistance(Hero[] heroes){
        boolean z = false;
        for(Hero hero: heroes){
            if(noticeRange >= Coordinates.pointDistanceXY(hero, this)){
                noticed = true;
                z = true;
                if(target == null){
                    target = hero;
                    Bot.sendA(this.name + " noticed you!");
                }

            }
        }
        if(!z){
            if(noticed){
                noticed = false;
                target = null;
                Bot.sendA(this.name + " no longer chasing you.");
            }

        }

    }



    public boolean delete(Hero hero){
        if(Coordinates.pointDistanceXY(hero, this) > hero.getVisibilityRange() + 5){
            return true;
        }
        return false;
    }

    private int calc(int i, int range){
        if(Random.Int(100) > 50){
            i += range;
        }else{
            i -= range;
        }
        return i;
    }

    public void move() {
        switch (Random.Int(3)){
            case 0:
                x++;
                break;
            case 1:
                x--;
                break;
            case 2:
                y++;
                break;
            case 3:
                y--;
        }

        if(Bot.game.getHero().getVisibilityRange() >= Coordinates.pointDistanceXY(Bot.game.getHero(), this)){
            Bot.send(this.name + " moved.");
        }
    }

    public void chase(Hero hero) {
        if(hero.getX() > x){
            x++;
        }else if(hero.getX() < x){
            x--;
        }else if(hero.getY() > y){
            y++;
        }else if(hero.getY() < y){
            y--;
        }

        Bot.sendA(this.name + " is chasing you.");
    }


    public void act(Hero[] heroes){
        checkDistance(heroes);
        if(noticed){
            if(canAttack(target)){
                attack(target);
            }else{
                chase((Hero) target);
            }
        }else{
            move();
        }



    }
}

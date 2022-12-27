package org.darkpaster.actor.mob;

import org.darkpaster.Bot;
import org.darkpaster.GameGUI;
import org.darkpaster.actor.Actor;
import org.darkpaster.actor.hero.Hero;
import org.darkpaster.utils.Coordinates;
import org.darkpaster.utils.Random;

import java.util.ArrayList;

public class Mob extends Actor {
    protected float rarity;


    protected int noticeRange;


    public boolean isChasing = false;

    public int dropExp;

    public float getRarity(){return rarity;}
    public Mob(){

        }

        public void initPos(){
            x = calc(Bot.game.getHero().getX(), Bot.game.getHero().getVisibilityRange());
            y = calc(Bot.game.getHero().getY(), Bot.game.getHero().getVisibilityRange());
            z = Bot.game.getHero().getZ();
        }

    protected void checkDistance(ArrayList<Hero> heroes){
        boolean z = false;
        for(Hero hero: heroes){
            if(noticeRange >= Coordinates.pointDistanceXY(hero, this)){
                noticed = true;
                if(target == null){
                    target = hero;
                    Bot.sendA("**" + this.name + "** noticed you!");
                }

            }

        }
        if(!z){
            if(noticed){
                noticed = false;
                target = null;
                Bot.sendA("**" + this.name + "** no longer chasing you.");
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
        switch (Random.Int(7)){
            case 0:
                x += 2;
                break;
            case 1:
                x -= 2;
                break;
            case 2:
                y++;
                break;
            case 3:
                y--;
        }

        if(x > GameGUI.world[9].length() - 2){
            x -= 2;
        }else if(x < 0){
            x += 2;
        }else if(y > GameGUI.world.length - 1){
            y = GameGUI.world.length - 1;
        }else if(y < 0){
            y = 0;
        }

        if(Coordinates.pointDistanceX(GameGUI.hero, this) <= 6 && Coordinates.pointDistanceY(GameGUI.hero, this) <= 3){
            isChasing = true;
            GameGUI.log.append(GameGUI.buildString(GameGUI.NOTICE, this.name));
        }

//        if(Bot.game.getHero().getVisibilityRange() >= Coordinates.pointDistanceXY(Bot.game.getHero(), this)){
//            Bot.send("**" + this.name + "** moved.");
//        }
    }

    public void chase(Hero hero) {
        if(hero.getX() > x){
            x += 2;
        }else if(hero.getX() < x){
            x -= 2;
        }else if(hero.getY() > y){
            y++;
        }else if(hero.getY() < y){
            y--;
        }

        //Bot.send(Bot.game.getUser(hero).getAsMention() + "**" + this.name + "** is chasing you.");
    }


    public void act(ArrayList<Hero> heroes){
//        checkDistance(heroes);
//        if(noticed){
//            if(canAttack(target)){
//                attack(target);
//            }else{
//                chase((Hero) target);
//            }
//        }else{
//            move();
//        }



    }
}

package org.darkpaster;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.darkpaster.actor.hero.Hero;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.Channel;
import java.util.ArrayList;
import java.util.Objects;

public class Game extends Bot{
    public float time = 0.0f;
    public int turns = 0;
    public float excess = 0;

    public boolean onlineMode = false;
    private User currentTurn;

    public Hero hero;
    public Hero hero2;
    public Hero hero3;
    public Hero hero4;
    public Hero hero5;

    public Game(){
        hero = new Hero();
        currentTurn = user;
    }

//    @Override
//    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
//        super.onMessageReceived(event);
//
//        message = event.getMessage();
//        msg = event.getMessage().getContentDisplay();
//        realMsg = event.getMessage().getContentRaw();
//        chan = event.getChannel();
//        user = message.getAuthor();
//    }

    protected void saveGame(){
        if(hero != null){
            jsObj.put("hero", hero);
        }
        if(hero2 != null){
            jsObj.put("hero2", hero2);
        }
        if(hero3 != null){
            jsObj.put("hero3", hero3);
        }
        if(hero4 != null){
            jsObj.put("hero4", hero4);
        }
        if(hero5 != null){
            jsObj.put("hero5", hero5);
        }
        jsObj.put("currentTurn", currentTurn);

        try {
            FileWriter writer = new FileWriter(password + ".json");
            writer.write(jsObj.toJSONString());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    protected void loadGame(){
        hero = (Hero) jsObj.get("hero");
        if(jsObj.containsKey("hero2")){
            hero2 = (Hero) jsObj.get("hero2");
        }
        if(jsObj.containsKey("hero3")){
            hero3 = (Hero) jsObj.get("hero3");
        }
        if(jsObj.containsKey("hero4")){
            hero4 = (Hero) jsObj.get("hero4");
        }
        if(jsObj.containsKey("hero5")){
            hero5 = (Hero) jsObj.get("hero5");
        }
    }

    protected void mainGameLoop(User user, String msg, String realMsg, Message message, MessageChannel chan){
        this.user = user;
        this.msg = msg;
        this.realMsg = realMsg;
        this.message = message;
        this.chan = chan;


        if(currentTurn != user){
            send("Now is not your turn.");
            return;
        }

        super.send(msg);
        if(super.msg.startsWith("!move")){
            if(Objects.requireNonNull(getHero()).move(cutString(msg))){
                send("You walked 1 meter.");
                spend(1);
            }
        }
        //send("test");

//        System.out.println(msg);
//        System.out.println(realMsg);
//        System.out.println(message);

    }

    private Hero getHero(){
        if(currentTurn == players.get(0)){
            return hero;
        }else if(currentTurn == players.get(1)){
            return hero2;
        }else if(currentTurn == players.get(2)){
            return hero3;
        }else if(currentTurn == players.get(3)){
            return hero4;
        }else if(currentTurn == players.get(4)){
            return hero5;
        }else{
            return null;
        }
    }

    public void spend(float f){
        float total = excess + f;
        if((time + total) - time > 1){
            nextTurn();
            excess = (time + total) % 1;
        }else{
            excess += f;
        }
        this.time += f;
    }

    public void nextTurn(){
        if(players.size() - 1 >= players.indexOf(currentTurn)){
            currentTurn = players.get(0);
        }else{
            currentTurn = players.get(players.indexOf(currentTurn) + 1);
        }
        send(currentTurn.getAsMention() + " Now your turn.");
    }

    private String cutString(String s){
        int i = s.indexOf(" ");
        return s.substring(i + 1).replaceAll(" ", "");
    }

}

package org.darkpaster;

import com.google.gson.Gson;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import org.darkpaster.actor.hero.Hero;
import org.darkpaster.levels.SpawnDungeon;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Game {
    public float time = 0.0f;
    public int turns = 0;

    public boolean combatMode = false;
    public float excess = 0;

    public boolean onlineMode = false;
    private User currentTurnUser;
    private String password;
    public ArrayList<User> players = new ArrayList<>();

    private MessageChannel chan;
    private Message message;
    private String msg;
    private User user;
    private String realMsg;
    public Guild guild;

    private JSONObject jsObj;

    public Hero hero;
//    public Hero hero2;
//    public Hero hero3;
//    public Hero hero4;
//    public Hero hero5;

    public Hero[] heroes;

    private SpawnDungeon startDungeon;

    //public Hero[] heroes = {hero, hero2, hero3, hero4, hero5};

    public Game(ArrayList<User> players, String password, Guild guild){
        this.guild = guild;
        this.players = players;
        startDungeon = new SpawnDungeon();
        heroes = new Hero[players.size()];
        System.out.println(players);
        System.out.println(this.players);
        //System.out.println("Players size: " + players.size());
        for (int i = 0; i < this.players.size(); i++) {
            System.out.println(i);
            heroes[i] = new Hero();
        }
        System.out.println("Heroes[0]: " + heroes[0]);
        //System.out.println("Heroes.length: " + heroes.length);
//        switch (players.size()){
//            case 1:
//                hero = new Hero();
//                break;
//            case 2:
//                hero = new Hero();
//                hero2 = new Hero();
//                break;
//            case 3:
//                hero = new Hero();
//                hero2 = new Hero();
//                hero3 = new Hero();
//                break;
//            case 4:
//                hero = new Hero();
//                hero2 = new Hero();
//                hero3 = new Hero();
//                hero4 = new Hero();
//                break;
//            case 5:
//                hero = new Hero();
//                hero2 = new Hero();
//                hero3 = new Hero();
//                hero4 = new Hero();
//                hero5 = new Hero();
//        }
        currentTurnUser = players.get(0);
        this.password = password;
    }

    public Game(JSONObject jsObj, Guild guild){
        this.guild = guild;
        this.jsObj = jsObj;
        loadGame();
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
        jsObj = new JSONObject();

        System.out.println(players);

        for(int i = 0; i < heroes.length; i++){
            String jsonInString = new Gson().toJson(heroes[i]);
                jsObj.put("hero" + i, jsonInString);
            System.out.println("Saved id = " + players.get(i).getIdLong());
            jsObj.put("players" + i, players.get(i).getIdLong());
        }

        jsObj.put("currentTurnUser", currentTurnUser.getIdLong());

            jsObj.put("password", password);

            jsObj.put("excess", excess);
            jsObj.put("time", time);


        try {
            FileWriter writer = new FileWriter(password + ".json");
            writer.write(jsObj.toJSONString());
            writer.flush();
            writer.close();
            Bot.send("Game was successfully saved.");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    protected void loadGame(){
        //System.out.println("LoadGame method jsObj: " + jsObj);
        int ind = 0;

        for (int i = 0; i < 5; i++) {
            if(jsObj.containsKey("hero" + i)){
                //Hero hero = (Hero) jsObj.get("hero" + i);
                ind = i;
            }
        }
        heroes = new Hero[ind + 1];

        password = (String) jsObj.get("password");

        for (int i = 0; i <= ind; i++) {
            heroes[i] = new Gson().fromJson((String) jsObj.get("hero" + i), Hero.class);
            long userId = (Long) jsObj.get("players" + i);
            players.add(i, Bot.jda.retrieveUserById(userId).complete());
        }

        long userId = (Long) jsObj.get("currentTurnUser");
        currentTurnUser = Bot.jda.retrieveUserById(userId).complete();

        double ebal = (double) jsObj.get("excess");
        excess = (float) ebal;
        ebal = (double) jsObj.get("time");
        time = (float) ebal;
    }


private final String WRONG = "Wrong command.";
    protected void mainGameLoop(User user, String msg, String realMsg, Message message, MessageChannel chan){
        this.user = user;
        this.msg = msg;
        this.realMsg = realMsg;
        this.message = message;
        this.chan = chan;


        if(!currentTurnUser.equals(user) && msg.startsWith("!")){
            Bot.send("Now is not your turn.");
            return;
        }

        infoCommands();

        if(msg.startsWith("!move")){
            if(getHero().move(cutString(msg))){
                Bot.send("You walked 1 meter.");
                spend(getHero().getSpeed());
            }else{
                if(combatMode){
                    Bot.send("You cannot travel long distances in combat.");
                    return;
                }
                if(cutString(msg).length() > "forward".length()){
                    String sub = "";
                    try {
                        String dirs[] = {"left", "right", "forward", "back"};
                        for (int i = 0; i < dirs.length; i++) {
                            if(cutString(msg).startsWith(dirs[i])){
                                System.out.println(cutString(msg).substring(dirs[i].length()));
                                sub = cutString(msg).substring(dirs[i].length());
                                float num = Integer.parseInt(sub) > 1 ? getHero().getSpeed() * Integer.parseInt(sub) : getHero().getSpeed();
                                for (int j = 0; j < num; j++) {
                                    getHero().move("left");
                                    spend(1);
                                    startDungeon.levelEvent(getHero());
                                    if(событие, например, бой){
                                        Bot.send("You walked " + j + " meters.");
                                        return;
                                    }
                                }

                            }

                        }

//                        if(cutString(msg).startsWith("left")){
//
//                        }else if(cutString(msg).startsWith("right")){
//                            sub = cutString(msg).substring(5);
//                            getHero().move("right", Integer.parseInt(sub));
//                            float num = Integer.parseInt(sub) > 1 ? getHero().getSpeed() * Integer.parseInt(sub) : getHero().getSpeed();
//                            Bot.send("You walked " + num + " meters.");
//                            spend(Integer.parseInt(sub));
//                        }else if(cutString(msg).startsWith("back")){
//                            sub = cutString(msg).substring(4);
//                            getHero().move("back", Integer.parseInt(sub));
//                            float num = Integer.parseInt(sub) > 1 ? getHero().getSpeed() * Integer.parseInt(sub) : getHero().getSpeed();
//                            Bot.send("You walked " + num + " meters.");
//                            spend(Integer.parseInt(sub));
//                        }else if(cutString(msg).startsWith("forward")){
//                            sub = cutString(msg).substring(7);
//                            getHero().move("forward", Integer.parseInt(sub));
//                            float num = Integer.parseInt(sub) > 1 ? getHero().getSpeed() * Integer.parseInt(sub) : getHero().getSpeed();
//                            Bot.send("You walked " + num + " meters.");
//                            spend(Integer.parseInt(sub));
//                        }else{
//                            Bot.send(WRONG);
//                            return;
//                        }


                    }catch (Exception e){
                        Bot.send(WRONG);
                    }


                }
            }
        }


    }

    private void infoCommands(){
        if(msg.equals("!current turn info")){

        }

        if(msg.equals("!hero info")){
            Bot.send("x " + getHero().getX() + "\ny " + getHero().getY() + "\nName " + getHero().getName() + "\nMove speed " + getHero().getSpeed());
        }
    }

    private void enemyTurn(){

        currentTurnUser = players.get(0);

        Bot.send("Enemies have made their turn.");
        if(players.size() > 1){
            currTurn();
        }
    }

    private Hero getHero(){
        for(User user: players){
            if(user.equals(currentTurnUser)){
                return heroes[players.indexOf(user)];
            }
        }
        System.out.println("Nope.");
        return null;
    }

    public void spend(float f){
        float total = excess + f;
        if(time + total - time >= 1){
            if(time + total - time >= 2){
                int diff = (int) (time + total - time) - 1;
                getHero().skipTurn += diff;
            }
            nextTurn();
            this.time += total;
        }else{
            excess += f;
        }
    }

    public void nextTurn(){
        if(players.size() == 1){
            enemyTurn();
            return;
        }

        if(currentTurnUser == players.get(players.size() - 1)){
            enemyTurn();
        }

        if(players.size() - 1 == players.indexOf(currentTurnUser)){
            currentTurnUser = players.get(0);
        }else{
            currentTurnUser = players.get(players.indexOf(currentTurnUser) + 1);
        }

        if(getHero().skipTurn > 0){
            if(currentTurnUser == players.get(players.size() - 1)){
                enemyTurn();
                getHero().skipTurn--;
                return;
            }

            getHero().skipTurn--;
            if(players.size() - 1 == players.indexOf(currentTurnUser)){
                currentTurnUser = players.get(0);
            }else{
                currentTurnUser = players.get(players.indexOf(currentTurnUser) + 1);
            }
        }
        if(getHero().skipTurn < 0){
            getHero().skipTurn = 0;
        }
        currTurn();
    }

    private void skipTurn(){

    }

    private void currTurn(){
        Bot.send(currentTurnUser.getAsMention() + " Now your turn.");
    }


    private String cutString(String s){
        int i = s.indexOf(" ");
        return s.substring(i + 1).replaceAll(" ", "");
    }

    private String cutString(String s, int i){
        return s.substring(i);
    }

    private String cutString(String s, String index){
        return s.substring(s.indexOf(index) + 1);
    }

}

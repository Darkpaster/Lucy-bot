package org.darkpaster;

import com.google.gson.Gson;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import org.darkpaster.actor.hero.Hero;
import org.darkpaster.actor.mob.Mob;
import org.darkpaster.levels.Level;
import org.darkpaster.levels.SpawnDungeon;
import org.darkpaster.utils.Coordinates;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Game {
    public float time = 0.0f;
    public int realTime = 0;
    public int turns = 0;
    private boolean endTurn = false;

    public boolean combatMode = false;
    public float excess = 0;

    public boolean onlineMode = false;
    User currentTurnUser;
    private String password;
    public ArrayList<User> players = new ArrayList<>();

    private MessageChannel chan;
    private Message message;
    private String msg;
    private User user;
    private String realMsg;
    public Guild guild;

    private JSONObject jsObj;

    public Hero[] heroes;

    private SpawnDungeon startDungeon;

    //public Hero[] heroes = {hero, hero2, hero3, hero4, hero5};

    public Game(ArrayList<User> players, String password, Guild guild){
        this.guild = guild;
        this.players = players;
        startDungeon = new SpawnDungeon();
        heroes = new Hero[players.size()];
        SpawnDungeon dun = new SpawnDungeon();
        for (int i = 0; i < this.players.size(); i++) {
            System.out.println(i);
            heroes[i] = new Hero();
            heroes[i].currentLevel = dun;
        }
        System.out.println("Heroes[0]: " + heroes[0]);

        currentTurnUser = players.get(0);
        this.password = password;
        init();
    }

    public Game(JSONObject jsObj, Guild guild){
        this.guild = guild;
        this.jsObj = jsObj;
        loadGame();
    }

    private void init(){
        heroes[0].currentLevel.levelInit();
        Bot.sendAttach("You remember nothing about who you are and what are you doing in this place.", Bot.sign);
        currTurn();
    }


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
            jsObj.put("realTime", realTime);


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
        ebal = (int) jsObj.get("realTime");
        realTime = (int) ebal;
    }


private final String WRONG = "Wrong command.";
    protected void mainGameLoop(User user, String msg, String realMsg, Message message, MessageChannel chan){
        this.user = user;
        this.msg = msg;
        this.realMsg = realMsg;
        this.message = message;
        this.chan = chan;


        if(!currentTurnUser.equals(user) && msg.startsWith("!")){
            Bot.sendA("Now is not your turn.");
            return;
        }

        infoCommands();

        if(msg.equals("!attack") && getHero().currentLevel.combatMode){
            getHero().attack(getHero().currentLevel.enemies.get(0));
            spend(1);
        }

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
                                for (int j = 0; j < Integer.parseInt(sub); j++) {
                                    if(!getHero().move(dirs[i])){
                                        System.out.println("Move error.");
                                        return;
                                    }
                                    getHero().currentLevel.levelEvent(getHero());
                                    if(getHero().currentLevel.combatMode){
                                        Bot.sendA("You walked " + j * getHero().getSpeed() + " meters and noticed **" + getHero().currentLevel.enemies.get(0).getName() + "**.");
                                        spend(j);
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

        if(msg.equals("!time")){
            Bot.send(getHero().currentLevel.timeOfDay());
        }

        if(msg.equals("!hero info")){
            Bot.send("x " + getHero().getX() + "\ny " + getHero().getY() + "\nName " + getHero().getName() + "\nMove speed " + getHero().getSpeed());
        }
    }

    private void enemyTurn(){

        getHero().currentLevel.levelEvent(getHero());

        if(getHero().currentLevel.combatMode){
            for(Mob mob: getHero().currentLevel.enemies){
                mob.act(heroes);
            }
        }
    }

    public Hero getHero(){
        for(User user: players){
            if(user.equals(currentTurnUser)){
                return heroes[players.indexOf(user)];
            }
        }
        System.out.println("Nope.");
        return null;
    }

    public Hero getHero(User user){
        return heroes[players.indexOf(user)];

    }

    public void spend(float f){
        float total = excess + f;
        if(time + total - time >= 1){
            if(time + total - time >= 2){
                int diff = (int) (time + total - time) - 1;
                getHero().skipTurn += diff;
            }
            nextTurn((int) total);
            this.time += total;
            realSpend((int) total);
        }else{
            excess += f;
        }
    }


    private void realSpend(int i){
        realTime += i;
        if(realTime > 1440){
            realTime = 0;
        }
    }

    public void nextTurn(int turns){
        System.out.println("NextTurn readed");
        enemyTurn();
        if(players.size() == 1){
            return;
        }
        User previousUser = currentTurnUser;
        if(turns > 1){
            getHero().skipTurn += turns - 1;
        }else{
            getHero().skipTurn--;
        }
        if(getHero().skipTurn < 0){
            getHero().skipTurn = 0;
        }
        if(currentTurnUser.equals(players.get(players.size() - 1))){
            //fight methode
            if(getHero(players.get(0)).skipTurn <= getHero().skipTurn){
                currentTurnUser = players.get(0);
            }
        }else{
            if(getHero().skipTurn >= getHero(players.get(players.indexOf(currentTurnUser) + 1)).skipTurn){
                currentTurnUser = players.get(players.indexOf(currentTurnUser) + 1);
            }

        }

        if(!currentTurnUser.equals(previousUser)){
            currTurn();
        }
    }

    private void skipTurn(){

    }

    private void currTurn(){
        Bot.sendA("Now your turn.");
    }


    public Level getCurrentLevel(Hero hero){
        SpawnDungeon spDun = new SpawnDungeon();
        if(Coordinates.levelRadiusXYZ(hero, spDun)){
            //return location.START_DUNGEON;
            return spDun;
        }else{
            return null;
            //return null;
        }
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

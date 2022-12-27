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
import org.darkpaster.utils.StringEditor;
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

    public ArrayList<Hero> heroes = new ArrayList<>();

    private SpawnDungeon startDungeon;

    //public Hero[] heroes = {hero, hero2, hero3, hero4, hero5};

    public Game(ArrayList<User> players, String password, Guild guild){
        this.guild = guild;
        this.players = players;
        startDungeon = new SpawnDungeon();
        //heroes = new Hero[players.size()];
        SpawnDungeon dun = new SpawnDungeon();
        currentTurnUser = players.get(0);
        this.password = password;
        for (int i = 0; i < this.players.size(); i++) {
            System.out.println(i);
            heroes.add(i, new Hero());
            heroes.get(i).currentLevel = dun;
        }
        //System.out.println("Heroes[0]: " + heroes[0]);
    }

    public Game(JSONObject jsObj, Guild guild){
        this.guild = guild;
        this.jsObj = jsObj;
        loadGame();
    }

     void init(boolean first){
        getHero().currentLevel.levelInit();
         if (first) {
             Bot.sendAttach("You remember nothing about who you are and what are you doing in this place.", Bot.sign);
         }
        currTurn();
    }


     void saveGame(){
        jsObj = new JSONObject();

        System.out.println(players);

        for(int i = 0; i < heroes.size(); i++){
            String jsonInString = new Gson().toJson(heroes.get(i));
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

     void loadGame(){
        //System.out.println("LoadGame method jsObj: " + jsObj);
        int ind = 0;

        for (int i = 0; i < 5; i++) {
            if(jsObj.containsKey("hero" + i)){
                //Hero hero = (Hero) jsObj.get("hero" + i);
                ind = i;
            }
        }
        //heroes = new Hero[ind + 1];

        password = (String) jsObj.get("password");

        for (int i = 0; i <= ind; i++) {
            heroes.add(i, new Gson().fromJson((String) jsObj.get("hero" + i), Hero.class));
            long userId = (Long) jsObj.get("players" + i);
            players.add(i, Bot.jda.retrieveUserById(userId).complete());
        }

        long userId = (Long) jsObj.get("currentTurnUser");
        currentTurnUser = Bot.jda.retrieveUserById(userId).complete();

        double ebal = (double) jsObj.get("excess");
        excess = (float) ebal;
        ebal = (double) jsObj.get("time");
        time = (float) ebal;
        ebal = (long) jsObj.get("realTime");
        realTime = (int) ebal;
    }


private final String WRONG = "Wrong command.";

    public float spended = 0;
    protected void mainGameLoop(User user, String msg, String realMsg, Message message, MessageChannel chan){
        this.user = user;
        this.msg = msg;
        this.realMsg = realMsg;
        this.message = message;
        this.chan = chan;

        spended = 0;

        boolean combMode = getHero().currentLevel.combatMode;


        if(!currentTurnUser.equals(user) && msg.startsWith("!")){
            Bot.send(user.getAsMention() + " Now is not your turn.");
            return;
        }

        infoCommands();

        if(msg.equals("!attack") && getHero().currentLevel.combatMode){
            System.out.println("attack!");
            getHero().attack(getHero().currentLevel.enemies.get(0));
        }

        if(msg.equals("!wait")){
            Bot.send("One turn skipped.");
            spended = 1;
        }

        if(msg.startsWith("!move")){
            if(getHero().move(StringEditor.cutStr(msg))){
                Bot.send("You walked " + (int) getHero().getSpeed() + " meter.");
                spended = 1;
                getHero().currentLevel.levelEvent(getHero());
            }else{
                if(getHero().currentLevel.combatMode){
                    Bot.send("You cannot travel long distances in combat.");
                    return;
                }
                System.out.println("here: " + StringEditor.cutStr(msg).length() + "/" + "forward".length());
                //if(StringEditor.cutStr(msg).length() > "forward".length()){
                    String sub = "";
                    try {
                        String[] dirs = {"left", "right", "forward", "back", "up", "down"};
                        for (String dir : dirs) {
                            System.out.println(StringEditor.cutStr(msg));
                            if (StringEditor.cutStr(msg).startsWith(dir)) {
                                System.out.println(StringEditor.cutStr(msg).substring(dir.length()));
                                sub = StringEditor.cutStr(msg).substring(dir.length());
                                for (int j = 0; j <= Integer.parseInt(sub); j++) {
                                    System.out.println("move!!!!!!!!!!");
                                    if (!getHero().move(dir)) {
                                        System.out.println("Move error.");
                                    }
                                    getHero().currentLevel.levelEvent(getHero());
                                    if (getHero().currentLevel.combatMode) {
                                        Bot.sendA("You walked " + (int) (j * getHero().getSpeed()) + " meters and noticed **" + getHero().currentLevel.enemies.get(0).getName() + "**.");
                                        spended = j;
                                        break;
                                    }else if(j == Integer.parseInt(sub)){
                                        Bot.sendA("You walked " + (int) (j * getHero().getSpeed()) + " meters.");
                                        spended = j;
                                    }
                                }

                            }

                        }


                    }catch (Exception e){
                        e.printStackTrace();
                        Bot.send(WRONG);
                    }


                //}
            }
        }

        if (getHero().currentLevel.combatMode && !combMode) {
            Bot.sendA("You noticed **" + getHero().currentLevel.enemies.get(0).getName() + "**.");
        }

        if(spended > 0){
            spend(spended);
        }


    }

    private void infoCommands(){
        if(msg.equals("!current turn info")){
            Bot.send("Now " + currentTurnUser.getAsMention() + "'s turn.");
        }

        if(msg.equals("!time")){
            Bot.send(getHero().currentLevel.timeOfDay());
        }

        if(msg.equals("!hero info")){
            Bot.send("x " + getHero().getX() + "\ny " + getHero().getY() + "\nName " + getHero().getName() + "\nMove speed " + getHero().getSpeed());
        }
    }

    private void enemyTurn(){
        boolean z = false;
        for(Hero hero: heroes){
            boolean combatMode = hero.currentLevel.combatMode;
            if(combatMode && hero.currentLevel.enemies.size() == 0){
                z = true;
                hero.currentLevel.combatMode = false;
            }
        }
        if(z){
            Bot.send("Combat mode disabled.");
        }

        if(getHero().currentLevel.combatMode){
            for(Mob mob: getHero().currentLevel.enemies){
                mob.act(heroes);
            }
        }else{
            getHero().currentLevel.levelEvent(getHero());
        }
    }

    public Hero getHero(){
        for(User user: players){
            if(user.equals(currentTurnUser)){
                return heroes.get(players.indexOf(user));
            }
        }
        return null;
    }

    public Hero getHero(User user){
        return heroes.get(players.indexOf(user));
    }

    public User getUser(Hero hero){
        return players.get(heroes.indexOf(hero));
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
        System.out.println(turns);
        if(players.size() == 1){
            enemyTurn();
            return;
        }
        User previousUser = currentTurnUser;

        skipTurn();

        if(currentTurnUser.equals(players.get(players.size() - 1))){
            enemyTurn();
            if(getHero(players.get(0)).skipTurn <= getHero().skipTurn){
                currentTurnUser = players.get(0);
                System.out.println("zzzzzzz");
            }
        }else{
            if(getHero().skipTurn >= getHero(players.get(players.indexOf(currentTurnUser) + 1)).skipTurn){
                currentTurnUser = players.get(players.indexOf(currentTurnUser) + 1);
                System.out.println("fffffffffff");
            }

        }

        if(!currentTurnUser.equals(previousUser)){
            currTurn();
        }
    }

    private void skipTurn(){
        boolean z = turns > 1;
        if(z){
            getHero().skipTurn += turns;
        }

        for(Hero hero: heroes){
            if(!hero.equals(getHero()) && hero.skipTurn > 0){
                if(z){
                    hero.skipTurn -= turns;
                }else{
                    hero.skipTurn--;
                }
            }
        }

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



    private String cutString(String s, int i){
        return s.substring(i);
    }

    private String cutString(String s, String index){
        return s.substring(s.indexOf(index) + 1);
    }

}

package org.darkpaster;


import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;
import net.dv8tion.jda.api.events.emoji.EmojiAddedEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.AttachmentProxy;
import net.dv8tion.jda.api.utils.FileUpload;
import org.darkpaster.NN.NeuralNetwork;
import org.darkpaster.actor.hero.Hero;
import org.darkpaster.utils.Coordinates;
import org.darkpaster.utils.Random;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.UnaryOperator;

import static org.darkpaster.GameGUI.*;
import static org.darkpaster.Russian.read;

public class Bot extends ListenerAdapter {

    public static final Emoji HEART = Emoji.fromUnicode("U+2764");

//    public static final EmbedBuilder files = new EmbedBuilder().setImage("attachment://black.png")
//            .setImage("attachment://giant_rat.png").setImage("attachment://dungeon.png");

    public static final File blackPath = new File("attachFiles/black.jpg");
    public static final File sign = new File("attachFiles/sign.jpg");
    private boolean gameStarted = false;

    protected ArrayList<User> players = new ArrayList<>();

    protected String password;
    private boolean accountConnected = false;
    private boolean accountCreated = false;


    private final String commands = "__!help__ - I'll send a list of all commands (except for commands for chatting).\n" +
            "__!gameHelp__ - Idk this command yet.\n" +
            "__!roll *max value*__ - I'll send a random number from 0 to written value.\n" +
            "__!remind *hours.minutes*__ - I'll remind you when your entered time is up.";

    protected static MessageChannel chan;
    protected static Message message;
    protected static String msg;
    protected static String prevMsg;
    protected static String realMsg;
    protected static MessageReceivedEvent eventMsg;

    protected static Guild guild;

    public static Game game;
    protected JSONObject jsObj;

    protected static User user;

    public static JDA jda;

    static NeuralNetwork nn = null;
    static boolean activated = false;

    public static final ArrayList<String> usersMessages = new ArrayList<>();


    public static void main(String[] args) throws Exception {
        if(args.length > 0) {
            System.out.println(123);
//            activated = true;
//            UnaryOperator<Double> sigmoid = x -> 1 / (1 + Math.exp(-x));
//            UnaryOperator<Double> dsigmoid = y -> y * (1 - y);
//            nn = new NeuralNetwork(0.01, sigmoid, dsigmoid, 2500, 1024, 128, 1);
//            nn.learnImg(20);
        }



        jda = JDABuilder.createLight("MTAzMTIzMjMyMzczMzE2NDA4Mg.GetuPM.7ktACySux3lMkbYvcgZr32LCRKlAhycwkhgWOE",
                GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MESSAGE_REACTIONS,
                GatewayIntent.DIRECT_MESSAGE_REACTIONS, GatewayIntent.GUILD_MEMBERS)
                .addEventListeners(new Bot())
                .setActivity(Activity.watching("Berserk"))
                .build();


        //jda.awaitReady().getCategories().get(0).getTextChannels().get(0).sendMessage("matawa").submit();
    }


    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getName().equals("game")) {

            clearGame();
            hero = new Hero();
            hero.setName("ты");
            spawnMobs();
            buildWindow();
            StringBuilder realWindow = new StringBuilder();
            for (String s : heroRealm) {
                realWindow.append(s);
            }
            event.reply(getStatus() + realWindow + log)
                    .addActionRow(Button.primary("MU", "↑"), Button.primary("ATK", "Attack"))
                    .addActionRow(
                            Button.primary("ML", "←"), Button.primary("MR", "→"))
                    .addActionRow(Button.primary("MD", "↓"))
                    .queue();
        }
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        clearLog();
        turns++;
        checkTarget();
        GameGUI.spawnMobs();
        GameGUI.enemyTurn();
        boolean z = false;
        z = hero.moveGUI(event.getComponentId());
        if (z) {
            log.append(MOVE + "```");
        } else if (event.getComponentId().equals("ATK")) {
            if (target == null) {
                log.append("Нет цели.\n");
                turns--;
            } else {
                System.out.println("XY enemy: " + target.getX() + "/" + target.getY());
                System.out.println("XY hero: " + hero.getX() + "/" + hero.getY());
                hero.attack(target);
            }
        }

        StringBuilder realWindow = new StringBuilder();
        for (String s : heroRealm) {
            realWindow.append(s);
        }
        if (hero.getHP() <= 0) {
            event.editMessage("Ты помер.").queue();
        } else {
            event.editMessage(getStatus() + realWindow + log).queue();
        }
    }


    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        //getContentDisplay() - сообщение в том виде, в котором появляется в дискорде
        //getContentRaw() - в необработанном виде //86
        eventMsg = event;
        message = event.getMessage();
        List<MessageEmbed> embeds = message.getEmbeds();
        msg = event.getMessage().getContentDisplay();
        realMsg = event.getMessage().getContentRaw();
        chan = event.getChannel();
        user = message.getAuthor();

        if (message.isFromGuild() && !user.isBot()) {
            guild = message.getGuild();
            guild.upsertCommand("game", "Test game.").queue();
            guild.updateCommands().queue();
        }

        if(msg.equals("!get channel") && usersMessages.size() < 1){
            String[] messages = read(new File(chan.getName()+".txt")).split("∫");
            for(String m: messages){
                if(m.startsWith("haykmund")){
                    usersMessages.add(m.replaceFirst("haykmund", "").replaceAll("√", ""));
                }
                if(m.startsWith("humanhu")){
                    usersMessages.add(m.replaceFirst("humanhu", "").replaceAll("√", ""));
                }
            }
            System.out.println(usersMessages.size());
        }

        if (msg.equals("!help")) {
            //send(commands);
            send("This command disabled.");
        }

        if (msg.equals("!gameHelp")) {
            send("Idk this command yet(.");
        }
        if (msg.equals("!play")) {
            launchGame();
        }
        if (msg.equals("!exit") && gameStarted) {
            exitGame();
        }

        if (msg.startsWith(".roll")) {
            try {
                if (msg.contains("-")) {
                    int i = Integer.parseInt(msg.substring(".roll".length(), msg.indexOf("-")).replaceAll(" ", ""));
                    msg.replaceAll("-", "");
                    int i2 = Integer.parseInt(msg.substring(msg.indexOf("-") + 1).replaceAll(" ", ""));
                    send(user.getName() + " rolls **" + roll(i, i2) + "** (" + i + " - " + i2 + ")");
                } else {
                    int i = Integer.parseInt(msg.substring(".roll".length()).replaceAll(" ", ""));
                    send(user.getName() + " rolls **" + roll(i) + "** (1 - " + i + ")");
                }
            } catch (Exception e) {
                send("Wrong command.");
                e.printStackTrace();
            }
        }

        if (msg.startsWith("!remind")) {
            int index = msg.indexOf("d") + 1;
            int i = msg.indexOf(".");
            int i5 = msg.lastIndexOf(".");
            try {
                int i2 = Integer.parseInt(msg.substring(index, i).replaceAll(" ", ""));
                if (i5 != i) {
                    int i3 = Integer.parseInt(msg.substring(i + 1, i5).replaceAll(" ", ""));
                    String stri = msg.substring(i5 + 1);
                    send(user.getAsMention(), i2, i3, stri);
                } else {
                    int i3 = Integer.parseInt(msg.substring(i + 1).replaceAll(" ", ""));
                    send(user.getAsMention(), i2, i3);
                }
            } catch (Exception e) {
                send("Wrong command.");
                e.printStackTrace();
            }

        }
        if (!user.isBot()) {
            List<Message.Attachment> att = message.getAttachments();
            if(att.size() > 0 && activated){
                InputStream in = null;
                System.out.println(1234);
                for(Message.Attachment attach: att){
                    if(attach.isImage()){
                        System.out.println(123);
                        boolean checkbox = false;
                        try {
                            URL url = new URL(attach.getProxy().getUrl(50, 50));
                            URLConnection connection = url.openConnection();
                            connection.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
                            connection.connect();
                            in = connection.getInputStream();
                            Files.copy(in, Paths.get("bear.jpg"), StandardCopyOption.REPLACE_EXISTING);
                            in.close();
                            BufferedImage img = ImageIO.read(new File("bear.jpg"));
                            double[] input = new double[img.getHeight() * img.getWidth()];
                            for (int y = 0; y < img.getHeight(); y++) {
                                for (int x = 0; x < img.getWidth(); x++) {
                                    input[y * img.getWidth() + x] = (img.getRGB(x, y) & 0xff) / (255.0 * 3);
                                }
                            }
                            double[] answer = nn.feedForward(input);
                            if (answer[0] > 0.5) {
                                checkbox = true;
                                System.out.println("true");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if(checkbox){
                            System.out.println(1231241223);
                            message.addReaction(HEART).queue();
                            break;
                        }
                    }
                }
            }
            if (gameStarted && chan.equals(gameChannel)) {
                createGame();
            } else {
                chatting(event);
                Russian.general();
            }

            if(usersMessages.size() > 0 && Math.random() > 0.95d){
                String randomMes;
                do{
                    randomMes = Random.randomString(usersMessages);
                }while(randomMes.length() > 35);
                message.reply(randomMes).submitAfter(2, TimeUnit.SECONDS);
            }
//            if(msg.equals("_test!")){
//                //message.reply("123").timeout(2, TimeUnit.SECONDS).queue();
//                //System.out.println(123);
//                ArrayList<String> test = new ArrayList<>();
//                test.add("1");
//                test.add("12");
//                test.add("123");
//                message.reply(Random.randomString(test)).submitAfter(2, TimeUnit.SECONDS);
//            }
        }
//            if(att.size() > 0){
//                System.out.println("att size > 0");
//                for(Message.Attachment a: att){
//                    System.out.println("att "+a);
//                    if(a.isImage()){
//                        System.out.println("att is image");
//                        try {
//                            System.out.println(a.getProxy().downloadToFile(new File("wtf.jpeg")).get().getPath());
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }

        prevMsg = msg;

    }

    private boolean once = true;

    private void createGame() {

        if (!accountConnected && !accountCreated) {
            connectAcc();
        }

        if (accountCreated && game == null) {
            game = new Game(players, password, guild);
            System.out.println("Players: " + game.players);
            System.out.println("Heroes: " + game.heroes);
            game.init(true);
        }


        if (accountConnected && game == null) {
            //System.out.println("Passed to game jsObj" + jsObj);
            game = new Game(jsObj, guild);
            game.init(false);
        }

        if (game != null && game.players.contains(user)) {
            //System.out.println("Pass");
            game.mainGameLoop(user, msg, realMsg, message, chan);
        }


    }


    private String passwordGenerator() {
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 9; i++) {
            password.append(roll(100));
        }
        return new String(password);
    }

    private void createNewAcc() {

        if (!gameModeSelected) {
            checkGameMode();
            if (once) {
                send("Which game mode do you want to play?\n**1** - Online\n**2** - Offline");
                once = false;
            }
            if (gameModeSelected) {
                once = true;
            }
        }

        if (online && !playersAmountSelected && gameModeSelected) {
            if (once) {
                send("Enter number of players.");
                once = false;
            } else {

                try {
                    int mess = Integer.parseInt(msg.replaceAll(" ", ""));
                    if (mess > 5) {
                        send("Max number of players is 5. Enter number again.");
                    } else if (mess < 2) {
                        send("Min number of players is 2. Enter number again.");
                    } else {
                        playersAmount = mess;
                        playersAmountSelected = true;
                        send("Write __!ready__ to register.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    send("Invalid format. Enter a number.");
                }

            }

        }
        if (gameModeSelected && !online) {
            playersAmountSelected = true;
        }


        if (gameModeSelected && playersAmountSelected) {

            try {

                if (online) {
                    if (msg.equals("!ready")) {
                        if (!players.contains(user)) {
                            players.add(user);
                            send("Player **" + user.getName() + "** registered!" +
                                    "\nI need to register " + playersAmount + " players to start. Current: **" + players.size() + "/" + playersAmount + "**");
                            if (players.size() >= playersAmount) {
                                accountCreated = true;
                                String password = passwordGenerator();
                                this.password = password;
                                File file = new File(password + ".json");
                                for (User user : players) {
                                    user.openPrivateChannel().queue((privateChannel -> {
                                        privateChannel.sendMessage(password).queue();
                                    }));
                                }
                                file.createNewFile();
                                send("**New game (multiplayer mode) has been created!**\nI sent your password for this game. In future you'll be able to load this game with password.");
                            }
                        } else {
                            send(user.getName() + ", you already registered.\nI need to register " + playersAmount + " players to start. Currently: **" + players.size() + "/2" + playersAmount + "**");
                        }

                    }

                } else {
                    String password = passwordGenerator();
                    this.password = password;
                    File file = new File(password + ".json");
                    file.createNewFile();
                    players.add(user);
                    user.openPrivateChannel().queue((privateChannel -> {
                        privateChannel.sendMessage(password).queue();
                    }));
                    accountCreated = true;
                    send("**New game (single mode) has been created!**\nI sent your password for this game. In future you'll be able to load this game with password.");
                }


            } catch (IOException e) {
                e.printStackTrace();
                send(e.toString());
            }


        }


    }

    private boolean gameModeSelected = false;
    private boolean online = false;

    private int playersAmount = 0;

    private void checkGameMode() {
        if (!once && msg.equals("1")) {
            online = true;
            gameModeSelected = true;
        } else if (!once && msg.equals("2")) {
            online = false;
            gameModeSelected = true;
        }
    }

    private String selected = "";
    private boolean playersAmountSelected = false;


    private void clear() {
        gameStarted = false;
        once = true;
        accountCreated = false;
        accountConnected = false;
        gameModeSelected = false;
        playersAmountSelected = false;
        playersAmount = 0;
        players.clear();
        selected = "";
    }

    private void connectAcc() {

        if (msg.equals("1") || selected.equals("1")) {
            createNewAcc();
            selected = "1";
        } else if (msg.equals("2") || selected.equals("2")) {
            loadExistAcc();
            selected = "2";
        }

    }

    private void loadExistAcc() {
        if (msg.equals("2")) {
            send("Enter special password for your game.");
            return;
        }
//        String comm = "12345678912345:";
//        String str = msg.substring(0, comm.length())
        File file = new File(msg + ".json");

        if (!file.exists()) {
            send("Invalid password.");
        } else {
            accountConnected = true;
            send("Game has been loaded!");
            password = msg;
            try {
                JSONParser parser = new JSONParser();
                FileReader file2 = new FileReader(password + ".json");
                Object obj = parser.parse(file2);
                JSONObject jsObj = (JSONObject) obj;
                System.out.println("LoadExistAcc jsObj" + jsObj);
                this.jsObj = jsObj;
                System.out.println("LoadExistAcc jsObj" + this.jsObj);
            } catch (Exception e) {
                e.printStackTrace();
                send("Error.");
                exitGame();
            }

        }
    }

    protected void exitGame() {
        if (game != null) {
            game.saveGame();
        }
        clear();
        game = null;
        send("Game mode disabled.");
    }

    private MessageChannel gameChannel;

    private void launchGame() {
        if (gameStarted) {
            send("Game mode already enabled.");
            return;
        }
        gameStarted = true;
        send("Game mode enabled.");
        gameChannel = chan;
        send("**1** - Create new game.\n**2** - Load existing game.");
        //gameStarted = false;
    }

    private void chatting(MessageReceivedEvent e) {

        if (msg.equalsIgnoreCase("berserk")) {
            send("Berserk.");
        }
        if (msg.equalsIgnoreCase("Gaos")) {

            boolean z = roll(100) < 30;
            boolean z2 = roll(100) < 60;

            if (z) {
                send("What's the matter?");
            } else if (z2) {
                send("How can i help?");
            } else {
                send("?");
            }
        }
        if (msg.equalsIgnoreCase("people")) {
            send("Equals shit.");
        }
        if (msg.equalsIgnoreCase("society")) {
            send("Sucks.");
        }
        if (msg.equalsIgnoreCase("doomsday")) {
            send("Is what we're all waiting for.");
        }
        if (msg.equalsIgnoreCase("Molly")) {
            if (roll(100) > 50) {
                send("Is the best cat!");
            } else {
                send("Is our god.");
            }
        }

        if (msg.equalsIgnoreCase("Gaos stop")) {
            send("(");
        }

//        if(msg.equalsIgnoreCase("Guts hi") || msg.equalsIgnoreCase("Guts hello") || msg.equalsIgnoreCase("Guts hi there")){
//            send("Hi " + user.getName() + ".");
//        }

        if (msg.toLowerCase().contains("Gaos")) {

            if ((msg.contains("say hi to") || msg.contains("say hello to")) && msg.toLowerCase().startsWith("Gaos")) {
                int index = msg.indexOf("to");
                String index2 = msg.substring(index);
                String nickname = index2.substring(index2.indexOf(" "));
                if (roll(100) > 50) {
                    send("Hi " + nickname + ".");
                } else {
                    send("Hello " + nickname + ".");
                }
            } else if ((msg.contains("hi") || msg.contains("sup") || msg.contains("hello") || msg.contains("wassup")) && msg.toLowerCase().startsWith("Gaos")) {
                send("Hi " + user.getName() + ".");
            }

            if (msg.toLowerCase().contains("how are you")) {
                if (roll(100) > 50) {
                    send("I think i'm good but i can be wrong cause i cannot to feel even anything.");
                } else {
                    send("If I could be as much strong as Guts I'd answer you i'm good...");
                }
            } else if (msg.toLowerCase().contains("how old are you?") || msg.toLowerCase().contains("how old are you")) {
                if (roll(100) > 50) {
                    send("I don't remember. Honestly.");
                } else {
                    send("Don't ask about it.");
                }
            } else if (msg.toLowerCase().contains("where are you from?") || msg.toLowerCase().contains("where are you from")
                    || msg.contains("where are u from") || msg.contains("where are u from?")) {
                send("From Nekoland");
            } else if (msg.toLowerCase().contains("what's better") || msg.toLowerCase().contains("what is better") || msg.toLowerCase().contains("what is") ||
                    msg.toLowerCase().contains("how much") || msg.toLowerCase().contains("how many") || msg.toLowerCase().contains("do you")) {
                send("Idk.");
            } else if (msg.toLowerCase().contains("love you") || msg.toLowerCase().contains("i like you") || msg.toLowerCase().contains("likes you") || msg.toLowerCase().contains("loves you") || realMsg.contains(":heart:")) {
                send(":heart:");
            } else if (msg.endsWith("?")) {
                send("Idk.");
            }

            String ms = msg.toLowerCase();
            if (ms.contains("you're smart") || ms.contains("you are smart")
                    || ms.contains("you are beautiful") || ms.contains("you're beautiful")
                    || realMsg.contains("it suits you") || realMsg.contains("you're cute") || realMsg.contains("you are cute")) {
                send("Thank you.");
            }


        }
    }

    public static void send(String msg) {
        chan.sendMessage(msg).submit();
    }

    public static void sendA(String msg) {
        chan.sendMessage(game.currentTurnUser.getAsMention() + "\n" + msg).submit();
    }

    public static void sendAttach(String msg, File file) {
        chan.sendMessage(msg).addFile(file).queue();
    }

    public static void sendReference(String msg) {
        //chan.sendMessage(msg).setMessageReference(message).submit();
        chan.sendMessage(msg).reference(message).submit();
    }

    public static void sendQue(String msg) {
        chan.sendMessage(msg).queue();
    }

    public static void sendOnce(String msg, String nonce) {
        chan.sendMessage(msg).nonce(nonce).submit();
    }

    public static void sendContent(String msg, String content) {
        chan.sendMessage(msg).content(content).submit();
    }

    public static void send(String msg, int sec) {
        chan.sendMessage(msg).submitAfter(sec, TimeUnit.SECONDS);
    }

    private void send(String msg, int hours, int minutes) {
        //System.out.println("H: " + hours);
        //System.out.println("M: " + minutes);
        send("I'll remind you about something in " + hours + " hours and " + minutes + " minutes.");
        if (hours > 0) {
            int m = 60 * hours;
            chan.sendMessage(msg + " Your time is up.").submitAfter(minutes + m, TimeUnit.MINUTES);
            //chan.sendMessage(str).timeout(minutes + m, TimeUnit.MINUTES);
        } else {
            chan.sendMessage(msg + " Your time is up.").submitAfter(minutes, TimeUnit.MINUTES);
            //chan.sendMessage(str).timeout(minutes, TimeUnit.MINUTES);
        }
    }

    private void send(String msg, int hours, int minutes, String desc) {
        //System.out.println("H: " + hours);
        //System.out.println("M: " + minutes);
        send("I'll remind you about " + desc + " in " + hours + " hours and " + minutes + " minutes.");
        if (hours > 0) {
            int m = 60 * hours;
            chan.sendMessage(msg + " " + desc).submitAfter(minutes + m, TimeUnit.MINUTES);
            //chan.sendMessage(str).timeout(minutes + m, TimeUnit.MINUTES);
        } else {
            chan.sendMessage(msg + " " + desc).submitAfter(minutes, TimeUnit.MINUTES);
            //chan.sendMessage(str).timeout(minutes, TimeUnit.MINUTES);
        }
    }

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        //System.out.println(event.getReaction().getEmoji().getName());
        //System.out.println(event.getReaction().getEmoji());

        //send("Meow.");
    }


    public static int roll(int max) {
        return max > 0 ? Random.Int(max) : 0;
    }

    public static int roll(int min, int max){
        return Random.IntRange(min, max);
    }
}
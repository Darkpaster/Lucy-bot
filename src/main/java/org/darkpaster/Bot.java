package org.darkpaster;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;

import java.io.*;

public class Bot extends ListenerAdapter {

    public static final Emoji HEART = Emoji.fromUnicode("U+2764");
    public boolean gameStarted = false;
    private boolean accountConnected = false;
    private final String[] commands = {"/help", "/gameHelp"};

    private MessageChannel chan;
    private Message message;
    private String msg;
    private String realMsg;


    public static void main(String[] args) throws Exception {


        JDA jda = JDABuilder.createLight("MTAzMTIzMjMyMzczMzE2NDA4Mg.GWJZnR.pOJ1ZiCvfSyzY6aSAuphsvFRTloq-wGyI80GNg",
                GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MESSAGE_REACTIONS,
                        GatewayIntent.DIRECT_MESSAGE_REACTIONS)
                .addEventListeners(new Bot())
                .setActivity(Activity.watching("Berserk"))
                .build();

        //jda.awaitReady().getCategories().get(0).getTextChannels().get(0).sendMessage("matawa").submit();
    }


    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        //getContentDisplay() - сообщение в том виде, в котором появляется в дискорде
        //getContentRaw() - в необработанном виде
        message = event.getMessage();
        msg = event.getMessage().getContentDisplay();
        realMsg = event.getMessage().getContentRaw();
        chan = event.getChannel();

        if(msg.equals("!help")){
            send("later.");
        }
        if(msg.equals("!play") && !gameStarted){
            launchGame();
        }
        if(msg.equals("!exit") && gameStarted){
            exitGame();
        }

        if(gameStarted){
            game();
        }else{
            chatting(event);
        }

    }

    private void game(){
        if(!accountConnected){
            connectAcc();
        }else{

        }

    }

    private void connectAcc(){
        JSONObject json = new JSONObject();
        String comm = "12345678912345:";
        String str = msg.substring(0, comm.length() - 1);
        if(str.equals("Create account:")){
            File file = new File(msg.substring(comm.length() - 1, str.length()));
            System.out.println("File exists: " + file.exists());
            if(!file.exists()){
                try {
                    System.out.println(file.createNewFile());
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
            try {
                ObjectOutputStream objStream = new ObjectOutputStream(new FileOutputStream(file));
                objStream.writeObject(json);
                objStream.close();
                send("Account has been created!");
            }catch (IOException e){
                e.printStackTrace();
                System.out.println("govno");
            }

        }

        if(msg.contains("Log in account:")){
            File file = new File(msg.substring(comm.length() - 1, str.length()));
            if(!file.exists()){
                send("Invalid password.");
            }else{
                accountConnected = true;
            }
        }

    }

    private void exitGame(){
        gameStarted = false;
        send("Game mode disabled.");
    }

    private void launchGame(){
        gameStarted = true;
        send("Game mode enabled.");
    }

    private void chatting(MessageReceivedEvent e){
        if(msg.equalsIgnoreCase("berserk")){
            send("Berserk.");
        }
        if(msg.equalsIgnoreCase("Lucy")){

            boolean z = roll(100) < 30;
            boolean z2 = roll(100) < 60;

            if(z){
                send("What's the matter?");
            }else if(z2){

                send("?");
            }else{
                send("How can i help?");
            }
        }
        if(msg.equalsIgnoreCase("people")){
            send("Equals shit.");
        }
        if(msg.equalsIgnoreCase("society")){
            send("Sucks.");
        }
        if(msg.equalsIgnoreCase("doomsday")){
            send("Is what we're all waiting for.");
        }
        if(msg.equalsIgnoreCase("Molly")){
            if(roll(100) > 50){
                send("Is the best cat!");
            }else{
                send("Is our god.");
            }
        }

        if(msg.equalsIgnoreCase("Lucy stop")){
            send("Ok.");
        }
    }

    private void send(String str){
        chan.sendMessage(str).submit();
    }

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event)
    {
        //System.out.println(event.getReaction().getEmoji());

        send("Meow.");
    }


    public static int roll( int max ) {
        return max > 0 ? (int)(Math.random() * max) : 0;
    }

//    public static int roll( int min, int max ) {
//        return min + (int)(Math.random() * (max - min));
//    }
//
//    public static int roll2( int min, int max ) {
//        return min + (int)(Math.random() * (max - min + 1));
//    }
//
//    public static int roll3( int min, int max ) {
//        return min + (int)((Math.random() + Math.random()) * (max - min + 1) / 2f);
//    }
}
package org.darkpaster;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;

public class Bot extends ListenerAdapter {

    public static final Emoji HEART = Emoji.fromUnicode("U+2764");
    public boolean gameStarted = false;
    private final String[] commands = {"/help", "/gameHelp"};

    private MessageChannel chan;
    private Message message;
    private String msg;
    private String realMsg;


    public static void main(String[] args) throws Exception {


        JDA jda = JDABuilder.createLight("MTAzMTIzMjMyMzczMzE2NDA4Mg.GWJZnR.pOJ1ZiCvfSyzY6aSAuphsvFRTloq-wGyI80GNg",
                GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES, GatewayIntent.MESSAGE_CONTENT)
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

        if(msg.equals("/help")){
            send("later");
        }

        commonCommands(event);
    }

    private void commonCommands(MessageReceivedEvent e){
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

                send("What do you need?");
            }
        }
        if(msg.equalsIgnoreCase("people")){
            send("Equals shit.");
        }
        if(msg.equalsIgnoreCase("society")){
            send("Sucks.");
        }
        if(msg.equalsIgnoreCase("doom")){
            send("Is what we're all waiting for.");
        }
        if(msg.equalsIgnoreCase("Molly")){
            if(roll(100) > 50){
                send("Is the best cat!");
            }else{
                send("Is our god.");
            }
        }
//        if(msg.getContentDisplay().compareTo("Test yes") == 0){
//            System.out.println("equals");
//        }else if(msg.getContentDisplay().compareTo("Test yes") > 0){
//            System.out.println("more");
//        }else if(msg.getContentDisplay().compareTo("Test yes") < 0){
//            System.out.println("less");
//        }

        if(msg.equalsIgnoreCase("Lucy stop")){
            send("Ok");
        }
    }

    private void send(String str){
        chan.sendMessage(str).submit();
    }

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event)
    {
        System.out.println(event.getReaction().getEmoji());
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
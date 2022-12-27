package org.darkpaster;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.utils.cache.SortedSnowflakeCacheView;
import org.darkpaster.utils.Random;
import org.darkpaster.utils.StringEditor;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.*;

import static org.darkpaster.Bot.send;
import static org.darkpaster.utils.Random.randomString;

public abstract class Russian extends Bot{
    private static final String GREETING_REGEX = "(((к+у+)+|х+а+й+|п+р+и+в+е*т*|з*д+а*р+о+(?:в|у)+а*|х+е+л+о+у*|з+д+р+а+в+с+т+в+у+й+)+!*\\)*)+";
    private static final String BOT_NAME = "(л+ю+с+(?:я|и)+|л+ю+с+ь+к+а+|л+ю+с+е+ч+?ь*к+а+)+(,)?"; // \!*\)*
    private static final String IDU_REGEX = "[а-я0-9ё\\s]+";
    private static final String SEARCH_REGEX = "[^а-яА-ЯёЁ\\s\\-—]";

    private static String lMsg = msg.toLowerCase();

    public static void general() {
        lMsg = msg.toLowerCase();
        if(lMsg.startsWith("!")){
            commands();
        }else{
            russianChatting();
        }
    }

    private static void commands(){
        if(lMsg.startsWith("!значение")) {
            try {
                Document doc = getPage("https://ru.wikipedia.org/wiki/" + StringEditor.cutStr(lMsg, "!значение".length()).trim());
                doc.outputSettings().charset("UTF-8");
                Elements mainInfoDiv = doc.select("p");
                Elements anotherInfo = doc.select("ul");
                StringBuilder str = new StringBuilder();
                for (Element el : mainInfoDiv) {
                    if (str.length() > 1500) {
                        break;
                    }
                    str.append(el.text()).append("\n");
                    //str.append(el.toString().replaceAll(SEARCH_REGEX, "").replaceAll("   ", "").replaceAll("--", ""));
                }
                for(Element inf: anotherInfo){
                    if(str.length() > 800){
                        break;
                    }
                    str.append(inf.text()).append("\n");
                }
                if(str.length() > 1999){
                    send(String.valueOf(str.substring(0, 1998)));
                }else{
                    send(String.valueOf(str));
                }
            }catch (Exception e){
                e.printStackTrace();
                send("Не найдено.");
                return;
            }
        }

        if(lMsg.startsWith("!изображение")){
            try {
                Document doc = getPage("https://www.google.com/search?tbm=isch&q=" + StringEditor.cutStr(lMsg, "!изображение".length()).trim());
                doc.outputSettings().charset("UTF-8");
                Elements images = doc.select("img[src]");
                System.out.println(images.size());
                String[] imgList = new String[images.size() - 1];
                for(int i = 0; i < imgList.length; i++){
                    imgList[i] = images.get(i + 1).attr("abs:src");
                }
                System.out.println(Arrays.toString(imgList));
                send(Random.randomString(imgList));
            }catch (Exception e){
                e.printStackTrace();
                send("Не найдено.");
                return;
            }
        }

        if(lMsg.startsWith("!синонимы")){
            try {
                Document doc = getPage("https://kartaslov.ru/синонимы-к-слову/" + StringEditor.cutStr(lMsg, "!синонимы".length()).trim());
                doc.outputSettings().charset("UTF-8");
                Elements words = doc.select("ul.v2-syn-list.v2-syn-head-list");
                StringBuilder stringBuilder = new StringBuilder();
                for(Element word: words){
                    stringBuilder.append(word.text());
                    if(stringBuilder.length() > 1500){
                        break;
                    }
                }
                send(String.valueOf(stringBuilder));
            }catch (Exception e){
                e.printStackTrace();
                send("Не найдено.");
                return;
            }
        }

        if(lMsg.equals("!статистика")){
            send("Не робит пока.");
//            MessageHistory history = MessageHistory.getHistoryFromBeginning(chan).complete();
//            SortedSnowflakeCacheView<TextChannel> channels = guild.getTextChannelCache();
//            //List<Message> messages = ;
//            boolean pass = true;
//            int index = 0;
        }

    }

    private static void downloadImage(String strImageURL){

        //get file name from image path
        String strImageName =
                strImageURL.substring( strImageURL.lastIndexOf("/") + 1 );

        System.out.println("Saving: " + strImageName + ", from: " + strImageURL);

        try {

            //open the stream from URL
            URL urlImage = new URL(strImageURL);
            InputStream in = urlImage.openStream();

            byte[] buffer = new byte[4096];
            int n = -1;

            OutputStream os =
                    new FileOutputStream("D:projects/" + strImageName );

            //write bytes to the output stream
            while ( (n = in.read(buffer)) != -1 ){
                os.write(buffer, 0, n);
            }

            //close the stream
            os.close();

            System.out.println("Image saved");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    static Document getPage(String url){
        try {
            return Jsoup.connect(url)
                    .userAgent("Chrome/4.0.249.0 Safari/532.5")
                    .referrer("http://www.google.com")
                    .timeout(10000)
                    .get();
            //return Jsoup.parse(new URL(url).openStream(), "UTF-8", url);
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    private static void russianChatting(){

        if(lexerBooleanOne(lMsg, BOT_NAME)){
            toLucy();
            return;
        }

        if(lMsg.equals("берсерк") || lMsg.equals("берсерк.")){
            send(msg);
        }
        if(msg.equals("Сестра") || msg.equals("СЕСТРА")){
            send("Брат");
        }
        if(msg.equals("24") || msg.equals("25")){
            send(")");
        }

    }

    private static void toLucy(){
        String noNameMsg = msg.replaceAll(BOT_NAME, "").trim();
        if(noNameMsg.contains(" или ")) {
            String[] cases = noNameMsg.split(" или ");
            send(randomString(cases));
        }else if(lexerBooleanOne(noNameMsg, GREETING_REGEX)) {
            greeting(lexerGetMatch(noNameMsg, GREETING_REGEX, 1));
        }else{
            send(randomString("а?", "каво", "што..."));
        }
    }


    private static void greeting(String hi){
        send(hi);

    }

    private static boolean lexerBoolean(String input, String regex){
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        return matcher.matches();
    }

    private static boolean lexerBooleanRvrs(String input, String regex){
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        return !matcher.matches();
    }

    private static String lexerGetMatch(String input, String regex, int i){
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        if(matcher.find()) {
            return matcher.group(i);
        }else{
            return " ";
        }

    }

    private static boolean lexerBooleanOne(String input, String regex){
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        return matcher.find();
    }

    private static String lexerGetMatch(String input, String regex, String find){
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        matcher.matches();
        return matcher.group(find);
    }

    private static boolean lexerBoolean(String input, String regex, int find){
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        return matcher.find(find);
    }


    private static int lexerNumMatches(String input, String regex){
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        return matcher.groupCount();
    }


    private static String lexerGetMatches(String input, String regex){
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        matcher.matches();
        try {
            return matcher.group();
        } catch (Exception e) {
            return "";
        }
    }

    private static String lexerGetMatchesResult(String input, String regex){
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        matcher.find();
        matcher.matches();
        try {
            return matcher.toMatchResult().group();
        } catch (Exception e) {
            return "";
        }
    }
    //            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
//                try {
//                    // + Random.randomString(new String[]{"картошка", "капуста", "морковь", "свекла", "яблоко"})
//                    Desktop.getDesktop().browse(new URI("https://www.google.com/search?q=" + Random.randomString(new String[]{"апельсин", "ананас", "арбуз",
//                            "груша", "яблоко", "банан", "мандарин", "черешня", "вишня", "виноград"})));
//                } catch (IOException | URISyntaxException e) {
//                    throw new RuntimeException(e);
//                }
//            }else{
//                System.out.println("неа");
//            }
//        }
    //                URL chrome = new URL("https://ru.wikipedia.org/wiki/" + StringEditor.cutStr(lMsg, "!значение".length()).trim());
//                URLConnection uc = chrome.openConnection();
//                uc.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
//                uc.connect();
//                BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
//                String inputLine;
}

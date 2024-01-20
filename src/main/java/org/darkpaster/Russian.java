package org.darkpaster;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.utils.cache.SortedSnowflakeCacheView;
import org.darkpaster.utils.Random;
import org.darkpaster.utils.StringEditor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.dv8tion.jda.internal.utils.IOUtil.silentClose;
import static org.darkpaster.Bot.send;
import static org.darkpaster.utils.Random.randomString;

public abstract class Russian extends Bot{
    private static final String GREETING_REGEX = "(((к+у+)+|х+а+й+|п+р+и+в+е*т*|з*д+а*р+о+(?:в|у)+а*|х+е+л+о+у*|з+д+р+а+в+с+т+в+у+й+)+!*\\)*)+";
    private static final String BOT_NAME = "(г+а+о+(?:с|с)+|г+а+о+с|г+а+о+с)+(,)?"; // \!*\)*
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

    private static void commands() {
        if (lMsg.startsWith("!значение")) {
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
                for (Element inf : anotherInfo) {
                    if (str.length() > 800) {
                        break;
                    }
                    str.append(inf.text()).append("\n");
                }
                if (str.length() > 1999) {
                    send(String.valueOf(str.substring(0, 1998)));
                } else {
                    send(String.valueOf(str));
                }
            } catch (Exception e) {
                e.printStackTrace();
                send("Не найдено.");
                return;
            }
        }
        if (lMsg.startsWith("!изображение") || lMsg.startsWith("!img")) {
            try {
                String blya = lMsg.startsWith("!изображение") ? "!изображение" : "!img";
                String searchWord = StringEditor.cutStr(lMsg, blya.length()).trim();
                Document doc = getPage("https://www.google.com/search?tbm=isch&q=" + searchWord);
                doc.outputSettings().charset("UTF-8");
                Elements references = doc.select("a[href]");
                references.removeIf(el -> el.attr("abs:href").contains("search?"));//url?q=
                references.removeIf(el -> !el.attr("abs:href").contains("www.google.com/url?q="));//url?q=
                references.removeIf(el -> el.attr("abs:href").contains("support.google.com"));//url?q=
                references.removeIf(el -> el.attr("abs:href").contains("accounts.google.com"));//url?q=
                System.out.println(references.size());
                String[] rfrList = new String[references.size()];
                for (int i = 0; i < rfrList.length; i++) {
                    rfrList[i] = references.get(i).attr("abs:href");
                }
                String imgURL = Random.randomString(rfrList);
                System.out.println(imgURL);
                Document doc2 = getPage(imgURL);
                doc2.outputSettings().charset("UTF-8");
                Elements images = doc2.select("img[src]");
                for (Element el : images) {
                    String url = images.get(Random.Int(images.size() - 1)).attr("abs:src");
                    boolean dot = url.charAt(url.length() - 3) == '.' || url.charAt(url.length() - 4) == '.' || url.charAt(url.length() - 2) == '.';
                    try {
                        int width = Integer.parseInt(el.attr("width"));
                        int height = Integer.parseInt(el.attr("height"));
                        if ((url.endsWith(".jpg") || url.endsWith(".png") || !dot)
                                && (width > 300 && height > 300) || (el.attr("alt").toLowerCase().contains(searchWord) || url.toLowerCase().contains(searchWord))) {
                            send(url);
                            break;
                        }
                    } catch (Exception e) {
                        if ((url.endsWith(".jpg")) || !dot && (el.attr("alt").toLowerCase().contains(searchWord) || url.toLowerCase().contains(searchWord))) {
                            send(url);
                            break;
                        }
                    }
                }
                //send(doc2.selectFirst("img[src]").attr("abs:src"));
            } catch (Exception e) {
                e.printStackTrace();
                send("Не найдено.");
                return;
            }

        }
        if (lMsg.startsWith("!синонимы")) {
            try {
                Document doc = getPage("https://kartaslov.ru/синонимы-к-слову/" + StringEditor.cutStr(lMsg, "!синонимы".length()).trim());
                doc.outputSettings().charset("UTF-8");
                Elements words = doc.select("ul.v2-syn-list.v2-syn-head-list");
                StringBuilder stringBuilder = new StringBuilder();
                for (Element word : words) {
                    stringBuilder.append(word.text());
                    if (stringBuilder.length() > 1500) {
                        break;
                    }
                }
                send(String.valueOf(stringBuilder));
            } catch (Exception e) {
                e.printStackTrace();
                send("Не найдено.");
                return;
            }
        }
//            if (lMsg.startsWith("!изображение") || lMsg.startsWith("!img")) {
//                try {
//                    String blya = lMsg.startsWith("!изображение") ? "!изображение" : "!img";
//                    String searchWord = StringEditor.cutStr(lMsg, blya.length()).trim();
//                    Document doc = getPage("https://ya.ru/images/search?from=tabbar&text=" + searchWord);
//                    doc.outputSettings().charset("UTF-8");
//                    //System.out.println(doc);
//                    System.out.println(doc);
//                    Elements references = doc.select("img[src]");
//                    //references.removeIf(el -> el.attr("abs:href").contains("search?"));//url?q=
//                    //references.removeIf(el -> el.attr("abs:href").contains("https://s.pinimg.com/webapp"));//url?q=
//                    //references.removeIf(el -> !el.attr("abs:href").contains("http://enable-javascript.com/"));
//                    //references.removeIf(el -> el.attr("abs:href").contains("support.google.com"));//url?q=
//                    //references.removeIf(el -> el.attr("abs:href").contains("accounts.google.com"));//url?q=
//
//                    System.out.println(references.size());
//                    System.out.println(references.toString());
//                    //System.out.println(references.toString());
//                    String[] rfrList = new String[references.size()];
//                    for (int i = 0; i < rfrList.length; i++) {
//                        rfrList[i] = references.get(i).attr("abs:src");
//                    }
//                    String imgURL = Random.randomString(rfrList);
//                    System.out.println(imgURL);
////                    Document doc2 = getPage(imgURL);
////                    doc2.outputSettings().charset("UTF-8");
////                    Elements images = doc2.select("img[src]");
////                    for (Element el : images) {
////                        String url = images.get(Random.Int(images.size() - 1)).attr("abs:src");
////                        boolean dot = url.charAt(url.length() - 3) == '.' || url.charAt(url.length() - 4) == '.' || url.charAt(url.length() - 2) == '.';
////                        try {
////                            int width = Integer.parseInt(el.attr("width"));
////                            int height = Integer.parseInt(el.attr("height"));
////                            if ((url.endsWith(".jpg") || url.endsWith(".png") || !dot)
////                                    && (width > 300 && height > 300) || (el.attr("alt").toLowerCase().contains(searchWord) || url.toLowerCase().contains(searchWord))) {
////                                send(url);
////                                break;
////                            }
////                        } catch (Exception e) {
////                            if ((url.endsWith(".jpg")) || !dot && (el.attr("alt").toLowerCase().contains(searchWord) || url.toLowerCase().contains(searchWord))) {
////                                send(url);
////                                break;
////                            }
////                        }
////                    }
//                    sendQue(imgURL);
//                    return;
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    send("Не найдено.");
//                    return;
//                }
//            }

        if (lMsg.equals("!обновить статистику")) {
            handleSaveConversation(eventMsg);
            //read(new File(handleSaveConversation(eventMsg)));
        }
        if(lMsg.equals("!статистика")){
            ArrayList<String> botsMessages = new ArrayList<>();
            ArrayList<String> haykMessages = new ArrayList<>();
            ArrayList<String> humanMessages = new ArrayList<>();
            File stat = new File(chan.getName()+".txt");
            if(!stat.exists()){
                sendQue("У этого канала нет статистики.");
                return;
            }
            String[] res = read(stat).split("∫");
            for(String m: res){
                if(m.startsWith("humanhu") || m.startsWith("Rest In Peace")){
                    humanMessages.add(m);
                }else if(m.startsWith("haykmund")){
                    haykMessages.add(m);
                }else{
                    botsMessages.add(m);
                }
            }
            sendQue("Статистика канала " + chan.getName() + ":\nВсего сообщений: "+res.length+"\nСообщений от Human: "
            +humanMessages.size()+"\nСообщений от Haykmund: "+haykMessages.size()+"\nОстальные сообщения: "+botsMessages.size());
        }
    }
    static RestAction<?> latestUpdate = null;

    private static void handleSaveConversation(MessageReceivedEvent event) {
        String fileName = event.getChannel().getName() + ".txt";
        String filePath = "" + fileName;

        final Writer fileWriter;
        try {
            fileWriter = new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8);
            //fileWriter = new FileWriter(filePath);
            //System.out.println(fileWriter.getEncoding());
        } catch (IOException e) {
            e.printStackTrace();
            event.getChannel().sendMessage("Failed to save message due to error getting file writer: " + e.getMessage()).queue();
            return;
        }

        Message progressMessage;
        try {
            // Using .complete for simplicity, but could also be moved to queue and use its callback.
            progressMessage = event.getChannel().sendMessage(getProgressInfo(filePath, 0)).complete();
        }
        catch (Exception e) {
            e.printStackTrace();
            silentClose(fileWriter);
            return;
        }

        int MAX_DESIRED = 400000;
        List<Message> messages = new ArrayList<Message>();
        event.getChannel().getIterableHistory()
                .forEachAsync(message -> {
                    messages.add(message);
                    handleProgressUpdate(progressMessage, filePath, messages.size());
                    return messages.size() < MAX_DESIRED;
                })
                .thenAccept(_ignored -> {
                    Collections.reverse(messages);
                    for (Message message : messages) {
                        // Don't save the progress message as it isn't part of chat.
                        if (message.getIdLong() == progressMessage.getIdLong()) {
                            continue;
                        }

                        try {
                            fileWriter.write(message.getAuthor().getName() + "√" + message.getContentRaw() + "∫");
                        }
                        catch (IOException e) {
                            // Rethrow as a RuntimeException so that the .exceptioally(...) will tell user what happened
                            throw new RuntimeException(e);
                        }
                    }
                    event.getChannel().sendMessage("Conversation was successfully saved.").queue();
                })
                .exceptionally(error -> {
                    error.printStackTrace();
                    event.getChannel().sendMessage("Conversation saving failed due to error: " + error.getMessage()).queue();
                    return null;
                })
                .whenComplete((_ignored, _ignored2) -> {
                    // Set latestUpdate to null to try and prevent any updates being sent now that we're done
                    // as we're about to delete the progressMessage we've been updating.
                    latestUpdate = null;
                    progressMessage.delete().queue();
                    silentClose(fileWriter);
                });
    }

    private static void handleProgressUpdate(Message progressMessage, String filePath, int totalMessages) {
        RestAction<?> action = progressMessage.editMessage(getProgressInfo(filePath, totalMessages));
        latestUpdate = action;

        action.setCheck(() -> {
            // Only send out the latest progress update.
            return action == latestUpdate;
        });
        //Submit is similar to .queue(...), but gives easier access to a "finally" like method.
        action.submit().whenComplete((_ignored, _ignored2) -> {
            // Clear the latest update if we just sent it.
            if (latestUpdate == action) {
                latestUpdate = null;
            }
        });
    }

    public static void silentClose(AutoCloseable closeable)
    {
        try
        {
            closeable.close();
        }
        catch (Exception ignored) {}
    }

    private static String getProgressInfo(String filePath, int totalMessages) {
        String message = "";
        message += "Processing conversation to file:\n";
        message += "`" + filePath + "`\n\n";
        message += "Total messages retrieved thus far: " + totalMessages;

        return message;
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
            toGaos();
            return;
        }

        if(lMsg.equals("берсерк") || lMsg.equals("берсерк.")){
            send(msg);
        }
        if(msg.equals("Брат") && prevMsg.equals("Брат") || msg.equals("БРАТ") && prevMsg.equals("БРАТ")){
            send(msg);
        }
        if(msg.equals("24") || msg.equals("25")){
            send("да");
        }

    }

    private static void toGaos(){
        String noNameMsg = msg.replaceAll(BOT_NAME, "").trim();
        if(noNameMsg.contains(" или ")) {
            String[] cases = noNameMsg.replaceAll("[^а-яА-ЯёЁ -]", "").split(" ");
            ArrayList<String> words = new ArrayList<>();
            for (int j = 0; j < cases.length; j++) {
                if(cases[j].equalsIgnoreCase("или")){
                    if(!words.contains(cases[j - 1])){
                        words.add(cases[j - 1]);
                    }
                    if(!words.contains(cases[j + 1])){
                        words.add(cases[j + 1]);
                    }
                }
            }
            send(randomString(words));
        }else if(lexerBooleanOne(noNameMsg, GREETING_REGEX)) {
            greeting(lexerGetMatch(noNameMsg, GREETING_REGEX, 1));
        }else{
            message.reply(Random.randomString(Bot.usersMessages)).submitAfter(1, TimeUnit.SECONDS);
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
    public static String read(File file) {
        StringBuilder content = new StringBuilder();
        try {
            Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
            for (int i = 0; i < file.length(); i++) {
                char c;
                c = (char) reader.read();
                //if(file.getName().equals("input.txt")) System.out.println(c);;
                content.append(c);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content.toString();
    }

    private static void write(File file, String content) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(file);
            writer.write(content);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
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

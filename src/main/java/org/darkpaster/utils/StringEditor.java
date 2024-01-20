package org.darkpaster.utils;

//import jdk.nashorn.internal.runtime.regexp.joni.Regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringEditor {

    public static String cutStr(String s){
        int i = s.indexOf(" ");
        return s.substring(i + 1).replaceAll(" ", "");
    }

    public static String cutStr(String s, int startAt){
        return s.substring(startAt + 1).trim();
    }

    public static boolean findMatches(String input){
        Pattern pattern = Pattern.compile(".");
        Matcher matcher = pattern.matcher(input);
        return matcher.matches();
    }


}

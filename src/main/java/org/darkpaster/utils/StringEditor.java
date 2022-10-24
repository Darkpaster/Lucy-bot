package org.darkpaster.utils;

public class StringEditor {

    public static String cutStr(String s){
        int i = s.indexOf(" ");
        return s.substring(i + 1).replaceAll(" ", "");
    }
}

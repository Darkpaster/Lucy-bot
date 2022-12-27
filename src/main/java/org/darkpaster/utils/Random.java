package org.darkpaster.utils;

public class Random {

    public static int Int(int max) {
        max++;
        return max > 0 ? (int) (Math.random() * ((double) max)) : 0;
    }

    public static int Int(int min, int max) {
        return ((int) (Math.random() * ((double) (max - min)))) + min;
    }

    public static float Float(float max) {
        return (float) (Math.random() * ((float) max));
    }

    public static float Float() {
        return (float) Math.random();
    }

    public static int IntRange(int min, int max) {
        return ((int) (Math.random() * ((double) ((max - min) + 1)))) + min;
    }

    public static int NormalIntRange(int min, int max) {
        return ((int) (((Math.random() + Math.random()) * ((double) ((max - min) + 1))) / 2.0d)) + min;
    }

    public static String randomString(String... strings){
        return strings[Int(strings.length - 1)];
    }

}

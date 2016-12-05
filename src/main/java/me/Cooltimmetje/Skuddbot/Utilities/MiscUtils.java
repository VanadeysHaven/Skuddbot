package me.Cooltimmetje.Skuddbot.Utilities;

import java.text.SimpleDateFormat;
import java.util.Random;

/**
 * Created by Tim on 8/4/2016.
 */
public class MiscUtils {

    public static int randomInt(int min, int max) {
        Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;
    }

    public static boolean isInt(String str){
        try {
            int num = Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static String randomString(int len){
        final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random rnd = new Random();

        StringBuilder sb = new StringBuilder( len );
        for( int i = 0; i < len; i++ )
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }

    public static String formatTime(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd'd' HH'h' mm'm' ss'm'");
        return sdf.format(time - (3600 * 24 * 1000));
    }

    public static String flipText(String input){
        String normal = "abcdefghijklmnopqrstuvwxyz_,;.?!'()[]{}";
        String split  = "ɐqɔpǝɟbɥıɾʞlɯuodbɹsʇnʌʍxʎz‾'؛˙¿¡,)(][}{";
//maj
        normal += "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        split  += "∀qϽᗡƎℲƃHIſʞ˥WNOԀὉᴚS⊥∩ΛMXʎZ";
//number
        normal += "0123456789";
        split  += "0ƖᄅƐㄣϛ9ㄥ86";

        char letter;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i< input.length(); i++) {
            letter = input.charAt(i);

            int a = normal.indexOf(letter);
            sb.append((a != -1) ? split.charAt(a) : letter);
        }
        return sb.reverse().toString();
    }
}

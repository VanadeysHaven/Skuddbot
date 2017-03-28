package me.Cooltimmetje.Skuddbot.Utilities;

import me.Cooltimmetje.Skuddbot.Enums.DataTypes;
import me.Cooltimmetje.Skuddbot.Main;
import sx.blah.discord.handle.obj.Status;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

/**
 * Some useful utilities I can use throughout the code.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.3.01-ALPHA
 * @since v0.1-ALPHA
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

    /**
     * (╯°□°）╯︵ ¿uoıʇɐuıɐldxǝ uɐ pǝǝu ʎllɐǝɹ sıɥʇ sǝop
     *
     * @param input input
     * @return ʇnduı
     */
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

    /**
     * Normalize a string.
     *
     * @param input The string we want to be normalized.
     * @return The normalized string.
     */
    public static String normalizeString(String input){ //
        String characters = "𝟎𝟐𝓑𝓪𝓮𝐢𝒌𝓵𝓻𝒕";
        String fixedChars = "02Baeiklrt";

        char letter;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i< input.length(); i++) {
            letter = input.charAt(i);

            int a = characters.indexOf(letter);
            sb.append((a != -1) ? fixedChars.charAt(a) : letter);
        }
        return sb.toString();
    }

    public static String reverse(String input){
        return new StringBuilder(input).reverse().toString();
    }

    /**
     * Gets a random message from the awesome message pool of the given type.
     *
     * @param type Type required.
     * @return The selected message.
     */
    public static String getRandomMessage(DataTypes type){
        boolean rightType = false;
        String selectedMessage = "null";
        while (!rightType) {
            Random generator = new Random();
            Object[] values = Constants.awesomeStrings.values().toArray();
            int selected = generator.nextInt(values.length);
            DataTypes dataType = (DataTypes) values[selected];
            if(dataType == type){
                rightType = true;
                selectedMessage = (String) Constants.awesomeStrings.keySet().toArray()[selected];
            }
        }

        return selectedMessage;
    }

    public static void setPlaying(){
        DateFormat dateFormat = new SimpleDateFormat("dd/MM");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());

        switch (date) {

            //Birthdays
            case "21/10":
                Main.getInstance().getSkuddbot().changeStatus(Status.game("HAPPY BIRTHDAY TIMMY!"));
                Constants.CURRENT_EVENT = "It's someone's birthday! HAPPY BIRTHDAY!";
                Constants.EVENT_ACTIVE = true;
                break;
            case "18/03":
                Main.getInstance().getSkuddbot().changeStatus(Status.game("HAPPY BIRTHDAY RAY!"));
                Constants.CURRENT_EVENT = "It's someone's birthday! HAPPY BIRTHDAY!";
                Constants.EVENT_ACTIVE = true;
                break;
            case "30/07":
                Main.getInstance().getSkuddbot().changeStatus(Status.game("HAPPY BIRTHDAY MELSH!"));
                Constants.CURRENT_EVENT = "It's someone's birthday! HAPPY BIRTHDAY!";
                Constants.EVENT_ACTIVE = true;
                break;
            case "03/10":
                Main.getInstance().getSkuddbot().changeStatus(Status.game("HAPPY BIRTHDAY JESSICA!"));
                Constants.CURRENT_EVENT = "It's someone's birthday! HAPPY BIRTHDAY!";
                Constants.EVENT_ACTIVE = true;
                break;

            //Seasonal events
            case "24/12":
            case "25/12":
            case "26/12":
                Main.getInstance().getSkuddbot().changeStatus(Status.game(MiscUtils.getRandomMessage(DataTypes.PLAYING_CHRISTMAS)));
                Constants.CURRENT_EVENT = "It's Christmas time!";
                Constants.EVENT_ACTIVE = true;
                break;
            case "01/01":
                Main.getInstance().getSkuddbot().changeStatus(Status.game(MiscUtils.getRandomMessage(DataTypes.PLAYING_NEW_YEAR)));
                Constants.CURRENT_EVENT = "HAPPY NEW YEAR!";
                Constants.EVENT_ACTIVE = true;
                break;

            //Default
            default:
                Main.getInstance().getSkuddbot().changeStatus(Status.game(MiscUtils.getRandomMessage(DataTypes.PLAYING)));
                Constants.EVENT_ACTIVE = false;
                break;
        }

    }
}

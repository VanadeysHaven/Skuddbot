package me.Cooltimmetje.Skuddbot.Utilities;

import me.Cooltimmetje.Skuddbot.Enums.Avatars;
import me.Cooltimmetje.Skuddbot.Enums.DataTypes;
import me.Cooltimmetje.Skuddbot.Main;
import me.Cooltimmetje.Skuddbot.Profiles.MySqlManager;
import me.Cooltimmetje.Skuddbot.Profiles.Server;
import sx.blah.discord.handle.obj.ActivityType;
import sx.blah.discord.handle.obj.StatusType;
import sx.blah.discord.util.Image;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Some useful utilities I can use throughout the code.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.4.51-ALPHA
 * @since v0.1-ALPHA
 */
public class MiscUtils {

    private static Random rand = new Random();

    public static int randomInt(int min, int max) {
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

    public static boolean isLong(String str){
        try {
            long num = Long.parseLong(str);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public static String randomString(int len){
        final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random rnd = new Random();

        StringBuilder sb = new StringBuilder( len );
        for( int i = 0; i < len; i++ )
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }

    public static String randomStringWithChars(int len){
        final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvqxyz`~!@#$%^&*()-_+=/*[]{};:\"'?.,<>";
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

    /**
     * Reverses any given string, will add a ZWS when it's not whitelisted and the origin is Twitch.
     *
     * @param input The string we want reversed
     * @param twitch If the string originates from Twitch
     * @return The reversed string with a ZWS when applicable.
     */
    public static String reverse(String input, boolean twitch){
        String reversed = new StringBuilder(input).reverse().toString();

        if(!Constants.whitelistedCommands.contains(reversed.split(" ")[0])){
            reversed = "\u200B" + reversed;
        }

        return reversed;
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

    public static HashMap<String,Long> lastShown = new HashMap<>();

    public static boolean randomCheck(String string) {
        if ((Constants.awesomeStrings.size() * 0.75) < lastShown.size()) {
            lastShown.clear();
        }
        boolean allowed = false;

        if(!lastShown.containsKey(string)){
            allowed = true;
        } else {
            if((System.currentTimeMillis() - lastShown.get(string)) < 24*60*60*1000){
                allowed = MiscUtils.randomInt(1,4) == 1;
            } else {
                allowed = true;
            }
        }

        if(allowed) {
            lastShown.put(string, System.currentTimeMillis());
        }
        return allowed;
    }

    public static void setPlaying(boolean startup){

        DateFormat dateFormat = new SimpleDateFormat("dd/MM");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());
        String[] strings = date.split("/");
        int[] dates = {Integer.parseInt(strings[0]), Integer.parseInt(strings[1])};

        if(startup) {
            Main.getInstance().getSkuddbot().changePresence(StatusType.ONLINE, ActivityType.PLAYING,Constants.config.get("version") + " | " + Constants.config.get("branch") + " > " + Constants.config.get("deployed_from"));
            Constants.CURRENT_EVENT = "The bot has just started up, give it a min, alright?";
            Constants.EVENT_ACTIVE = true;
        } else {
            //Playing status
            switch (date) {
                //Birthdays
                case "18/03":
                    Main.getInstance().getSkuddbot().changePresence(StatusType.ONLINE, ActivityType.PLAYING,"HAPPY BIRTHDAY RAY!");
                    Constants.CURRENT_EVENT = "It's someone's birthday! HAPPY BIRTHDAY!";
                    Constants.EVENT_ACTIVE = true;
                    break;
                case "21/03":
                    Main.getInstance().getSkuddbot().changePresence(StatusType.ONLINE, ActivityType.PLAYING,"HAPPY BIRTHDAY EMBERS!");
                    Constants.CURRENT_EVENT = "It's someone's birthday! HAPPY BIRTHDAY!";
                    Constants.EVENT_ACTIVE = true;
                    break;
                case "21/06":
                    Main.getInstance().getSkuddbot().changePresence(StatusType.ONLINE, ActivityType.PLAYING,"HAPPY BIRTHDAY BATTLEKILLER!");
                    Constants.CURRENT_EVENT = "It's someone's birthday! HAPPY BIRTHDAY!";
                    Constants.EVENT_ACTIVE = true;
                    break;
                case "18/07":
                    Main.getInstance().getSkuddbot().changePresence(StatusType.ONLINE, ActivityType.PLAYING,"HAPPY BIRTHDAY LIZ!");
                    Constants.CURRENT_EVENT = "It's someone's birthday! HAPPY BIRTHDAY!";
                    Constants.EVENT_ACTIVE = true;
                    break;
                case "27/07":
                    Main.getInstance().getSkuddbot().changePresence(StatusType.ONLINE, ActivityType.PLAYING,"HAPPY BIRTHDAY FIDDY!");
                    Constants.CURRENT_EVENT = "It's someone's birthday! HAPPY BIRTHDAY!";
                    Constants.EVENT_ACTIVE = true;
                    break;
                case "30/07":
                    Main.getInstance().getSkuddbot().changePresence(StatusType.ONLINE, ActivityType.PLAYING,"HAPPY BIRTHDAY MELSH!");
                    Constants.CURRENT_EVENT = "It's someone's birthday! HAPPY BIRTHDAY!";
                    Constants.EVENT_ACTIVE = true;
                    break;
                case "01/08":
                    Main.getInstance().getSkuddbot().changePresence(StatusType.ONLINE, ActivityType.PLAYING,"HAPPY BIRTHDAY DEFECTIUS!");
                    Constants.CURRENT_EVENT = "It's someone's birthday! HAPPY BIRTHDAY!";
                    Constants.EVENT_ACTIVE = true;
                    break;
                case "08/08":
                    Main.getInstance().getSkuddbot().changePresence(StatusType.ONLINE, ActivityType.PLAYING,"HAPPY BIRTHDAY IAIN!");
                    Constants.CURRENT_EVENT = "It's someone's birthday! HAPPY BIRTHDAY!";
                    Constants.EVENT_ACTIVE = true;
                    break;
                case "25/08":
                    Main.getInstance().getSkuddbot().changePresence(StatusType.ONLINE, ActivityType.PLAYING,"HAPPY BIRTHDAY NAKOR!");
                    Constants.CURRENT_EVENT = "It's someone's birthday! HAPPY BIRTHDAY!";
                    Constants.EVENT_ACTIVE = true;
                    break;
                case "01/10":
                    Main.getInstance().getSkuddbot().changePresence(StatusType.ONLINE, ActivityType.PLAYING,"HAPPY BIRTHDAY TGM!");
                    Constants.CURRENT_EVENT = "It's someone's birthday! HAPPY BIRTHDAY!";
                    Constants.EVENT_ACTIVE = true;
                    break;
                case "02/10":
                    Main.getInstance().getSkuddbot().changePresence(StatusType.ONLINE, ActivityType.PLAYING,"HAPPY BIRTHDAY THOMAS!");
                    Constants.CURRENT_EVENT = "It's someone's birthday! HAPPY BIRTHDAY!";
                    Constants.EVENT_ACTIVE = true;
                    break;
                case "03/10":
                    Main.getInstance().getSkuddbot().changePresence(StatusType.ONLINE, ActivityType.PLAYING,"HAPPY BIRTHDAY JESSICA!");
                    Constants.CURRENT_EVENT = "It's someone's birthday! HAPPY BIRTHDAY!";
                    Constants.EVENT_ACTIVE = true;
                    break;
                case "21/10":
                    Main.getInstance().getSkuddbot().changePresence(StatusType.ONLINE, ActivityType.PLAYING,"HAPPY BIRTHDAY TIMMY!");
                    Constants.CURRENT_EVENT = "It's someone's birthday! HAPPY BIRTHDAY!";
                    Constants.EVENT_ACTIVE = true;
                    break;
                case "04/11":
                    Main.getInstance().getSkuddbot().changePresence(StatusType.ONLINE, ActivityType.PLAYING,"HAPPY BIRTHDAY LAM!");
                    Constants.CURRENT_EVENT = "It's someone's birthday! HAPPY BIRTHDAY!";
                    Constants.EVENT_ACTIVE = true;
                    break;
                case "28/11":
                    Main.getInstance().getSkuddbot().changePresence(StatusType.ONLINE, ActivityType.PLAYING,"HAPPY BIRTHDAY SCROOGE!");
                    Constants.CURRENT_EVENT = "It's someone's birthday! HAPPY BIRTHDAY!";
                    Constants.EVENT_ACTIVE = true;
                    break;
                case "02/12":
                    Main.getInstance().getSkuddbot().changePresence(StatusType.ONLINE, ActivityType.PLAYING,"HAPPY BIRTHDAY LOCKSTAR!");
                    Constants.CURRENT_EVENT = "It's someone's birthday! HAPPY BIRTHDAY!";
                    Constants.EVENT_ACTIVE = true;
                    break;
                case "03/12":
                    Main.getInstance().getSkuddbot().changePresence(StatusType.ONLINE, ActivityType.PLAYING,"HAPPY BIRTHDAY MEERY!");
                    Constants.CURRENT_EVENT = "It's someone's birthday! HAPPY BIRTHDAY!";
                    Constants.EVENT_ACTIVE = true;
                    break;

                //Seasonal events
                case "01/01":
                    Main.getInstance().getSkuddbot().changePresence(StatusType.ONLINE, ActivityType.PLAYING,MiscUtils.getRandomMessage(DataTypes.PLAYING_NEW_YEAR));
                    Constants.CURRENT_EVENT = "HAPPY NEW YEAR!";
                    Constants.EVENT_ACTIVE = true;
                    break;

                case "24/12":
                case "25/12":
                case "26/12":
                    Main.getInstance().getSkuddbot().changePresence(StatusType.ONLINE, ActivityType.PLAYING,MiscUtils.getRandomMessage(DataTypes.PLAYING_CHRISTMAS));
                    Constants.CURRENT_EVENT = "It's Christmas time!";
                    Constants.EVENT_ACTIVE = true;
                    break;

                case "31/12":
                    int SECONDS_IN_A_DAY = 24 * 60 * 60;
                    Calendar thatDay = Calendar.getInstance();
                    thatDay.setTime(new Date(0)); /* reset */
                    thatDay.set(Calendar.DAY_OF_MONTH,1);
                    thatDay.set(Calendar.MONTH,0); // 0-11 so 1 less
                    thatDay.set(Calendar.YEAR, 2020);

                    Calendar today = Calendar.getInstance();
                    long diff =  thatDay.getTimeInMillis() - today.getTimeInMillis();
                    long diffSec = diff / 1000;

                    long secondsDay = diffSec % SECONDS_IN_A_DAY;
                    long hours = (secondsDay / 3600); // % 24 not needed

                    Main.getInstance().getSkuddbot().changePresence(StatusType.ONLINE, ActivityType.PLAYING,hours + " hours remain...");
                    Constants.CURRENT_EVENT = "The countdown has begun...";
                    Constants.EVENT_ACTIVE = true;
                    break;

                //Default
                default:
                    Main.getInstance().getSkuddbot().changePresence(StatusType.ONLINE, ActivityType.PLAYING, MiscUtils.getRandomMessage(DataTypes.PLAYING).replace("$version", Constants.config.get("version")));
                    Constants.EVENT_ACTIVE = false;
                    break;
            }
        }

        Avatars avatar = Avatars.DEFAULT;
        if(Constants.CURRENT_EVENT.contains("HAPPY BIRTHDAY") && Constants.EVENT_ACTIVE){
            avatar = Avatars.PARTY;
        } else if((dates[0] >= 1) && (dates[0] <= 26) && (dates[1] == 12)){
            avatar = Avatars.CHRISTMAS;
        } else if ((dates[0] == 1) && (dates[1] == 4)){
            avatar = Avatars.MEME;
        } else if (Main.getInstance().getSkuddbot().getOurUser().getStringID().equals("224553721210732544")){
            avatar = Avatars.WIP;
        }

        if(avatar != Avatars.valueOf(Constants.config.get("avatar"))){
            Main.getInstance().getSkuddbot().changeAvatar(Image.forUrl("png", avatar.getUrl()));
            Constants.config.put("avatar", avatar.toString());
            MySqlManager.saveGlobal("avatar", avatar.toString());
        }

    }

    public static ArrayList<Long> gatherActiveUsers(Server server){
        return gatherActiveUsers(server,24 * 60 * 60 * 1000);
    }

    public static ArrayList<Long> gatherActiveUsers(Server server, int activeDelay){
        ArrayList<Long> activeUsers = new ArrayList<>();
        for (Long userid : server.lastSeen.keySet()) {
            if ((System.currentTimeMillis() - server.lastSeen.get(userid) < activeDelay)) {
                activeUsers.add(userid);
            }
        }
        return activeUsers;
    }

    public static boolean isNumeric(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static String glueStrings(String prefix, String defGlue, String lastGlue, String suffix, String[] strings){
        if(strings.length == 1) return prefix + strings[0] + suffix;
        StringBuilder sb = new StringBuilder();

        int currentIndex = 0;
        for(String str : strings){
            if (currentIndex == 0) {
                sb.append(prefix).append(strings[currentIndex]).append(defGlue);
            } else if(currentIndex == strings.length - 1) {
                sb.append(strings[currentIndex]).append(suffix);
            } else if(currentIndex == strings.length - 2) {
                sb.append(strings[currentIndex]).append(lastGlue);
            } else {
                sb.append(strings[currentIndex]).append(defGlue);
            }
            currentIndex++;
        }

        return sb.toString();
    }
}

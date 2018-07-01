package me.Cooltimmetje.Skuddbot.Utilities;

import me.Cooltimmetje.Skuddbot.Main;
import sx.blah.discord.handle.obj.IEmoji;
import sx.blah.discord.handle.obj.IGuild;

import java.text.MessageFormat;
import java.util.HashMap;

/**
 * This class helps with custom Emoji's.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.4.31-ALPHA
 * @since v0.4.31-ALPHA
 */
public class EmojiHelper {

    public static HashMap<String,String> emojis = new HashMap<>();

    public static void loadEmoji(){
        Logger.info("Loading custom emoji...");

        IGuild guild = Main.getInstance().getSkuddbot().getGuildByID(Constants.HOME_SERVER);
        for(IEmoji emoji : guild.getEmojis()){
            emojis.put(emoji.getName(), MessageFormat.format("<:{0}:{1}>", emoji.getName(), emoji.getStringID()));
            Logger.info(MessageFormat.format("Emoji \"{0}\" added. (ID: {1})", emoji.getName(), emoji.getStringID()));
        }
    }

    public static String getEmoji(String emoji){
        return emojis.getOrDefault(emoji, MessageFormat.format("[unknown emoji: {0}]", emoji));
    }

}

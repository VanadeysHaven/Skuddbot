package me.Cooltimmetje.Skuddbot.Utilities;

import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.GuildEmoji;
import discord4j.core.object.util.Snowflake;
import me.Cooltimmetje.Skuddbot.Main;

import java.text.MessageFormat;
import java.util.HashMap;

/**
 * This class helps with custom Emoji's.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.5.1-ALPHA
 * @since v0.4.31-ALPHA
 */
public class EmojiHelper {

    public static HashMap<String,String> emojis = new HashMap<>();

    public static void loadEmoji(){
        Logger.info("Loading custom emoji...");

        Guild guild = Main.getInstance().getSkuddbot().getGuildById(Snowflake.of(Constants.HOME_SERVER)).block();
        for(GuildEmoji emoji : guild.getEmojis().collectList().block()){
            emojis.put(emoji.getName(), emoji.asFormat());
            Logger.info(MessageFormat.format("Emoji \"{0}\" added. (ID: {1})", emoji.getName(), emoji.getId().asString()));
        }
    }

    public static String getEmoji(String emoji){
        return emojis.getOrDefault(emoji, MessageFormat.format("[unknown emoji: {0}]", emoji));
    }

}

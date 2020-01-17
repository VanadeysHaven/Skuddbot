package me.Cooltimmetje.Skuddbot.Commands.Useless;

import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.GuildEmoji;
import discord4j.core.object.entity.Message;
import me.Cooltimmetje.Skuddbot.Enums.EmojiEnum;
import me.Cooltimmetje.Skuddbot.Profiles.MySqlManager;
import me.Cooltimmetje.Skuddbot.Utilities.Constants;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import me.Cooltimmetje.Skuddbot.Utilities.MiscUtils;

import java.util.HashMap;
import java.util.List;

/**
 * o7
 *
 * @author Tim (Cooltimmetje)
 * @version v0.5.1-ALPHA
 * @since v0.4.01-ALPHA-DEV
 */
//TODO: add cooldown manager
public class SaluteCommand {

    static HashMap<String,Long> cooldown = new HashMap<>();

    public static void run(Message message) {
        Guild guild = message.getGuild().block();
        List<GuildEmoji> emojis = guild.getEmojis().collectList().block();

        if(message.getContent().get().endsWith("-tc") && Constants.adminUser.contains(message.getAuthor().get().getId().asString())){
            boolean newState = !Boolean.parseBoolean(Constants.config.get("salute_cooldown"));
            Constants.config.put("salute_cooldown", newState+"");
            MessagesUtils.addReaction(message, "Cooldown toggled: " + newState, EmojiEnum.WHITE_CHECK_MARK, false);
            MySqlManager.saveGlobal("salute_cooldown", newState+"");
        } else {
            if(cooldown.containsKey(message.getAuthor().get().getId().asString())){
                if(System.currentTimeMillis() - cooldown.get(message.getAuthor().get().getId().asString()) < 30000){
                    if(Boolean.parseBoolean(Constants.config.get("salute_cooldown"))) {
                        return;
                    }
                }
            }

            int chance = MiscUtils.randomInt(0, 100);
            if (chance <= 5 && guild.getId().asLong() == 198483566026424321L) {
                MessagesUtils.sendPlain(MiscUtils.randomStringWithChars(MiscUtils.randomInt(10, 50)) + emojis.get(MiscUtils.randomInt(0, emojis.size())).toString() + MiscUtils.randomStringWithChars(MiscUtils.randomInt(10, 50)) + "**7**" + MiscUtils.randomStringWithChars(MiscUtils.randomInt(10, 50)), message.getChannel().block(), false);
            } else {
                MessagesUtils.sendPlain(((emojis.size() == 0) ? ("o") : (emojis.get(MiscUtils.randomInt(0, emojis.size())).asFormat())) + "7", message.getChannel().block(), false);
            }

            cooldown.put(message.getAuthor().get().getId().asString(), System.currentTimeMillis());
        }
    }

}


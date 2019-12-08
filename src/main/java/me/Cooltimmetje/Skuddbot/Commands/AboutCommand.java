package me.Cooltimmetje.Skuddbot.Commands;

import discord4j.core.object.entity.Message;
import discord4j.core.spec.EmbedCreateSpec;
import me.Cooltimmetje.Skuddbot.Utilities.Constants;
import me.Cooltimmetje.Skuddbot.Utilities.MiscUtils;

import java.awt.*;
import java.util.function.Consumer;

/**
 * This class shows some information about the bot, such as the current branch, commit, and the manual.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.3-ALPHA
 * @since v0.2-ALPHA
 */
public class AboutCommand {

    /**
     * CMD: This shows some information about Skuddbot.
     *
     * @param message This is the message that triggered the command.
     */
    public static void run(Message message){
        Consumer<EmbedCreateSpec> template = spec -> {
            spec.setAuthor("Skuddbot " + Constants.config.get("version"), null, "http://i.imgur.com/v1vlVru.png");
            spec.setThumbnail("http://i.imgur.com/v1vlVru.png");
            spec.setColor(new Color(MiscUtils.randomInt(0,255), MiscUtils.randomInt(0,255), MiscUtils.randomInt(0,255)));

            spec.addField("__Deployed on:__", Constants.config.get("deployed_on"), true);
            spec.addField("__Built on:__", Constants.config.get("built_on"), true);
            spec.addField("__Branch:__", Constants.config.get("branch"), true);
            spec.addField("__Deployed from:__", "`" + Constants.config.get("deployed_from") + "`", true);
            spec.addField("__Users in memory:__", Constants.PROFILES_IN_MEMORY+"", true);
            spec.addField("__Manual:__", Constants.config.get("manual"), true);
            spec.addField("__Privacy Statement:__", Constants.config.get("privacy_statement"), true);
            spec.addField("__Changelog:__", Constants.config.get("changelog"), true);
        };

        message.getChannel().block().createMessage(msgSpec -> {
            msgSpec.setEmbed(template);
        }).block();
    }

}

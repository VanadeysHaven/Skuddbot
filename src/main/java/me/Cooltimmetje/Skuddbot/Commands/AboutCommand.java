package me.Cooltimmetje.Skuddbot.Commands;

import me.Cooltimmetje.Skuddbot.Utilities.Constants;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import sx.blah.discord.handle.obj.IMessage;

import java.text.MessageFormat;

/**
 * Created by Tim on 10/2/2016.
 */
public class AboutCommand {

    /**
     * CMD: This shows some information about Skuddbot.
     *
     * @param message This is the message that triggered the command.
     */
    public static void run(IMessage message){

        MessagesUtils.sendPlain(MessageFormat.format("**Skuddbot** | `{0}`\n```\n" +
                "Deployed on:     {1}\n" +
                "Built on:        {2}\n" +
                "Branch:          {3}\n" +
                "Deployed from:   {4}\n" +
                "Build ID:        {5}\n\n" +

                "Users in memory: {6}\n\n" +

                "Manual:          {7}\n" +
                "Changelog:       {8}" +
                "\n```",
                Constants.config.get("version"), Constants.config.get("deployed_on"), Constants.config.get("built_on"), Constants.config.get("branch"), Constants.config.get("deployed_from"),
                Constants.config.get("build_id"), Constants.PROFILES_IN_MEMORY, Constants.config.get("manual"), Constants.config.get("changelog")), message.getChannel());

    }

}

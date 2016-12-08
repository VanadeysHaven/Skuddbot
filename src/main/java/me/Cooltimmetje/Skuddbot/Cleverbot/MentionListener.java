package me.Cooltimmetje.Skuddbot.Cleverbot;

import me.Cooltimmetje.Skuddbot.Main;
import me.Cooltimmetje.Skuddbot.Profiles.Server;
import me.Cooltimmetje.Skuddbot.Profiles.ServerManager;
import me.Cooltimmetje.Skuddbot.Utilities.Constants;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.MentionEvent;
import sx.blah.discord.handle.obj.IMessage;

/**
 * This manages the input from users for the cleverbot.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.2-ALPHA
 * @since v0.1-ALPHA
 */
public class MentionListener {

    @EventSubscriber
    public void onMention(MentionEvent event){
        Server server = ServerManager.getServer(event.getMessage().getGuild().getID());
        if(server.getCleverbotChannel() != null){
            if(event.getMessage().getChannel().getID().equals(server.getCleverbotChannel())) {
                if (event.getMessage().getContent().startsWith(Constants.SKUDDBOT_MENTION)) {
                    if (!event.getMessage().mentionsEveryone() || !event.getMessage().getAuthor().isBot()) {
                        IMessage message = event.getMessage();
                        String msgContent = message.getContent();
                        if (msgContent.split(" ").length >= 2) {
                            message.getChannel().toggleTypingStatus();
                            String input = event.getMessage().getContent().replace(Constants.SKUDDBOT_MENTION, " ").trim();
                            MessagesUtils.sendPlain(":speech_balloon: " + Main.getCleverskudd().getOutput(input, server), message.getChannel());
                        }
                    }
                }
            }
        }
    }



}

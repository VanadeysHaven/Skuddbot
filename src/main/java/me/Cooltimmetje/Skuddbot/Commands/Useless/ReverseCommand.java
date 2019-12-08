package me.Cooltimmetje.Skuddbot.Commands.Useless;

import discord4j.core.object.entity.Message;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import me.Cooltimmetje.Skuddbot.Utilities.MiscUtils;

/**
 * This will reverse the input.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.5.1-ALPHA
 * @since v0.3-ALPHA
 */
public class ReverseCommand {

    public static void run(Message message){
        String[] args = message.getContent().get().split(" ");
        String input;
        if(args.length > 1){
            StringBuilder sb = new StringBuilder();
            int mentionCount = 0;
            for(int i=1; i < args.length; i++) {
                if (message.getUserMentions().collectList().block().size() != 0) {
                    if (message.getUserMentions().collectList().block().get(mentionCount).getMention().replace("<@!", "<@").equals(args[i].replace("<@!", "<@"))) {
                        sb.append("@").append(message.getUserMentions().collectList().block().get(mentionCount).asMember(message.getGuild().block().getId()).block().getDisplayName());
                        mentionCount++;
                    } else {
                        sb.append(args[i]).append(" ");
                    }
                } else {
                    sb.append(args[i]).append(" ");
                }
            }
            input = sb.toString().trim();
        } else {
            input = "You didn't input any text D:";
        }

        MessagesUtils.sendPlain(MiscUtils.reverse(input, false), message.getChannel().block(), false);
    }

}

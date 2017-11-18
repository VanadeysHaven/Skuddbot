package me.Cooltimmetje.Skuddbot.Commands;

import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import me.Cooltimmetje.Skuddbot.Utilities.MiscUtils;
import sx.blah.discord.handle.obj.IMessage;

/**
 * This will reverse the input.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.5-ALPHA-DEV
 * @since v0.3-ALPHA
 */
public class ReverseCommand {

    public static void run(IMessage message){
        String[] args = message.getContent().split(" ");
        String input;
        if(args.length > 1){
            StringBuilder sb = new StringBuilder();
            int mentionCount = 0;
            for(int i=1; i < args.length; i++) {
                if (message.getMentions().size() != 0) {
                    if (message.getMentions().get(mentionCount).mention().replace("<@!", "<@").equals(args[i].replace("<@!", "<@"))) {
                        if(message.getMentions().get(mentionCount).getNicknameForGuild(message.getGuild()) != null){
                            sb.append("@").append(message.getMentions().get(mentionCount).getNicknameForGuild(message.getGuild()));
                        } else {
                            sb.append("@").append(message.getMentions().get(mentionCount).getName());
                        }
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

        MessagesUtils.sendPlain(MiscUtils.reverse(input, false), message.getChannel(), false);
    }

}

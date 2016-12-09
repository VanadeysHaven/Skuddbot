package me.Cooltimmetje.Skuddbot.Commands;

import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import me.Cooltimmetje.Skuddbot.Utilities.MiscUtils;
import sx.blah.discord.handle.obj.IMessage;

/**
 * (╯°□°）╯︵ uoıʇɐʇuǝɯnɔop pǝǝu ʇ,usǝop sıɥʇ
 *
 * @author Tim (Cooltimmetje)
 * @version v0.3-ALPHA
 * @since v0.2-ALPHA
 */
public class FlipTextCommand {

    public static void run(IMessage message){
        String[] args = message.getContent().split(" ");
        String input;
        if(args.length > 1){
            StringBuilder sb = new StringBuilder();
            int mentionCount = 0;
            for(int i=1; i < args.length; i++) {
                if (message.getMentions().size() != 0) {
                    if (message.getMentions().get(mentionCount).mention().replace("<@!", "<@").equals(args[i].replace("<@!", "<@"))) {
                        sb.append("@").append(message.getMentions().get(mentionCount).getNicknameForGuild(message.getGuild()).isPresent() ?
                                message.getMentions().get(mentionCount).getNicknameForGuild(message.getGuild()).get() : message.getMentions().get(mentionCount).getName()).append(" ");
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

        MessagesUtils.sendPlain("(╯°□°）╯︵ " + MiscUtils.flipText(input), message.getChannel());
    }

}

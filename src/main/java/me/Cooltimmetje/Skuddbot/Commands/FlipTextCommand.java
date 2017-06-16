package me.Cooltimmetje.Skuddbot.Commands;

import me.Cooltimmetje.Skuddbot.Main;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import me.Cooltimmetje.Skuddbot.Utilities.MiscUtils;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

import java.util.Objects;

/**
 * (╯°□°）╯︵ uoıʇɐʇuǝɯnɔop pǝǝu ʇ,usǝop sıɥʇ
 *
 * @author Tim (Cooltimmetje)
 * @version v0.4.01-ALPHA-DEV
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
                        IUser user = message.getMentions().get(mentionCount);
                        if(Objects.equals(user.getStringID(), Main.getInstance().getSkuddbot().getOurUser().getStringID())){
                            user = message.getAuthor();
                        }
                        sb.append("@").append(user.getDisplayName(message.getGuild()));
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

        MessagesUtils.sendPlain("(╯°□°）╯︵ " + MiscUtils.flipText(input), message.getChannel(), false);
    }

}

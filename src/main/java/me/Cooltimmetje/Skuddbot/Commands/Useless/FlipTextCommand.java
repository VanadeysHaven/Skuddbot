package me.Cooltimmetje.Skuddbot.Commands.Useless;

import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import me.Cooltimmetje.Skuddbot.Main;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import me.Cooltimmetje.Skuddbot.Utilities.MiscUtils;

import java.util.Objects;

/**
 * (╯°□°）╯︵ uoıʇɐʇuǝɯnɔop pǝǝu ʇ,usǝop sıɥʇ
 *
 * @author Tim (Cooltimmetje)
 * @version v0.5.1-ALPHA
 * @since v0.2-ALPHA
 */
//TODO: REWRITE
public class FlipTextCommand {

    public static void run(Message message){
        String[] args = message.getContent().get().split(" ");
        String input;
        if(args.length > 1){
            StringBuilder sb = new StringBuilder();
            int mentionCount = 0;
            for(int i=1; i < args.length; i++) {
                if (message.getUserMentions().collectList().block().size() != 0) {
                    if (message.getUserMentions().collectList().block().get(mentionCount).getMention().replace("<@!", "<@").equals(args[i].replace("<@!", "<@"))) {
                        Member user = message.getUserMentions().collectList().block().get(mentionCount).asMember(message.getGuild().block().getId()).block();
                        if(Objects.equals(user.getId().asString(), Main.getInstance().getSkuddbot().getSelf().block().getId().asString())){
                            user = message.getAuthor().get().asMember(message.getGuild().block().getId()).block();
                        }
                        sb.append("@").append(user.getDisplayName());
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

        MessagesUtils.sendPlain("(╯°□°）╯︵ " + MiscUtils.flipText(input), message.getChannel().block(), false);
    }

}

package me.Cooltimmetje.Skuddbot.ServerSpecific.PogoGravesend;

import me.Cooltimmetje.Skuddbot.Enums.EmojiEnum;
import me.Cooltimmetje.Skuddbot.Profiles.MySqlManager;
import me.Cooltimmetje.Skuddbot.Profiles.Server;
import me.Cooltimmetje.Skuddbot.Profiles.ServerManager;
import me.Cooltimmetje.Skuddbot.Utilities.Constants;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.RequestBuffer;

/**
 * Command for setting up the message.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.4.61-ALPHA
 * @since v0.4.32-ALPHA
 */
public class SetupCommand {

    public static void run(IMessage message){
        if(message.getGuild().getLongID() != PogoConstants.serverID){
            return;
        }

        Server server = ServerManager.getServer(message.getGuild().getStringID());
        boolean allowAccess = false;
        if (server.getAdminRole() != null) {
            if (message.getGuild().getRolesByName(server.getAdminRole()).size() == 1) {
                allowAccess = message.getAuthor().getRolesForGuild(message.getGuild()).contains(message.getGuild().getRolesByName(server.getAdminRole()).get(0));
            }
        }
        if (!allowAccess){
            allowAccess = message.getAuthor() == message.getGuild().getOwner() || Constants.adminUser.contains(message.getAuthor().getStringID());
        }

        if(!allowAccess){
            return;
        }

        String[] args = message.getContent().split(" ");
        if(args.length <= 1){
            MessagesUtils.addReaction(message, "Not enough arguments. - Usage: `!pogo_setup <message ID>`", EmojiEnum.X, false);
            return;
        }
        long messageID;
        try {
            messageID = Long.parseLong(args[1]);
        } catch (NumberFormatException e){
            MessagesUtils.addReaction(message, "A message ID may only consist of numbers and is typically 18 numbers long.", EmojiEnum.X, false);
            return;
        }
        IMessage setupMessage = RequestBuffer.request(() -> message.getChannel().getMessageByID(messageID)).get();
        if(setupMessage == null){
            MessagesUtils.addReaction(message, "A message with that ID doesn't exist, try again.", EmojiEnum.X, false);
            return;
        }

        message.getChannel().setTypingStatus(true);
        RequestBuffer.request(setupMessage::removeAllReactions);
        for (TeamEnum team : TeamEnum.values()) {
            RequestBuffer.request(() -> setupMessage.addReaction(setupMessage.getGuild().getEmojiByID(team.getEmoji())));

        }

        Constants.config.put("pogo_message_id", messageID+"");
        MySqlManager.saveGlobal("pogo_message_id", messageID+"");
        message.delete();
        message.getChannel().setTypingStatus(false);
    }

}

package me.Cooltimmetje.Skuddbot.Minigames.FreeForAll;

import com.vdurmont.emoji.EmojiManager;
import me.Cooltimmetje.Skuddbot.Enums.EmojiEnum;
import me.Cooltimmetje.Skuddbot.Utilities.Logger;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionAddEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionRemoveEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.RequestBuffer;
import sx.blah.discord.util.RequestBuilder;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class FFAHandler {

    private String serverID;

    public FFAHandler(String serverID){
        this.serverID = serverID;
        Logger.info("Creating FFA handler for Server with ID: " + serverID);
    }

    public HashMap<IUser,Long> cooldowns = new HashMap<>();

    private ArrayList<IUser> entrants = new ArrayList<>();
    private IUser host = null;
    private IMessage messageSent;

    public void enter(IMessage message){
        if(message.getAuthor() == host){
            return;
        }

        entrants.add(message.getAuthor());
        if(host == null) {
            host = message.getAuthor();
            messageSent = MessagesUtils.sendPlain(MessageFormat.format("{0} **{1}** is looking to host a free for all fight, anyone can participate!\n" +
                            "Click the {0} reaction to enter, {1} can start the fight by clicking the {2} reaction.",
                    EmojiEnum.CROSSED_SWORDS.getString(), message.getAuthor().getDisplayName(message.getGuild()), EmojiEnum.WHITE_CHECK_MARK.getString()),
                    message.getChannel(), false);

            RequestBuffer.request(() -> {
                messageSent.addReaction(EmojiManager.getForAlias(EmojiEnum.CROSSED_SWORDS.getAlias()));
                messageSent.addReaction(EmojiManager.getForAlias(EmojiEnum.WHITE_CHECK_MARK.getAlias()));
            });
        }
    }


    public void reactionAdd(ReactionAddEvent event){
        if(event.getMessage() != messageSent){
            return;
        }
        String unicodeEmoji = event.getReaction().getEmoji().getName();

        if(EmojiEnum.getByUnicode(unicodeEmoji) == EmojiEnum.WHITE_CHECK_MARK){

        } else if (EmojiEnum.getByUnicode(unicodeEmoji) == EmojiEnum.CROSSED_SWORDS){

        }
    }

    public void reactionRemove(ReactionRemoveEvent event){
        if(event.getUser() == host){
            return;
        }
        if(event.getMessage() != messageSent){
            return;
        }
    }



}

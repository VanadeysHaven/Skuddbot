package me.Cooltimmetje.Skuddbot.Minigames.FreeForAll;

import com.vdurmont.emoji.EmojiManager;
import me.Cooltimmetje.Skuddbot.Enums.EmojiEnum;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionAddEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionRemoveEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class FFAHandler {

    private String serverID;

    public FFAHandler(String serverID){
        this.serverID = serverID;
    }

    public HashMap<IUser,Long> cooldowns = new HashMap<>();

    private ArrayList<IUser> entrants = new ArrayList<>();
    private IUser host = null;

    public void enter(IMessage message){
        entrants.add(message.getAuthor());
        if(host == null) {
            host = message.getAuthor();
            IMessage messageSent = MessagesUtils.sendPlain(MessageFormat.format("{0} **{1}** is looking to host a free for all fight, anyone can participate!\n" +
                    "Click the {0} reaction to enter, {1} can start the fight by clicking the {2} reaction.",
                    EmojiEnum.CROSSED_SWORDS.getString(), message.getAuthor().getDisplayName(message.getGuild()), EmojiEnum.WHITE_CHECK_MARK.getString()),
                    message.getChannel(), false);

            messageSent.addReaction(EmojiManager.getForAlias(EmojiEnum.CROSSED_SWORDS.getAlias()));
            messageSent.addReaction(EmojiManager.getForAlias(EmojiEnum.WHITE_CHECK_MARK.getAlias()));
        }
    }

    public void reactionAdd(ReactionAddEvent event){

    }

    public void reactionRemove(ReactionRemoveEvent event){

    }



}

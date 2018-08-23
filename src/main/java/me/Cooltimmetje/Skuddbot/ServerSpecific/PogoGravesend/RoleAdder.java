package me.Cooltimmetje.Skuddbot.ServerSpecific.PogoGravesend;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionAddEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionRemoveEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.RequestBuffer;

import java.util.List;

/**
 * Adds the role to people that click the reaction.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.4.32-ALPHA
 * @since v0.4.32-ALPHA
 */
public class RoleAdder {

    @EventSubscriber
    public void onReaction(ReactionAddEvent event){
        if(event.getMessage().getLongID() != PogoConstants.messageID){
            return;
        }
        IGuild guild = event.getMessage().getGuild();
        IMessage message = event.getMessage();
        IUser user = event.getUser();
        long emojiID = event.getReaction().getEmoji().getLongID();
        TeamEnum team = TeamEnum.getTeamByEmoji(emojiID);
        List<IRole> roles = event.getUser().getRolesForGuild(guild);

        TeamEnum currentTeam = null;
        for(IRole role : roles){
            TeamEnum teamCheck = TeamEnum.getTeamByRole(role.getLongID());
            if(teamCheck != null){
                currentTeam = teamCheck;
            }
        }
        final TeamEnum finalCurrentTeam = currentTeam;

        if(currentTeam != null) {
            RequestBuffer.request(() -> message.removeReaction(user, guild.getEmojiByID(finalCurrentTeam.getEmoji())));
            RequestBuffer.request(() -> user.removeRole(guild.getRoleByID(finalCurrentTeam.getRole())));
        }
        RequestBuffer.request(() -> user.addRole(guild.getRoleByID(team.getRole())));
    }

    @EventSubscriber
    public void onReactionRemove(ReactionRemoveEvent event){
        if(event.getMessage().getLongID() != PogoConstants.messageID){
            return;
        }
        long emojiID = event.getReaction().getEmoji().getLongID();
        TeamEnum team = TeamEnum.getTeamByEmoji(emojiID);
        RequestBuffer.request(() -> event.getUser().removeRole(event.getMessage().getGuild().getRoleByID(team.getRole())));
    }
}

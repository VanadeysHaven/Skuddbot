package me.Cooltimmetje.Skuddbot.Minigames.TeamDeathmatch;

import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;

/**
 * This class represents a team member that is a actual user.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.4.7-ALPHA
 * @since v0.4.7-ALPHA
 */
public class UserMember implements TeamMember {

    private IUser user;
    private IGuild guild;

    public UserMember(IUser user, IGuild guild){
        this.user = user;
        this.guild = guild;
    }

    @Override
    public String getName() {
        return user.getDisplayName(guild);
    }

    @Override
    public boolean isAI() {
        return false;
    }
}

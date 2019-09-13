package me.Cooltimmetje.Skuddbot.Minigames.TeamDeathmatch.Members;

import me.Cooltimmetje.Skuddbot.Profiles.ProfileManager;
import me.Cooltimmetje.Skuddbot.Profiles.SkuddUser;
import me.Cooltimmetje.Skuddbot.Utilities.EmojiHelper;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;

/**
 * This class represents a team member that is a actual user.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.4.7-ALPHA
 * @since v0.4.7-ALPHA
 */
public class UserMember extends TeamMember {

    private static final int WIN_REWARD = 100;
    private static final int KILL_REWARD = 50;
    private static final int SAVE_REWARD = 75;
    private static final int FULL_TEAM_ALIVE_BONUS = 300;

    private IUser user;
    private IGuild guild;

    public UserMember(IUser user, IGuild guild){
        this.user = user;
        this.guild = guild;
    }

    @Override
    public String getName(boolean withTeamNumber) {
        String name = user.getDisplayName(guild);
        if(withTeamNumber)
            name = "[" + team.getTeamNumber() + "] " + name;

        return name;
    }

    @Override
    public String getName() {
        return getName(false);
    }

    @Override
    public boolean isAI() {
        return false;
    }

    @Override
    public String getIdentifier() {
        return user.getStringID();
    }

    private int getXpReward(){
        int i = (kills * KILL_REWARD) + (saves * SAVE_REWARD);
        if(team.allMembersAlive())
            i += FULL_TEAM_ALIVE_BONUS;
        if(team.isWinner())
            i += WIN_REWARD;

        return i;
    }

    public void awardRewards(int playerCount){
        SkuddUser su = ProfileManager.getDiscord(user, guild, true);

        if(team.isWinner()){
            su.setTeamDeathmatchWins(su.getTeamDeathmatchWins() + 1);
        } else {
            su.setTeamDeathmatchLosses(su.getTeamDeathmatchLosses() + 1);
        }

        if(team.allMembersAlive())
            su.setTeamDeathmatchAllSurvived(su.getTeamDeathmatchAllSurvived() + 1);

        if(su.getTeamDeathmatchMostWin() < playerCount && team.isWinner()){
            su.setTeamDeathmatchMostWin(playerCount);
        }

        su.setXp(su.getXp() + getXpReward());
        su.setTeamDeathmatchKills(su.getTeamDeathmatchKills() + kills);
        su.setTeamDeathmatchSaves(su.getTeamDeathmatchSaves() + saves);
    }

    public String getRewardString(int playerCount){
        SkuddUser su = ProfileManager.getDiscord(user, guild, true);
        if(saves == 0 && kills == 0 && !team.isWinner()) return null;
        StringBuilder sb = new StringBuilder();

        sb.append(getName()).append(": ");

        if(team.isWinner())
            sb.append("**WINNER** +").append(WIN_REWARD).append(" ").append(EmojiHelper.getEmoji("xp_icon")).append(" - ");
        if(team.allMembersAlive())
            sb.append("**ALL TEAM MEMBERS SURVIVED** +").append(FULL_TEAM_ALIVE_BONUS).append(" ").append(EmojiHelper.getEmoji("xp_icon")).append(" - ");
        if(kills > 0)
            sb.append("**").append(kills).append(" kills** +").append(KILL_REWARD * kills).append(" ").append(EmojiHelper.getEmoji("xp_icon")).append(" - ");
        if(saves > 0)
            sb.append("**").append(saves).append(" defences** +").append(SAVE_REWARD * saves).append(" ").append(EmojiHelper.getEmoji("xp_icon")).append(" - ");
        if(su.getTeamDeathmatchMostWin() < playerCount && team.isWinner())
            sb.append("**New highest entrants win: **").append(playerCount).append( " entrants - ");

        sb.append("**TOTAL** ").append(getXpReward()).append(" ").append(EmojiHelper.getEmoji("xp_icon"));

        return sb.toString();
    }

    public long getID(){
        return user.getLongID();
    }
}

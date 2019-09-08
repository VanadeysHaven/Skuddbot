package me.Cooltimmetje.Skuddbot.Minigames.TeamDeathmatch.Members;

import lombok.Getter;
import lombok.Setter;
import me.Cooltimmetje.Skuddbot.Minigames.TeamDeathmatch.Team;

/**
 * This abstract class represents a team member.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.4.7-ALPHA
 * @since v0.4.7-ALPHA
 */
@Getter
@Setter
public abstract class TeamMember {

    private int kills;
    private int saves;
    private boolean alive;
    private Team team;

    public TeamMember() {
        this.kills = 0;
        this.saves = 0;
        this.alive = true;
    }

    public abstract String getName();

    public abstract boolean isAI();

    public abstract String getIdentifier();

    public void addKills(int amount){
        this.kills += amount;
    }

    public void addSaves(int amount){
        this.saves += amount;
    }

}

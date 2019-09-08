package me.Cooltimmetje.Skuddbot.Minigames.TeamDeathmatch.Members;

/**
 * This class represents a AI team member.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.4.7-ALPHA
 * @since v0.4.7-ALPHA
 */
public class AIMember extends TeamMember {

    private String name;

    public AIMember(String name){
        this.name = name;
    }

    @Override
    public String getName() {
        return "[AI] " + this.name;
    }

    @Override
    public boolean isAI() {
        return true;
    }

    @Override
    public String getIdentifier() {
        return this.name;
    }
}

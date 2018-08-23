package me.Cooltimmetje.Skuddbot.ServerSpecific.PogoGravesend;

import lombok.Getter;
import org.omg.CORBA.PUBLIC_MEMBER;

/**
 * Corresponding roles for the different emoji.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.4.32-ALPHA
 * @since v0.4.32-ALPHA
 */
@Getter
public enum TeamEnum {

    VALOR    (481827845178654732L, 482118677370503169L),
    MYSTIC   (481827845975310357L, 482119059115343874L),
    INSTINCT (481827845438439425L, 482119603758039050L);

    private long emoji;
    private long role;

    TeamEnum(long l, long l1){
        this.emoji = l;
        this.role = l1;
    }

    public static TeamEnum getTeamByEmoji(long id){
        for(TeamEnum team : TeamEnum.values()){
            if(team.getEmoji() == id){
                return team;
            }
        }

        return null;
    }

    public static TeamEnum getTeamByRole(long id){
        for(TeamEnum team : TeamEnum.values()){
            if(team.getRole() == id){
                return team;
            }
        }

        return null;
    }


}

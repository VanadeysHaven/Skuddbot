package me.Cooltimmetje.Skuddbot.Enums;

import lombok.Getter;

/**
 * Database stuff, to separate stuff from each other.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.3-ALPHA
 * @since v0.3-ALPHA-DEV
 */
@Getter
public enum DataTypes {

    PLAYING(128),
    PLAYING_CHRISTMAS(128),
    PLAYING_NEW_YEAR(128),
    ERROR(512),
    ALIVE(512);

    private int maxLength;

    DataTypes(int i){
        this.maxLength = i;
    }

}

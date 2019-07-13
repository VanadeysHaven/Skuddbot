package me.Cooltimmetje.Skuddbot.Enums;

import lombok.Getter;

/**
 * Defines account types.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.4.62-ALPHA
 * @since v0.4.62-ALPHA
 */
@Getter
public enum AccountType {

    DISCORD       ("Discord","D" ),
    TWITCH        ("Twitch", "T" ),
    DISCORD_TWITCH("Linked", "DT");

    private String fullName;
    private String abbreviation;

    AccountType(String fullName, String abbreviation){
        this.fullName = fullName;
        this.abbreviation = abbreviation;
    }

}
